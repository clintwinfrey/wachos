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

import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class ButtonTutorial {

    public static Tutorial create(WSession session) {

        //let's make a very simple button that just posts a message when the user clicks it
        Button eventButton = new Button("Click Me", action -> session.postSuccess("Clicked!", "Nice work, buddy :)"));

        //let's make a button that's disabled :(
        Button disabledButton = new Button("Don't Click Me").setEnabled(false); //but don't let anyone click it

        //let's make a button that changes things in that second button
        Button changerButton = new Button("Change Disabled Button", action -> {
            if (disabledButton.isEnabled()) { //but only do this if the button is still disabled
                session.postError("Button already clicked", "You already clicked this button, which changed that first button accordingly");
            } else {
                disabledButton.setEnabled(true).setText("I'm a real button now!"); //make it enabled and change its text
                disabledButton.clickListeners.add(click -> session.postSuccess("I'm Real!", "I got no strings to hold me down!"));
            }
        });

        String code = "        //let's make a very simple button that just posts a message when the user clicks it\n"
                + "        Button eventButton = new Button(\"Click Me\", action -> session.postSuccess(\"Clicked!\", \"Nice work, buddy :)\"));\n"
                + "\n"
                + "        //let's make a button that's disabled :(\n"
                + "        Button disabledButton = new Button(\"Don't Click Me\").setEnabled(false); //but don't let anyone click it\n"
                + "\n"
                + "        //let's make a button that changes things in that second button\n"
                + "        Button changerButton = new Button(\"Change Disabled Button\", action -> {\n"
                + "            if (disabledButton.isEnabled()) { //but only do this if the button is still disabled\n"
                + "                session.postError(\"Button already clicked\", \"You already clicked this button, which changed that first button accordingly\");\n"
                + "            } else {\n"
                + "                disabledButton.setEnabled(true).setText(\"I'm a real button now!\"); //make it enabled and change its text\n"
                + "                disabledButton.clickListeners.add(click -> session.postSuccess(\"I'm Real!\", \"I got no strings to hold me down!\"));\n"
                + "            }\n"
                + "        });";

        return new Tutorial("Button", "gov/mil/navy/nswcdd/wachos/components/text/Button.html",
                new HBox(eventButton, disabledButton, changerButton), code);
    }

}
