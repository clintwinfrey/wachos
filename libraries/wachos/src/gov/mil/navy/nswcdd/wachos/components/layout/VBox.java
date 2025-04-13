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
package gov.mil.navy.nswcdd.wachos.components.layout;

import gov.mil.navy.nswcdd.wachos.components.Component;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A VBox is just a grid with where number of columns is set to 1
 */
public class VBox extends Grid<VBox> {

    /**
     * Constructor
     *
     * @param components all of the Components to show in this VBox
     */
    public VBox(Component... components) {
        super(1, components);
        hSpacing = 0;
    }

    /**
     * Add components to this layout, starting at the given index
     *
     * @param index the place to put these components
     * @param components the Components to add
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public void add(int index, List<Component> components) {
        if (isRendered()) {
            StringBuilder sb = new StringBuilder();

            //make the html that will be added
            for (int i = 0; i < components.size(); i++) {
                sb.append("<tr><td " + getStyle(components.get(i), i == 0 && index == 0, true, false) + ">").append(components.get(i).toHtml().replace("\\\\", "\\").replace("\\", "\\\\")).append("</tr></td>");
            }

            //if adding at index 0, then the old first element needs padding now
            String padOldFirstElement = "";
            if (index == 0 && !this.components.isEmpty()) {
                padOldFirstElement = "$('#table" + getId() + " > tbody > tr > td').eq(0).css('padding-top', '" + vSpacing + "px');\n";
            }

            //execute adding the components to the HTML
            String html = sb.toString().replace("#LAYOUT_ID#", layoutId).replace("\"", "\\\"").replace("\n", "\\n");
            exec(padOldFirstElement + "$('#table" + getId() + " > tbody" + (index == 0 ? "" : " > tr") + "')" + (index == 0 ? "" : ".eq(" + index + "-1)") + "." + (index == 0 ? "prepend" : "after") + "(\"" + html + "\");");
            WTools.initToolTips(components, session);
        }

        //these components need to all be initialized
        for (int i = 0; i < components.size(); i++) {
            components.get(i).init(layoutId, session);
        }

        //and finally, we need to add the components on the java side
        this.components.addAll(index, components);
    }

    /**
     * Remove these components (as a List) from the layout
     *
     * @param components the components to remove
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public void remove(List<Component> components) {
        List<Integer> indices = new ArrayList<>();
        for (Component component : components) {
            if (!this.components.contains(component)) {
                continue;
            }
            indices.add(this.components.indexOf(component));
        }
        if (indices.isEmpty()) {
            return; //there's nothing to actually return
        }
        this.components.removeAll(components);
        if (isRendered()) { //we actually need to remove this from the DOM, too
            Collections.sort(indices, Comparator.reverseOrder());
            StringBuilder sb = new StringBuilder();
            for (Integer index : indices) {
                sb.append("$('#table" + getId() + " > tbody > tr').eq(" + index + ").remove();\n");
            }
            if (!this.components.isEmpty()) {
                sb.append("$('#table" + getId() + " > tbody > tr > td').eq(0).css('padding-top', '0px');\n");
            }
            exec(sb.toString());
        }
    }

}
