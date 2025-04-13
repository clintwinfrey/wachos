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
import static gov.mil.navy.nswcdd.wachos.components.text.ImageButton.JQ_ICONS;
import java.util.List;

/**
 * ImageDropButton is a combination of ImageButton and DropButton, so a menu
 * appears when it is clicked on, but the button uses the specified image
 */
public class ImageDropButton extends DropButton {

    /**
     * @param text the button label
     * @param options the menu options that appear when the button is clicked
     */
    public ImageDropButton(String text, List<String> options) {
        super(text, options);
        properties.clear();
        setStyle("cursor", "pointer");
        this.clientTextProperty = "attr.src";
        inner = " > span";
    }

    /**
     * @param text the button label
     * @param options the menu options that appear when the button is clicked
     * @param menuClickListener listens to clicks on the menu options
     */
    public ImageDropButton(String text, List<String> options, ComponentListener menuClickListener) {
        this(text, options);
        menu.clickListeners.add(index -> menuClickListener.update(menu.getOptions().get(Integer.parseInt(index))));
    }

    /**
     * @param text the button label
     * @param options the menu options that appear when the button is clicked
     * @param menuClickListener listens to clicks on the menu options
     * @param hoverListener listens for hovers over the button
     */
    public ImageDropButton(String text, List<String> options, ComponentListener menuClickListener, ComponentListener hoverListener) {
        this(text, options, menuClickListener);
        hoverListeners.add(hoverListener);
    }

    /**
     * Sets the image icon of this button. Most likely, you don't need to call a
     * value listener whenever you set the button's content. As such, this is a
     * faster way because it only sets the value in the client
     *
     * @param src the image icon
     * @return this
     */
    public ImageDropButton setImage(String src) {
        setText(src);
        redraw();
        return this;
    }

    /**
     * Sets the size of this ImageDropButton
     *
     * @param size width is set to this, and height is set to auto
     * @return this
     */
    public ImageDropButton setSize(String size) {
        setStyle("width", size).setStyle("height", "auto");
        return this;
    }

    /**
     * HoverListeners will get updated when the mouse enters the button. Did
     * this so the menu options can be updated before the menu appears. Trying
     * to update the options on a click event will happen after the menu
     * appears, so it won't be updated until the user clicks on the button the
     * second time.
     *
     * @return html representation of this drop button
     */
    @Override
    public String toHtml() {
        String start;
        if (value.startsWith("fa-")) {
            start = "<div id='" + getId() + "'><span id='" + getId() + "inner' class='fa " + (enabled ? "" : "ui-state-disabled ") + menu.getId() + " " + value;
        } else if (JQ_ICONS.contains(value)) {
            start = "<div id='" + getId() + "'><span id='" + getId() + "inner' class='ui-icon " + (enabled ? "" : "ui-state-disabled ") + menu.getId() + " " + value;
        } else {
            start = "<div id='" + getId() + "'><img  id='" + getId() + "inner' class='" + menu.getId() + "' src=\"" + value.replace("\"", "&quot;").replace("\\", "\\\\") + "\" ";
        }
        return start + "' " + getProperties() + "style=\"" + getStyle() + "\" onclick=\"if ($(this).hasClass('ui-state-disabled')) { return; }\" "
                + "onmouseover=\"if ($(this).hasClass('ui-state-disabled')) { return; } changed" + layoutId + "({id: '" + getId() + "', value: '#buttonhover'});\"/>"
                + menu.toHtml() + "</div>";
    }
}
