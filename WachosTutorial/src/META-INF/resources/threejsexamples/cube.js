/* global THREE */

//this very simple example demonstrates everything needed to use three.js with wachos
const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, 500 / 500, 0.1, 1000);  // Aspect ratio 500/500
const renderer = new THREE.WebGLRenderer();
const canvas = document.getElementById('@wachoscanvas'); // <-- important!

// Set renderer size to match the div size
renderer.setSize(canvas.clientWidth, canvas.clientHeight);
canvas.appendChild(renderer.domElement);

const geometry = new THREE.BoxGeometry();
const material = new THREE.MeshBasicMaterial({color: 0x00ff00});
const cube = new THREE.Mesh(geometry, material);
scene.add(cube);
camera.position.z = 5;

var lastX = 0;
var paused = false;

// Receive info from wachos
function receiveFromWachos(input) {
    console.log(input);
    paused = !paused;
}

function animate() {
    requestAnimationFrame(animate);
    if (!paused) {
        cube.rotation.x += 0.01;
        cube.rotation.y += 0.01;
        if (cube.rotation.x > lastX) {
            fireWachosEvent('x = ' + Math.round(cube.rotation.x)); //send info to wachos
            lastX += 5; //wait five degrees before posting again
        }
    }
    renderer.render(scene, camera);
}
animate();