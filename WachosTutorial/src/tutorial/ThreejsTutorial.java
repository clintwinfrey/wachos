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
import gov.mil.navy.nswcdd.wachos.components.text.ComboBox;
import gov.mil.navy.nswcdd.wachos.components.threejs.Threejs;
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
                + "                Threejs soldier = new Threejs(\"threejsexamples/webgl_animation_walk.js\").setWidth(\"100%\").setHeight(\"400px\");\n"
                + "                soldier.threejsListeners.add(event -> session.postInfo(\"Movement\", event));\n"
                + "                threejsContainer.add(new Button(\"Run Faster\", action -> soldier.threeExec(\"runFaster\"))); //communicate with javascript via wachos button\n"
                + "                threejsContainer.add(soldier);\n"
                + "            } else if (selection.equals(\"Geometries\")) {\n"
                + "                threejsContainer.add(new Threejs(\"threejsexamples/webgl_geometries.js\").setWidth(\"100%\").setHeight(\"400px\"));\n"
                + "            } else if (selection.equals(\"Cube\")) {\n"
                + "                threejsContainer.add(new Threejs(\"threejsexamples/cube.js\").setWidth(\"100%\").setHeight(\"400px\"));\n"
                + "            }\n"
                + "        });";

        return new Tutorial("Threejs", "gov/mil/navy/nswcdd/wachos/components/text/Button.html",
                new VBox(threeSelector, threejsContainer).setWidth("100%"), code);
    }

}
