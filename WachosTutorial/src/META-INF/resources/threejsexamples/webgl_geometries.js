/* global THREE */

const scene = new THREE.Scene(); //creates scene to hold objects
const camera = new THREE.PerspectiveCamera(45, 1, 1, 2000); //creates camera with 45-degree FOV, initial aspect 1, near 1, far 2000
const renderer = new THREE.WebGLRenderer({antialias: true}); //sets up WebGL renderer with antialiasing
const canvas = document.getElementById('@wachoscanvas'); //grabs canvas element from HTML

canvas.appendChild(renderer.domElement); //attaches renderer's DOM element to canvas

function updateSizes() { //defines function tocento sync renderer  renderer and camera sizes
    camera.aspect = canvas.clientWidth / canvas.clientHeight; //updates camera aspect ratio based on canvas dimensions
    camera.updateProjectionMatrix(); //applies new aspect ratio to camera
    renderer.setSize(canvas.clientWidth, canvas.clientHeight, false); //sets renderer size to match canvas, no CSS adjustment
    renderer.setPixelRatio(window.devicePixelRatio); //adjusts for high-DPI displays
}

updateSizes(); //runs initial size setup
camera.position.y = 400; //positions camera 400 units above origin

scene.add(new THREE.AmbientLight(0xcccccc, 1.5)); //adds ambient light with light gray color, intensity 1.5
camera.add(new THREE.PointLight(0xffffff, 2.5, 0, 0)); //adds point light to camera, white, intensity 2.5, no decay
scene.add(camera); //adds camera with light to scene

const texture = new THREE.TextureLoader().load('threejsexamples/uv_grid_opengl.jpg', t => { //loads texture and configures it
    t.wrapS = t.wrapT = THREE.RepeatWrapping; //sets texture to repeat horizontally and vertically
    t.anisotropy = 16; //enables anisotropic filtering with 16 samples
    t.colorSpace = THREE.SRGBColorSpace; //uses sRGB color space for accurate colors
});
const material = new THREE.MeshPhongMaterial({map: texture, side: THREE.DoubleSide}); //creates Phong material with texture, renders both sides

const objects = [ //array of objects with geometry and positions
    {geom: new THREE.SphereGeometry(75, 20, 10), pos: [-300, 0, 200]}, //sphere: radius 75, 20x10 segments
    {geom: new THREE.IcosahedronGeometry(75, 1), pos: [-100, 0, 200]}, //icosahedron: radius 75, detail 1
    {geom: new THREE.OctahedronGeometry(75, 2), pos: [100, 0, 200]}, //octahedron: radius 75, detail 2
    {geom: new THREE.TetrahedronGeometry(75, 0), pos: [300, 0, 200]}, //tetrahedron: radius 75, detail 0
    {geom: new THREE.PlaneGeometry(100, 100, 4, 4), pos: [-300, 0, 0]}, //plane: 100x100, 4x4 segments
    {geom: new THREE.BoxGeometry(100, 100, 100, 4, 4, 4), pos: [-100, 0, 0]}, //box: 100x100x100, 4x4x4 segments
    {geom: new THREE.CircleGeometry(50, 20, 0, Math.PI * 2), pos: [100, 0, 0]}, //circle: radius 50, 20 segments, full circle
    {geom: new THREE.RingGeometry(10, 50, 20, 5, 0, Math.PI * 2), pos: [300, 0, 0]}, //ring: inner 10, outer 50, 20x5 segments
    {geom: new THREE.CylinderGeometry(25, 75, 100, 40, 5), pos: [-300, 0, -200]}, //cylinder: top 25, bottom 75, height 100, 40x5 segments
    {geom: new THREE.LatheGeometry(Array.from({length: 50}, (_, i) =>
            new THREE.Vector2(Math.sin(i * 0.2) * Math.sin(i * 0.1) * 15 + 50, (i - 5) * 2)), 20), pos: [-100, 0, -200]}, //lathe: 50 points rotated 20 times
    {geom: new THREE.TorusGeometry(50, 20, 20, 20), pos: [100, 0, -200]}, //torus: radius 50, tube 20, 20x20 segments
    {geom: new THREE.TorusKnotGeometry(50, 10, 50, 20), pos: [300, 0, -200]} //torus knot: radius 50, tube 10, 50x20 segments
];

objects.forEach(({ geom, pos }) => { //loops through objects to create and position meshes
    const mesh = new THREE.Mesh(geom, material); //creates mesh with geometry and material
    mesh.position.set(...pos); //sets mesh position from array
    scene.add(mesh); //adds mesh to scene
});

renderer.setAnimationLoop(() => { //sets up continuous animation loop
    const timer = Date.now() * 0.0001; //calculates time for smooth animation
    camera.position.set(Math.cos(timer) * 800, 400, Math.sin(timer) * 800); //orbits camera around scene
    camera.lookAt(scene.position); //points camera at scene origin
    scene.traverse(obj => obj.isMesh && (obj.rotation.x = timer * 5, obj.rotation.y = timer * 2.5)); //rotates all meshes
    renderer.render(scene, camera); //renders scene with camera
});

window.addEventListener('resize', updateSizes); //updates sizes on window resize