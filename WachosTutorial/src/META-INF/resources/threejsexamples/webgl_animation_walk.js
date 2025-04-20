/* global THREE */

const scene = new THREE.Scene(); // Initialize the main scene to hold all 3D objects
const canvas = document.getElementById('@wachoscanvas'); // Get canvas element from HTML for rendering
const camera = new THREE.PerspectiveCamera(45, canvas.clientWidth / canvas.clientHeight, 0.1, 100); // Create camera with 45° FOV, dynamic aspect ratio, near/far planes
const renderer = new THREE.WebGLRenderer({ antialias: true }); // Initialize WebGL renderer with antialiasing for smoother edges
canvas.appendChild(renderer.domElement); // Append renderer's DOM element to the canvas

// Declare global variables for key objects
let floor, orbitControls, model, mixer, clock, skeleton;
let group, followGroup;
let actions;

// Configuration settings for animation and rendering
const settings = {
    show_skeleton: false, // Toggle skeleton visibility
    fixe_transition: true // Enable fixed animation transitions
};

// Mathematical constants for rotation calculations
const PI = Math.PI;
const PI90 = Math.PI / 2;

// Control state for character movement and animation
const controls = { 
    key: [0, 0, 0], // [forward/back, left/right, run]
    ease: new THREE.Vector3(), // Smooth movement vector
    position: new THREE.Vector3(), // Character position
    up: new THREE.Vector3(0, 1, 0), // Up direction for orientation
    rotate: new THREE.Quaternion(), // Rotation quaternion
    current: 'Idle', // Current animation state
    fadeDuration: 0.5, // Animation transition duration
    runVelocity: 5, // Speed when running
    walkVelocity: 1.8, // Speed when walking
    rotateSpeed: 0.05, // Rotation smoothing factor
    floorDecale: 0 // Floor texture offset
};

// Scene setup
scene.background = new THREE.Color(0x5e5d5d); // Set gray background
scene.fog = new THREE.Fog(0x5e5d5d, 2, 20); // Add fog for depth effect

// Camera setup
camera.position.set(0, 2, -5); // Position camera above and behind origin

// Renderer configuration
renderer.setSize(canvas.clientWidth, canvas.clientHeight, false); // Set renderer size to canvas dimensions
renderer.setPixelRatio(window.devicePixelRatio); // Match device pixel ratio for clarity
renderer.toneMapping = THREE.ACESFilmicToneMapping; // Apply cinematic tone mapping
renderer.toneMappingExposure = 0.5; // Adjust exposure for lighting
renderer.shadowMap.enabled = true; // Enable shadow rendering

// Orbit controls for camera interaction
orbitControls = new THREE.OrbitControls(camera, renderer.domElement);
orbitControls.target.set(0, 1, 0); // Focus camera on point above origin
orbitControls.enableDamping = true; // Smooth camera movement
orbitControls.enablePan = false; // Disable panning
orbitControls.maxPolarAngle = PI90 - 0.05; // Limit vertical rotation
orbitControls.update(); // Apply initial settings

clock = new THREE.Clock(); // Initialize clock for animation timing

init(); // Start initialization

// Initialize scene components
function init() {
    group = new THREE.Group(); // Create group for model
    scene.add(group); // Add group to scene

    followGroup = new THREE.Group(); // Create group for light and camera tracking
    scene.add(followGroup);

    // Add directional lighting
    const dirLight = new THREE.DirectionalLight(0xffffff, 5); // White light with intensity 5
    dirLight.position.set(-2, 5, -3); // Position light above and to the side
    dirLight.castShadow = true; // Enable shadows
    let cam = dirLight.shadow.camera; // Configure shadow camera
    cam.top = cam.right = 2;
    cam.bottom = cam.left = -2;
    cam.near = 3;
    cam.far = 8;
    dirLight.shadow.bias = -0.005; // Reduce shadow artifacts
    dirLight.shadow.radius = 4; // Soften shadow edges
    followGroup.add(dirLight);
    followGroup.add(dirLight.target); // Add light target for tracking

    // Load environment map
    new THREE.RGBELoader()
        .setPath('threejsexamples/') // Set path for assets
        .load(
            'lobe.hdr', // HDR environment map
            function (texture) {
                texture.mapping = THREE.EquirectangularReflectionMapping; // Set mapping type
                scene.environment = texture; // Apply environment map
                scene.environmentIntensity = 1.5; // Adjust intensity
                loadModel(); // Load character model
                addFloor(); // Add floor geometry
            },
            undefined,
            function (error) {
                console.error('Failed to load HDR:', error); // Log errors
            }
        );

    // Add event listeners
    window.addEventListener('resize', onWindowResize); // Handle window resizing
    document.addEventListener('keydown', onKeyDown); // Handle key presses
    document.addEventListener('keyup', onKeyUp); // Handle key releases
}

// Create floor geometry and lighting
function addFloor() {
    let size = 50; // Floor dimensions
    let repeat = 16; // Texture repeat count

    const maxAnisotropy = renderer.capabilities.getMaxAnisotropy(); // Get max texture anisotropy

    // Load and configure diffuse texture
    const floorT = new THREE.TextureLoader().load('threejsexamples/FloorsCheckerboard_S_Diffuse.jpg');
    floorT.colorSpace = THREE.SRGBColorSpace; // Set color space
    floorT.repeat.set(repeat, repeat); // Repeat texture
    floorT.wrapS = floorT.wrapT = THREE.RepeatWrapping; // Enable wrapping
    floorT.anisotropy = maxAnisotropy; // Improve texture clarity

    // Load and configure normal map
    const floorN = new THREE.TextureLoader().load('threejsexamples/FloorsCheckerboard_S_Normal.jpg');
    floorN.repeat.set(repeat, repeat);
    floorN.wrapS = floorN.wrapT = THREE.RepeatWrapping;
    floorN.anisotropy = maxAnisotropy;

    // Create floor material
    let mat = new THREE.MeshStandardMaterial({
        map: floorT, // Diffuse texture
        normalMap: floorN, // Normal map
        normalScale: new THREE.Vector2(0.5, 0.5), // Normal map intensity
        color: 0x404040, // Base color
        depthWrite: false, // Disable depth writing
        roughness: 0.85 // Surface roughness
    });

    // Create and rotate floor geometry
    let g = new THREE.PlaneGeometry(size, size, 50, 50);
    g.rotateX(-PI90); // Rotate to lie flat

    floor = new THREE.Mesh(g, mat); // Create floor mesh
    floor.receiveShadow = true; // Enable shadow reception
    scene.add(floor);

    controls.floorDecale = (size / repeat) * 4; // Calculate floor offset

    // Add point light
    const bulbGeometry = new THREE.SphereGeometry(0.05, 16, 8); // Small sphere for light
    let bulbLight = new THREE.PointLight(0xffee88, 2, 500, 2); // Warm light
    let bulbMat = new THREE.MeshStandardMaterial({ 
        emissive: 0xffffee, // Emissive color
        emissiveIntensity: 1, 
        color: 0x000000 // Black base
    });
    bulbLight.add(new THREE.Mesh(bulbGeometry, bulbMat)); // Add geometry to light
    bulbLight.position.set(1, 0.1, -3); // Position light
    bulbLight.castShadow = true; // Enable shadows
    floor.add(bulbLight); // Attach to floor
}

// Load character model and animations
function loadModel() {
    const loader = new THREE.GLTFLoader();
    loader.load(
        'threejsexamples/Soldier.glb', // GLTF model file
        function (gltf) {
            model = gltf.scene; // Get model scene
            group.add(model); // Add to group
            model.rotation.y = PI; // Rotate model 180°
            group.rotation.y = PI; // Rotate group 180°

            // Configure model materials
            model.traverse(function (object) {
                if (object.isMesh) {
                    if (object.name == 'vanguard_Mesh') { // Main character mesh
                        object.castShadow = true;
                        object.receiveShadow = true;
                        object.material.shadowSide = THREE.DoubleSide; // Double-sided shadows
                        object.material.metalness = 1.0; // Full metalness
                        object.material.roughness = 0.2; // Low roughness
                        object.material.color.set(1, 1, 1); // White color
                        object.material.metalnessMap = object.material.map; // Use diffuse as metalness map
                    } else { // Other meshes (e.g., accessories)
                        object.material.metalness = 1;
                        object.material.roughness = 0;
                        object.material.transparent = true;
                        object.material.opacity = 0.8;
                        object.material.color.set(1, 1, 1);
                    }
                }
            });

            // Add skeleton helper
            skeleton = new THREE.SkeletonHelper(model);
            skeleton.visible = false; // Hide by default
            scene.add(skeleton);

            // Set up animations
            const animations = gltf.animations;
            mixer = new THREE.AnimationMixer(model); // Create animation mixer
            actions = {
                Idle: mixer.clipAction(animations[0]), // Idle animation
                Walk: mixer.clipAction(animations[3]), // Walk animation
                Run: mixer.clipAction(animations[1]) // Run animation
            };

            // Configure animation weights
            for (let m in actions) {
                actions[m].enabled = true;
                actions[m].setEffectiveTimeScale(1);
                if (m !== 'Idle') actions[m].setEffectiveWeight(0); // Start with Idle active
            }

            actions.Idle.play(); // Play Idle animation

            animate(); // Start animation loop
        },
        undefined,
        function (error) {
            console.error('Failed to load GLTF:', error); // Log errors
        }
    );
}

// Update character movement and animation
function updateCharacter(delta) {
    if (!mixer) return; // Exit if mixer not initialized

    const fade = controls.fadeDuration; // Animation fade duration
    const key = controls.key; // Input state
    const up = controls.up; // Up vector
    const ease = controls.ease; // Movement vector
    const rotate = controls.rotate; // Rotation quaternion
    const position = controls.position; // Character position
    const azimut = orbitControls.getAzimuthalAngle(); // Camera angle

    let active = key[0] === 0 && key[1] === 0 ? false : true; // Check if moving
    let play = active ? (key[2] ? 'Run' : 'Walk') : 'Idle'; // Determine animation

    // Handle animation transitions
    if (controls.current != play) {
        const current = actions[play]; // New animation
        const old = actions[controls.current]; // Previous animation
        controls.current = play; // Update state

        if (settings.fixe_transition) { // Fixed transition mode
            current.reset();
            current.weight = 1.0;
            current.stopFading();
            old.stopFading();
            if (play !== 'Idle')
                current.time = old.time * (current.getClip().duration / old.getClip().duration); // Sync timing
            old._scheduleFading(fade, old.getEffectiveWeight(), 0); // Fade out old
            current._scheduleFading(fade, current.getEffectiveWeight(), 1); // Fade in new
            current.play();
        } else { // Standard transition
            setWeight(current, 1.0);
            old.fadeOut(fade);
            current.reset().fadeIn(fade).play();
        }
    }

    // Update movement
    if (controls.current !== 'Idle') {
        let velocity = controls.current == 'Run' ? controls.runVelocity : controls.walkVelocity; // Set speed
        ease.set(key[1], 0, key[0]).multiplyScalar(velocity * delta); // Calculate movement

        let angle = unwrapRad(Math.atan2(ease.x, ease.z) + azimut); // Compute rotation angle
        rotate.setFromAxisAngle(up, angle); // Set rotation

        controls.ease.applyAxisAngle(up, azimut); // Adjust movement direction

        position.add(ease); // Update position
        camera.position.add(ease); // Move camera

        group.position.copy(position); // Update model position
        group.quaternion.rotateTowards(rotate, controls.rotateSpeed); // Smooth rotation

        orbitControls.target.copy(position).add({ x: 0, y: 1, z: 0 }); // Update camera target
        followGroup.position.copy(position); // Move light group

        // Update floor position to follow character
        let dx = position.x - floor.position.x;
        let dz = position.z - floor.position.z;
        if (Math.abs(dx) > controls.floorDecale) floor.position.x += dx;
        if (Math.abs(dz) > controls.floorDecale) floor.position.z += dz;
    }

    mixer.update(delta); // Update animations
    orbitControls.update(); // Update camera controls
}

// Normalize angle to [-π, π]
function unwrapRad(r) {
    return Math.atan2(Math.sin(r), Math.cos(r));
}

// Set animation weight
function setWeight(action, weight) {
    action.enabled = true;
    action.setEffectiveTimeScale(1);
    action.setEffectiveWeight(weight);
}

// Handle key press events
function onKeyDown(event) {
    switch (event.code) {
        case 'ArrowUp':
        case 'KeyW':
        case 'KeyZ':
            controls.key[0] = -1; // Move forward
            break;
        case 'ArrowDown':
        case 'KeyS':
            controls.key[0] = 1; // Move backward
            break;
        case 'ArrowLeft':
        case 'KeyA':
        case 'KeyQ':
            controls.key[1] = -1; // Move left
            break;
        case 'ArrowRight':
        case 'KeyD':
            controls.key[1] = 1; // Move right
            break;
        case 'ShiftLeft':
        case 'ShiftRight':
            controls.key[2] = 1; // Run
            break;
    }
}

// Handle key release events
function onKeyUp(event) {
    switch (event.code) {
        case 'ArrowUp':
        case 'KeyW':
        case 'KeyZ':
            controls.key[0] = controls.key[0] < 0 ? 0 : controls.key[0]; // Stop forward
            break;
        case 'ArrowDown':
        case 'KeyS':
            controls.key[0] = controls.key[0] > 0 ? 0 : controls.key[0]; // Stop backward
            break;
        case 'ArrowLeft':
        case 'KeyA':
        case 'KeyQ':
            controls.key[1] = controls.key[1] < 0 ? 0 : controls.key[1]; // Stop left
            break;
        case 'ArrowRight':
        case 'KeyD':
            controls.key[1] = controls.key[1] > 0 ? 0 : controls.key[1]; // Stop right
            break;
        case 'ShiftLeft':
        case 'ShiftRight':
            controls.key[2] = 0; // Stop running
            break;
    }
}

// Handle window resizing
function onWindowResize() {
    camera.aspect = canvas.clientWidth / canvas.clientHeight; // Update aspect ratio
    camera.updateProjectionMatrix(); // Apply changes
    renderer.setSize(canvas.clientWidth, canvas.clientHeight, false); // Resize renderer
    renderer.setPixelRatio(window.devicePixelRatio); // Update pixel ratio
}

// Main animation loop
function animate() {
    requestAnimationFrame(animate); // Schedule next frame
    let delta = clock.getDelta(); // Get time delta
    updateCharacter(delta); // Update character state
    renderer.render(scene, camera); // Render scene
}