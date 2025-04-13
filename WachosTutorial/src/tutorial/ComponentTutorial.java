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

import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.Color;

public class ComponentTutorial {

    public static Tutorial create(WSession session) {
        //Component is an abstract class, and just about everything in wachos extends it
        Label label = new Label("Double-click me");

        //tooltips give additional information when the user hovers over the component
        label.setToolTip("This is a label");

        //you can set a property as follows
        label.setProperty("ondblclick", "$(this).animate({color:'red'}, 1000);");

        //you can set CSS styling as follows
        label.setStyle("color", "white").setStyle("font-size", "40px");

        //there are also some convenience methods for styling
        label.setBackground(Color.BLACK);

        String code = "        //Component is an abstract class, and just about everything in wachos extends it\n"
                + "        Label label = new Label(\"Double-click me\");\n"
                + "        \n"
                + "        //tooltips give additional information when the user hovers over the component\n"
                + "        label.setToolTip(\"This is a label\");\n"
                + "\n"
                + "        //you can set a property as follows\n"
                + "        label.setProperty(\"ondblclick\", \"$(this).animate({color:'red'}, 1000);\");\n"
                + "\n"
                + "        //you can set CSS styling as follows\n"
                + "        label.setStyle(\"color\", \"white\").setStyle(\"font-size\", \"40px\");\n"
                + "\n"
                + "        //there are also some convenience methods for styling\n"
                + "        label.setBackground(Color.BLACK);";

        return new Tutorial("Component", "gov/mil/navy/nswcdd/wachos/components/Component.html",
                label, code);
    }

}
