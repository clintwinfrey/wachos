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
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.Arrays;

public class ComboBoxTutorial {

    public static Tutorial create(WSession session) {
        //create a combo for selection your favorite Beatle
        ComboBox combobox = new ComboBox("John", Arrays.asList("John", "Paul", "George", "Ringo"), value -> {
            if (value.equals("John")) {
                session.postInfo("Info", "Tough to argue with John");
            } else if (value.equals("Paul")) {
                session.postSuccess("Success", "I mean, Yesterday is possibly the greatest song ever written");
            } else if (value.equals("George")) {
                session.postWarning(("Warning"), "George?  I mean, I guess...");
            } else if (value.equals("Ringo")) {
                session.postError("Error", "You accidentally selected Ringo");
            }
        });

        //create a button that changes the available options in the combo
        Button filter = new Button("Change Options", action -> combobox.setOptions(Arrays.asList("John", "Paul")));
        filter.setToolTip("Click here if you only want to prevent user error, ensuring selection of John or Paul");

        String code = "        //create a combo for selection your favorite Beatle\n"
                + "        ComboBox combobox = new ComboBox(\"John\", Arrays.asList(\"John\", \"Paul\", \"George\", \"Ringo\"), value -> {\n"
                + "            if (value.equals(\"John\")) {\n"
                + "                session.postInfo(\"Info\", \"Tough to argue with John\");\n"
                + "            } else if (value.equals(\"Paul\")) {\n"
                + "                session.postSuccess(\"Success\", \"I mean, Yesterday is possibly the greatest song ever written\");\n"
                + "            } else if (value.equals(\"George\")) {\n"
                + "                session.postWarning((\"Warning\"), \"George?  I mean, I guess...\");\n"
                + "            } else if (value.equals(\"Ringo\")) {\n"
                + "                session.postError(\"Error\", \"You accidentally selected Ringo\");\n"
                + "            }\n"
                + "        });\n"
                + "\n"
                + "        //create a button that changes the available options in the combo\n"
                + "        Button filter = new Button(\"Change Options\", action -> combobox.setOptions(Arrays.asList(\"John\", \"Paul\")));\n"
                + "        filter.setToolTip(\"Click here if you only want to prevent user error, ensuring selection of John or Paul\");";

        return new Tutorial("ComboBox", "gov/mil/navy/nswcdd/wachos/components/text/ComboBox.html",
                new VBox(combobox, filter), code);
    }

}
