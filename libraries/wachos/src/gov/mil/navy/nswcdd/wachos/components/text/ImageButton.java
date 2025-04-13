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
import java.util.Arrays;
import java.util.List;

/**
 * ImageButton is like an Image, but it is also a button - the user can interact
 * with an ImageButton by clicking on it
 */
public class ImageButton extends Button {

    static List<String> JQ_ICONS = Arrays.asList("ui-icon-blank", "ui-icon-caret-1-n", "ui-icon-caret-1-ne", "ui-icon-caret-1-e", "ui-icon-caret-1-se", "ui-icon-caret-1-s", "ui-icon-caret-1-sw", "ui-icon-caret-1-w", "ui-icon-caret-1-nw",
            "ui-icon-caret-2-n-s", "ui-icon-caret-2-e-w", "ui-icon-triangle-1-n", "ui-icon-triangle-1-ne", "ui-icon-triangle-1-e", "ui-icon-triangle-1-se", "ui-icon-triangle-1-s", "ui-icon-triangle-1-sw", "ui-icon-triangle-1-w",
            "ui-icon-triangle-1-nw", "ui-icon-triangle-2-n-s", "ui-icon-triangle-2-e-w", "ui-icon-arrow-1-n", "ui-icon-arrow-1-ne", "ui-icon-arrow-1-e", "ui-icon-arrow-1-se", "ui-icon-arrow-1-s", "ui-icon-arrow-1-sw", "ui-icon-arrow-1-w",
            "ui-icon-arrow-1-nw", "ui-icon-arrow-2-n-s", "ui-icon-arrow-2-ne-sw", "ui-icon-arrow-2-e-w", "ui-icon-arrow-2-se-nw", "ui-icon-arrowstop-1-n", "ui-icon-arrowstop-1-e", "ui-icon-arrowstop-1-s", "ui-icon-arrowstop-1-w",
            "ui-icon-arrowthick-1-n", "ui-icon-arrowthick-1-ne", "ui-icon-arrowthick-1-e", "ui-icon-arrowthick-1-se", "ui-icon-arrowthick-1-s", "ui-icon-arrowthick-1-sw", "ui-icon-arrowthick-1-w", "ui-icon-arrowthick-1-nw",
            "ui-icon-arrowthick-2-n-s", "ui-icon-arrowthick-2-ne-sw", "ui-icon-arrowthick-2-e-w", "ui-icon-arrowthick-2-se-nw", "ui-icon-arrowthickstop-1-n", "ui-icon-arrowthickstop-1-e", "ui-icon-arrowthickstop-1-s",
            "ui-icon-arrowthickstop-1-w", "ui-icon-arrowreturnthick-1-w", "ui-icon-arrowreturnthick-1-n", "ui-icon-arrowreturnthick-1-e", "ui-icon-arrowreturnthick-1-s", "ui-icon-arrowreturn-1-w", "ui-icon-arrowreturn-1-n",
            "ui-icon-arrowreturn-1-e", "ui-icon-arrowreturn-1-s", "ui-icon-arrowrefresh-1-w", "ui-icon-arrowrefresh-1-n", "ui-icon-arrowrefresh-1-e", "ui-icon-arrowrefresh-1-s", "ui-icon-arrow-4", "ui-icon-arrow-4-diag", "ui-icon-extlink",
            "ui-icon-newwin", "ui-icon-refresh", "ui-icon-shuffle", "ui-icon-transfer-e-w", "ui-icon-transferthick-e-w", "ui-icon-folder-collapsed", "ui-icon-folder-open", "ui-icon-document", "ui-icon-document-b", "ui-icon-note",
            "ui-icon-mail-closed", "ui-icon-mail-open", "ui-icon-suitcase", "ui-icon-comment", "ui-icon-person", "ui-icon-print", "ui-icon-trash", "ui-icon-locked", "ui-icon-unlocked", "ui-icon-bookmark", "ui-icon-tag", "ui-icon-home",
            "ui-icon-flag", "ui-icon-calculator", "ui-icon-cart", "ui-icon-pencil", "ui-icon-clock", "ui-icon-disk", "ui-icon-calendar", "ui-icon-zoomin", "ui-icon-zoomout", "ui-icon-search", "ui-icon-wrench", "ui-icon-gear",
            "ui-icon-heart", "ui-icon-star", "ui-icon-link", "ui-icon-cancel", "ui-icon-plus", "ui-icon-plusthick", "ui-icon-minus", "ui-icon-minusthick", "ui-icon-close", "ui-icon-closethick", "ui-icon-key", "ui-icon-lightbulb",
            "ui-icon-scissors", "ui-icon-clipboard", "ui-icon-copy", "ui-icon-contact", "ui-icon-image", "ui-icon-video", "ui-icon-script", "ui-icon-alert", "ui-icon-info", "ui-icon-notice", "ui-icon-help", "ui-icon-check",
            "ui-icon-bullet", "ui-icon-radio-off", "ui-icon-radio-on", "ui-icon-pin-w", "ui-icon-pin-s", "ui-icon-play", "ui-icon-pause", "ui-icon-seek-next", "ui-icon-seek-prev", "ui-icon-seek-end", "ui-icon-seek-first", "ui-icon-stop",
            "ui-icon-eject", "ui-icon-volume-off", "ui-icon-volume-on", "ui-icon-power", "ui-icon-signal-diag", "ui-icon-signal", "ui-icon-battery-0", "ui-icon-battery-1", "ui-icon-battery-2", "ui-icon-battery-3", "ui-icon-circle-plus",
            "ui-icon-circle-minus", "ui-icon-circle-close", "ui-icon-circle-triangle-e", "ui-icon-circle-triangle-s", "ui-icon-circle-triangle-w", "ui-icon-circle-triangle-n", "ui-icon-circle-arrow-e", "ui-icon-circle-arrow-s",
            "ui-icon-circle-arrow-w", "ui-icon-circle-arrow-n", "ui-icon-circle-zoomin", "ui-icon-circle-zoomout", "ui-icon-circle-check", "ui-icon-circlesmall-plus", "ui-icon-circlesmall-minus", "ui-icon-circlesmall-close",
            "ui-icon-squaresmall-plus", "ui-icon-squaresmall-minus", "ui-icon-squaresmall-close", "ui-icon-grip-dotted-vertical", "ui-icon-grip-dotted-horizontal", "ui-icon-grip-solid-vertical", "ui-icon-grip-solid-horizontal",
            "ui-icon-gripsmall-diagonal-se", "ui-icon-grip-diagonal-se");

    /**
     * Constructor
     *
     * @param text this component's value
     */
    public ImageButton(String text) {
        super(text);
        properties.clear();
        setStyle("cursor", "pointer");
        this.clientTextProperty = "attr.src";
    }

    /**
     * Constructor
     *
     * @param text this component's value
     * @param clickListener contains the event that happens when the user clicks
     */
    public ImageButton(String text, ComponentListener clickListener) {
        super(text, clickListener);
        properties.clear();
        setStyle("cursor", "pointer");
        this.clientTextProperty = "attr.src";
    }

    /**
     * Sets the image icon of this button. Most likely, you don't need to call a
     * value listener whenever you set the button's content. As such, this is a
     * faster way because it only sets the value in the client
     *
     * @param src the image icon
     * @return this
     */
    public TextComponent setImage(String src) {
        setText(src);
        redraw();
        return this;
    }

    /**
     * Sets the size of this ImageButton
     *
     * @param size width is set to this, and height is set to auto
     * @return this
     */
    public ImageButton setSize(String size) {
        setStyle("width", size).setStyle("height", "auto");
        return this;
    }

    /**
     * @return html representation of this image button
     */
    @Override
    public String toHtml() {
        if (value.startsWith("fa-")) {
            return "<span id='" + getId() + "' class='fa " + (enabled ? "" : "ui-state-disabled ") + value + "' " + getProperties() + "style=\"" + getStyle() + "\" onclick=\"if ($(this).hasClass('ui-state-disabled')) { return; } changed" + layoutId + "({id: '" + getId() + "', value: '#buttonclick'});\"/>";
        } else if (JQ_ICONS.contains(value)) {
            return "<span id='" + getId() + "' class='ui-icon " + (enabled ? "" : "ui-state-disabled ") + value + "' " + getProperties() + "style=\"" + getStyle() + "\" onclick=\"if ($(this).hasClass('ui-state-disabled')) { return; } changed" + layoutId + "({id: '" + getId() + "', value: '#buttonclick'});\"/>";
        } else {
            return "<img  id='" + getId() + "' src=\"" + value.replace("\"", "&quot;").replace("\\", "\\\\") + "\" " + getProperties() + "style=\"" + getStyle() + "\" onclick=\"changed" + layoutId + "({id: '" + getId() + "', value: '#buttonclick'});\"/>";
        }
    }

}
