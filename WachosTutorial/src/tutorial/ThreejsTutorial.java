/**
 * The WACHOS software library is developed by the U.S. Department of Defense
 * (DoD).  It is made available to the public under the terms of the Apache
 * License, Version 2.0.
 *
 * Copyright (c) 2025, Naval Surface Warfare Center, Dahlgren Division.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Legal Notice: This software is subject to U.S. government licensing and
 * export control regulations. Unauthorized use, duplication, or distribution is
 * prohibited. All rights to this software are held by the U.S. Department of
 * Defense or its contractors.
 *
 * Patent Notice: This software may be subject to one or more patent
 * applications. Users of the software should ensure they comply with any
 * licensing or usage terms associated with the patent(s). For more
 * information, please refer to the patent application (Navy Case 109347,
 * 18/125,944).
 *
 * @author Clinton Winfrey
 * @version 1.0
 * @since 2025
 */
package tutorial;

import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.CodeSnippet;
import gov.mil.navy.nswcdd.wachos.components.text.ComboBox;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.Threejs;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.Arrays;

public class ThreejsTutorial {

    public static Tutorial create(WSession session) {

        //create a container for the Threejs object, and then add a Threejs object to it
        VBox threejsContainer = new VBox().setWidth("100%");
        threejsContainer.add(new Threejs("wjs/three.min.js", "threejsexamples/webgl_geometries.js").setWidth("100%").setHeight("400px"));

        //any time a selection occurs, load the associated threejs-based application
        //we don't need to reload threejs.min.js, because we loaded it a few lines ago
        ComboBox threeSelector = new ComboBox("Geometries", Arrays.asList("Geometries", "Soldier", "Cube"), selection -> {
            threejsContainer.removeAll();
            if (selection.equals("Soldier")) {
                threejsContainer.add(new Threejs("threejsexamples/webgl_animation_walk.js").setWidth("100%").setHeight("400px"));
            } else if (selection.equals("Geometries")) {
                threejsContainer.add(new Threejs("threejsexamples/webgl_geometries.js").setWidth("100%").setHeight("400px"));
            } else if (selection.equals("Cube")) {
                Threejs cube = new Threejs("threejsexamples/cube.js").setWidth("100%").setHeight("400px");
                cube.threejsListeners.add(event -> session.postInfo("Increased X", event)); //receive communication from threejs
                threejsContainer.add(new Button("Toggle Paused", action -> cube.threeExec("pressedTogglePaused"))); //send communication to threejs
                threejsContainer.add(cube);
                threejsContainer.add(new CodeSnippet("/* global THREE */\n"
                        + "\n"
                        + "//this very simple example demonstrates everything needed to use three.js with wachos\n"
                        + "const scene = new THREE.Scene();\n"
                        + "const camera = new THREE.PerspectiveCamera(75, 500 / 500, 0.1, 1000);  // Aspect ratio 500/500\n"
                        + "const renderer = new THREE.WebGLRenderer();\n"
                        + "const canvas = document.getElementById('@wachoscanvas'); // <-- important!\n"
                        + "\n"
                        + "// Set renderer size to match the div size\n"
                        + "renderer.setSize(canvas.clientWidth, canvas.clientHeight);\n"
                        + "canvas.appendChild(renderer.domElement);\n"
                        + "\n"
                        + "const geometry = new THREE.BoxGeometry();\n"
                        + "const material = new THREE.MeshBasicMaterial({color: 0x00ff00});\n"
                        + "const cube = new THREE.Mesh(geometry, material);\n"
                        + "scene.add(cube);\n"
                        + "camera.position.z = 5;\n"
                        + "\n"
                        + "var lastX = 0;\n"
                        + "var paused = false;\n"
                        + "\n"
                        + "// Receive info from wachos\n"
                        + "function receiveFromWachos(input) {\n"
                        + "    console.log(input);\n"
                        + "    paused = !paused;\n"
                        + "}\n"
                        + "\n"
                        + "function animate() {\n"
                        + "    requestAnimationFrame(animate);\n"
                        + "    if (!paused) {\n"
                        + "        cube.rotation.x += 0.01;\n"
                        + "        cube.rotation.y += 0.01;\n"
                        + "        if (cube.rotation.x > lastX) {\n"
                        + "            fireWachosEvent('x = ' + Math.round(cube.rotation.x)); //send info to wachos\n"
                        + "            lastX += 5; //wait five degrees before posting again\n"
                        + "        }\n"
                        + "    }\n"
                        + "    renderer.render(scene, camera);\n"
                        + "}\n"
                        + "animate();"));
            }
        });

        String code = "        //create a container for the Threejs object, and then add a Threejs object to it\n"
                + "        VBox threejsContainer = new VBox().setWidth(\"100%\");\n"
                + "        threejsContainer.add(new Threejs(\"wjs/three.min.js\", \"threejsexamples/webgl_geometries.js\").setWidth(\"100%\").setHeight(\"400px\"));\n"
                + "\n"
                + "        //any time a selection occurs, load the associated threejs-based application\n"
                + "        //we don't need to reload threejs.min.js, because we loaded it a few lines ago\n"
                + "        ComboBox threeSelector = new ComboBox(\"Geometries\", Arrays.asList(\"Geometries\", \"Soldier\", \"Cube\"), selection -> {\n"
                + "            threejsContainer.removeAll();\n"
                + "            if (selection.equals(\"Soldier\")) {\n"
                + "                threejsContainer.add(new Threejs(\"threejsexamples/webgl_animation_walk.js\").setWidth(\"100%\").setHeight(\"400px\"));\n"
                + "            } else if (selection.equals(\"Geometries\")) {\n"
                + "                threejsContainer.add(new Threejs(\"threejsexamples/webgl_geometries.js\").setWidth(\"100%\").setHeight(\"400px\"));\n"
                + "            } else if (selection.equals(\"Cube\")) {\n"
                + "                Threejs cube = new Threejs(\"threejsexamples/cube.js\").setWidth(\"100%\").setHeight(\"400px\");\n"
                + "                cube.threejsListeners.add(event -> session.postInfo(\"Increased X\", event)); //receive communication from threejs\n"
                + "                threejsContainer.add(new Button(\"Toggle Paused\", action -> cube.threeExec(\"pressedTogglePaused\"))); //send communication to threejs\n"
                + "                threejsContainer.add(cube);\n"
                + "                threejsContainer.add(new CodeSnippet(\"[cube.js source code]\"));\n"
                + "            }\n"
                + "        });";

        return new Tutorial("Threejs", "gov/mil/navy/nswcdd/wachos/components/Threejs.html",
                new VBox(new Label("Select 'Cube' to see simple JavaScript code with Java communication"), threeSelector, threejsContainer).setWidth("100%"), code);
    }

}
