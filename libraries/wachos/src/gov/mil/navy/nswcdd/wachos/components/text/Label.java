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
package gov.mil.navy.nswcdd.wachos.components.text;

import gov.mil.navy.nswcdd.wachos.components.ComponentListener;

/**
 * Label is a string of text
 */
public class Label extends Button {

    /**
     * Constructor
     *
     * @param text this component's value
     */
    public Label(String text) {
        super(text);
        setProperty("css.padding", "");
        this.clientTextProperty = "text";
    }

    /**
     * Constructor
     *
     * @param text this component's value
     * @param clickListener contains the event that happens when the user clicks
     */
    public Label(String text, ComponentListener clickListener) {
        this(text);
        clickListeners.add(clickListener);
    }

    /**
     * @return html representation of this label
     */
    @Override
    public String toHtml() {
        return "<span id='" + getId() + "' class='ui-widget' " + getProperties() + "style=\"" + getStyle() + "\" onclick=\"changed" + layoutId + "({id: '" + getId() + "', value: '#buttonclick'});\">" + value.replace("\"", "&quot;").replace("\\", "\\\\") + "</span>";
    }

}
