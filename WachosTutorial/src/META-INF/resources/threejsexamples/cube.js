/* global THREE */

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, 500 / 500, 0.1, 1000);  // Aspect ratio 500/500
const renderer = new THREE.WebGLRenderer();
const canvas = document.getElementById('@wachoscanvas');

// Set renderer size to match the div size
renderer.setSize(canvas.clientWidth, canvas.clientHeight);
canvas.appendChild(renderer.domElement);

const geometry1 = new THREE.BoxGeometry();
const material1 = new THREE.MeshBasicMaterial({color: 0x00ff00});
const cube1 = new THREE.Mesh(geometry1, material1);
scene.add(cube1);
camera.position.z = 5;

function animate() {
    requestAnimationFrame(animate);
    cube1.rotation.x += 0.01;
    cube1.rotation.y += 0.01;
    renderer.render(scene, camera);
}
animate();