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

import gov.mil.navy.nswcdd.wachos.components.MenuBar;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.ComboBox;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.TextField;
import gov.mil.navy.nswcdd.wachos.tools.Theme;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class ThemeTutorial {

    public static Tutorial create(WSession session) {
        //you can use Theme.createMenu to add theming to your menu bar
        MenuBar menubar = new MenuBar();
        menubar.setWidth("100%");
        menubar.addChild(Theme.createMenu(session, "12", "Arial"));

        //you can also set a theme explicitly
        TextField fontSize = new TextField("12");
        TextField fontFamily = new TextField("Arial");
        ComboBox themeSelector = new ComboBox(Theme.CUPERTINO, Theme.THEMES,
                selection -> {
                    Theme.change(session, selection, fontSize.getText(), fontFamily.getText()); //set the theme to the user selection
                    Theme.setDefault(selection); //store this as a cookie so the user can reference it in the next session
                });
        HBox customSelector = new HBox(new Label("Font Size"), fontSize, new Label("Font Family"), fontFamily, themeSelector);

        //and since we stored it as a cookie, we can load it when the application starts;
        //this can be set in application settings as follows, instead of explicitly setting the theme:
        String theme = Theme.getOrDefault(Theme.CUPERTINO); //the jquery theme

        String code = "        //you can use Theme.createMenu to add theming to your menu bar\n"
                + "        MenuBar menubar = new MenuBar();\n"
                + "        menubar.setWidth(\"100%\");\n"
                + "        menubar.addChild(Theme.createMenu(session, \"12\", \"Arial\"));\n"
                + "\n"
                + "        //you can also set a theme explicitly\n"
                + "        TextField fontSize = new TextField(\"12\");\n"
                + "        TextField fontFamily = new TextField(\"Arial\");\n"
                + "        ComboBox themeSelector = new ComboBox(Theme.CUPERTINO, Theme.THEMES,\n"
                + "                selection -> {\n"
                + "                    Theme.change(session, selection, fontSize.getText(), fontFamily.getText()); //set the theme to the user selection\n"
                + "                    Theme.setDefault(selection); //store this as a cookie so the user can reference it in the next session\n"
                + "                });\n"
                + "        HBox customSelector = new HBox(new Label(\"Font Size\"), fontSize, new Label(\"Font Family\"), fontFamily, themeSelector);\n"
                + "\n"
                + "        //and since we stored it as a cookie, we can load it when the application starts;\n"
                + "        //this can be set in application settings as follows, instead of explicitly setting the theme:\n"
                + "        String theme = Theme.getOrDefault(Theme.CUPERTINO); //the jquery theme";

        return new Tutorial("Theme", "gov/mil/navy/nswcdd/wachos/tools/Theme.html", new VBox(menubar, customSelector), code);
    }
}
