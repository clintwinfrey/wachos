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

import gov.mil.navy.nswcdd.wachos.components.ComponentListener;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class ComponentListenerTutorial {

    public static Tutorial create(WSession session) {

        //most components allow for event listening; to listen, you need a ComponentListener
        Button b1 = new Button("Listener", new ComponentListener() {
            @Override
            public void update(String eventInfo) { //eventInfo is going to contain string information relevant to the user's action 
                session.postWarning("Warning", "You are not using a lambda, which is kind of annoying");
            }
        });

        //for readability and brevity, you may want to use a lambda
        Button b2 = new Button("Lambda Listener", eventInfo -> session.postSuccess("Success!", "You pressed the button!"));

        //you can also add and remove listeners if you need more than one; open your browser's dev tools to see this one
        b2.clickListeners.add(eventInfo -> session.exec("console.log('I am another event.');"));

        String code = "        //most components allow for event listening; to listen, you need a ComponentListener\n"
                + "        Button b1 = new Button(\"Listener\", new ComponentListener() {\n"
                + "            @Override\n"
                + "            public void update(String eventInfo) { //eventInfo is going to contain string information relevant to the user's action \n"
                + "                session.postWarning(\"Warning\", \"You are not using a lambda, which is kind of annoying\");\n"
                + "            }\n"
                + "        });\n"
                + "\n"
                + "        //for readability and brevity, you may want to use a lambda\n"
                + "        Button b2 = new Button(\"Lambda Listener\", eventInfo -> session.postSuccess(\"Success!\", \"You pressed the button!\"));\n"
                + "\n"
                + "        //you can also add and remove listeners if you need more than one; open your browser's dev tools to see this one\n"
                + "        b2.clickListeners.add(eventInfo -> session.exec(\"console.log('I am another event.');\"));";
        return new Tutorial("ComponentListener", "gov/mil/navy/nswcdd/wachos/components/ComponentListener.html",
                new HBox(b1, b2), code);
    }

}
