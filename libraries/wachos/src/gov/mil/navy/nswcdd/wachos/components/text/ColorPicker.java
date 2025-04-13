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
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import gov.mil.navy.nswcdd.wachos.tools.Color;

/**
 * ColorPicker provides a user interface element that lets a user specify a
 * color by using a visual color picker interface.
 */
public class ColorPicker extends TextComponent {

    /**
     * Constructor
     *
     * @param color the starting color
     */
    public ColorPicker(Color color) {
        super(WTools.toHex(color));
        this.clientTextProperty = "val";
    }

    /**
     * Constructor
     *
     * @param color the starting color
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public ColorPicker(Color color, ComponentListener valueChangedListener) {
        super(WTools.toHex(color), valueChangedListener);
        this.clientTextProperty = "val";
    }

    /**
     * Sets the color of this picker
     *
     * @param color the color to use
     * @return this
     */
    @Override
    public ColorPicker setColor(Color color) {
        setText(WTools.toHex(color), true, true);
        return this;
    }

    /**
     * Gets the current color of this picker
     *
     * @return the current color
     */
    @Override
    public Color getColor() {
        return Color.decode(value);
    }

    /**
     * @return this object's unique identification
     */
    @Override
    public String getId() {
        return "clr" + hashCode();
    }

    /**
     * @return html representation of this combo box
     */
    @Override
    public String toHtml() {
        return "<input type='color' id='" + getId() + "' class='ui-icon' style='padding: 0; border: 0' onchange=\"changed" + layoutId + "({id: '" + getId() + "', value: this.value});\" value='" + value + "'/>";
    }

}
