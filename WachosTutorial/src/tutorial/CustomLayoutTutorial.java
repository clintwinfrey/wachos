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

import gov.mil.navy.nswcdd.wachos.components.layout.CustomLayout;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.PasswordField;
import gov.mil.navy.nswcdd.wachos.components.text.TextField;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.Arrays;

public class CustomLayoutTutorial {

    public static Tutorial create(WSession session) {
        //with a CustomLayout, you can put Components directly into HTML code
        String html = "<div class='wfontfamily'>\n" //sets the font family to the wachos-wide class
                + "<h1>This is a custom layout</h1>\n"
                + "<table width='200px' height='100%'>\n"
                + "  <tr height='100%'>\n"
                + "    <td>\n"
                + "      <table align='center'>\n"
                + "        <tr>\n"
                + "          <td align='right'>User&nbsp;name:</td>\n"
                + "          <td>#tf</td>\n" //this is the first #tf, so the first Component will be placed here; the 'user' TextField
                + "        </tr>\n"
                + "        <tr>\n"
                + "          <td align='right'>Password:</td>\n"
                + "          <td>#tf</td>\n" //this will be the second Component in the provided List, which is a PasswordField
                + "        </tr>\n"
                + "      </table>\n"
                + "    </td>\n"
                + "  </tr>\n"
                + "  <tr>\n"
                + "    <td align='right' colspan='2'>\n"
                + "      #tf\n" //and the third Component is our login button
                + "    </td>\n"
                + "  </tr>\n"
                + "</table>\n"
                + "</div>";
        TextField user = new TextField("");
        Button loginButton = new Button("Login", action -> session.postSuccess("Hooray!", "You tried to log in!"));
        CustomLayout custom = new CustomLayout(html, Arrays.asList(user, new PasswordField(""), loginButton));

        return new Tutorial("CustomLayout", "gov/mil/navy/nswcdd/wachos/components/layout/CustomLayout.html", custom,
                "        //with a CustomLayout, you can put Components directly into HTML code\n"
                + "        String html = \"<div class='wfontfamily'>\\n\" //sets the font family to the wachos-wide class\n"
                + "                + \"<h1>This is a custom layout</h1>\\n\"\n"
                + "                + \"<table width='200px' height='100%'>\\n\"\n"
                + "                + \"  <tr height='100%'>\\n\"\n"
                + "                + \"    <td>\\n\"\n"
                + "                + \"      <table align='center'>\\n\"\n"
                + "                + \"        <tr>\\n\"\n"
                + "                + \"          <td align='right'>User&nbsp;name:</td>\\n\"\n"
                + "                + \"          <td>#tf</td>\\n\" //this is the first #tf, so the first Component will be placed here; the 'user' TextField\n"
                + "                + \"        </tr>\\n\"\n"
                + "                + \"        <tr>\\n\"\n"
                + "                + \"          <td align='right'>Password:</td>\\n\"\n"
                + "                + \"          <td>#tf</td>\\n\" //this will be the second Component in the provided List, which is a PasswordField\n"
                + "                + \"        </tr>\\n\"\n"
                + "                + \"      </table>\\n\"\n"
                + "                + \"    </td>\\n\"\n"
                + "                + \"  </tr>\\n\"\n"
                + "                + \"  <tr>\\n\"\n"
                + "                + \"    <td align='right' colspan='2'>\\n\"\n"
                + "                + \"      #tf\\n\" //and the third Component is our login button\n"
                + "                + \"    </td>\\n\"\n"
                + "                + \"  </tr>\\n\"\n"
                + "                + \"</table>\\n\"\n"
                + "                + \"</div>\";\n"
                + "        TextField user = new TextField(\"\");\n"
                + "        Button loginButton = new Button(\"Login\", action -> session.postSuccess(\"Hooray!\", \"You tried to log in!\"));\n"
                + "        CustomLayout custom = new CustomLayout(html, Arrays.asList(user, new PasswordField(\"\"), loginButton));");
    }
}
