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

import gov.mil.navy.nswcdd.wachos.components.layout.Dialog;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class DialogTutorial {

    public static Tutorial create(WSession session) {

        //this will contain the 'Open Dialog' button
        VBox dialogCreator = new VBox();

        //create the dialog and set its content
        Dialog dialog = dialogCreator.createDialog("My Dialog"); //dialogCreator MUST be added to the app, so that dialog can attach to the app
        dialog.getContent().alignCenterH().add(
                new Label("Do you want to close this?"),
                new HBox(
                        new Button("Yes", action -> {
                            dialog.close();
                            session.postSuccess("Good Choice", "Your wisdom will prevent the wars of nations.");
                        }),
                        new Button("No", action -> session.postWarning("Why Not?", "You are a fool"))
                ).alignCenterH()
        );

        //add a button that will open the dialog
        dialogCreator.add(new Button("Open Dialog", action -> dialog.open()));

        String code = "        //this will contain the 'Open Dialog' button\n"
                + "        VBox dialogCreator = new VBox();\n"
                + "\n"
                + "        //create the dialog and set its content\n"
                + "        Dialog dialog = dialogCreator.createDialog(\"My Dialog\"); //dialogCreator MUST be added to the app, so that dialog can attach to the app\n"
                + "        dialog.getContent().alignCenterH().add(\n"
                + "                new Label(\"Do you want to close this?\"),\n"
                + "                new HBox(\n"
                + "                        new Button(\"Yes\", action -> {\n"
                + "                            dialog.close();\n"
                + "                            session.postSuccess(\"Good Choice\", \"Your wisdom will prevent the wars of nations.\");\n"
                + "                        }),\n"
                + "                        new Button(\"No\", action -> session.postWarning(\"Why Not?\", \"You are a fool\"))\n"
                + "                ).alignCenterH()\n"
                + "        );\n"
                + "\n"
                + "        //add a button that will open the dialog\n"
                + "        dialogCreator.add(new Button(\"Open Dialog\", action -> dialog.open()));";

        return new Tutorial("Dialog", "gov/mil/navy/nswcdd/wachos/components/layout/Dialog.html", dialogCreator, code);
    }
}
