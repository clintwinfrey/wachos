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
import gov.mil.navy.nswcdd.wachos.components.text.ImageDropButton;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.Arrays;

public class ImageDropButtonTutorial {

    public static Tutorial create(WSession session) {

        //an ImageDropButton is the same thing as a DropButton, except it looks like an image
        ImageDropButton imageDropButton = new ImageDropButton("resource/w.png", Arrays.asList("Option 1", "Option 2", "Option 3"),
                selection -> session.postSuccess("PNG", "You selected " + selection)).setSize("20px");

        //you can also use JQuery ui icons https://api.jqueryui.com/theming/icons/
        ImageDropButton jqueryDropButton = new ImageDropButton("ui-icon-plus", Arrays.asList("Option 1", "Option 2", "Option 3"),
                selection -> session.postSuccess("JQuery Icon", "You selected " + selection));

        //you can even use font-awesome (append fa- in front of your icon choice) https://fontawesome.com/v4/icons/
        ImageDropButton fontAwesomeDropButton = new ImageDropButton("fa-bolt", Arrays.asList("Option 1", "Option 2", "Option 3"),
                selection -> session.postSuccess("Font Awesome", "You selected " + selection));

        String code = "        //an ImageDropButton is the same thing as a DropButton, except it looks like an image\n"
                + "        ImageDropButton imageDropButton = new ImageDropButton(\"resource/w.png\", Arrays.asList(\"Option 1\", \"Option 2\", \"Option 3\"),\n"
                + "                selection -> session.postSuccess(\"PNG\", \"You selected \" + selection)).setSize(\"20px\");\n"
                + "\n"
                + "        //you can also use JQuery ui icons https://api.jqueryui.com/theming/icons/\n"
                + "        ImageDropButton jqueryDropButton = new ImageDropButton(\"ui-icon-plus\", Arrays.asList(\"Option 1\", \"Option 2\", \"Option 3\"),\n"
                + "                selection -> session.postSuccess(\"JQuery Icon\", \"You selected \" + selection));\n"
                + "\n"
                + "        //you can even use font-awesome (append fa- in front of your icon choice) https://fontawesome.com/v4/icons/\n"
                + "        ImageDropButton fontAwesomeDropButton = new ImageDropButton(\"fa-bolt\", Arrays.asList(\"Option 1\", \"Option 2\", \"Option 3\"),\n"
                + "                selection -> session.postSuccess(\"Font Awesome\", \"You selected \" + selection));";
        return new Tutorial("ImageDropButton", "gov/mil/navy/nswcdd/wachos/components/text/ImageDropButton.html", new VBox(imageDropButton, jqueryDropButton, fontAwesomeDropButton), code);
    }

}
