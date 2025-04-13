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
import gov.mil.navy.nswcdd.wachos.components.text.TextArea;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class TextAreaTutorial {

    public static Tutorial create(WSession session) {
        //type in one TextArea and have it show up in the other
        TextArea viewHere = new TextArea("View here..."); //view what's typed here
        TextArea typeHere = new TextArea("Type here...", //type stuff in here
                text -> viewHere.setText(text)); //on type, set text of other
        HBox container = new HBox(typeHere, viewHere);

        String code = "        //type in one TextArea and have it show up in the other\n"
                + "        TextArea viewHere = new TextArea(\"View here...\"); //view what's typed here\n"
                + "        TextArea typeHere = new TextArea(\"Type here...\", //type stuff in here\n"
                + "                text -> viewHere.setText(text)); //type text here\n"
                + "        HBox container = new HBox(typeHere, viewHere);";

        return new Tutorial("TextArea", "gov/mil/navy/nswcdd/wachos/components/text/TextArea.html", container, code);
    }
}
