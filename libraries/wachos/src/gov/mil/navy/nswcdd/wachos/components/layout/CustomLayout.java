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
import java.util.List;

/**
 * CustomLayout is a Layout that allows the user to define HTML, with components
 * placed where the #tf tag is used
 */
public class CustomLayout extends Layout {

    /**
     * the HTML of this layout
     */
    String html;

    /**
     * Constructor
     *
     * @param html the HTML of this layout
     * @param components the components to place wherever there is a #tf tag
     */
    public CustomLayout(String html, List<Component> components) {
        this.html = html;
        this.components.addAll(components);
    }

    /**
     * Updates the HTML of this layout
     *
     * @param html the HTML of this layout
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * @return the HTML that is produced - basically what the user passed in,
     * but maybe with a div around it to ensure sizing
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public String toHtml() {
        StringBuilder ret = new StringBuilder();
        String width = getProperty("width");
        String height = getProperty("height");
        if (!width.equals("") || !height.equals("")) {
            String gridWidth = width.equals("") ? "" : "width:" + width + ";";
            String gridHeight = height.equals("") ? "" : "height:" + height + ";"; //add padding for the scrollbar, which is standardized for all browsers at 17px
            ret.append("<div id=\"" + getId() + "\" style=\"" + gridWidth + gridHeight + "overflow:auto;\">");
        } else {
            ret.append("<div id=\"" + getId() + "\">");
        }
        ret.append(html);
        ret.append("</div>");
        String retStr = ret.toString();
        for (int i = 0; i < components.size(); i++) {
            retStr = retStr.replaceFirst("#tf", ((Component<?>) components.get(i)).toHtml().replace("$", "\\$"));
        }
        return retStr;
    }

}
