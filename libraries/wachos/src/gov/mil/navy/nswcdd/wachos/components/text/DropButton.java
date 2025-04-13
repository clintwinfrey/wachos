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
import gov.mil.navy.nswcdd.wachos.components.ComponentListeners;
import gov.mil.navy.nswcdd.wachos.components.ContextMenu;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.ArrayList;
import java.util.List;

/**
 * A DropButton is a widget that allows the user to click in order to have a
 * drop down menu appear
 */
public class DropButton extends TextComponent {

    /**
     * menu that appears when the button is clicked
     */
    public final ContextMenu menu = new ContextMenu(new ArrayList<>());
    /**
     * perform this hoverListener's action when the button is hovered over
     */
    public final ComponentListeners hoverListeners = new ComponentListeners();

    /**
     * @param text the button label
     * @param options the menu options that appear when the button is clicked
     */
    public DropButton(String text, List<String> options) {
        super(text);
        menu.setOptions(options);
        setProperty("css.padding", ".3em 1em");
        inner = " > button";
    }

    /**
     * @param text the button label
     * @param options the menu options that appear when the button is clicked
     * @param menuClickListener listens to clicks on the menu options
     */
    public DropButton(String text, List<String> options, ComponentListener menuClickListener) {
        this(text, options);
        menu.clickListeners.add(index -> menuClickListener.update(menu.getOptions().get(Integer.parseInt(index))));
    }

    /**
     * @param text the button label
     * @param options the menu options that appear when the button is clicked
     * @param menuClickListener listens to clicks on the menu options
     * @param hoverListener listens for hovers over the button
     */
    public DropButton(String text, List<String> options, ComponentListener menuClickListener, ComponentListener hoverListener) {
        this(text, options, menuClickListener);
        hoverListeners.add(hoverListener);
    }

    /**
     * Sets the options for the plus button at the top right
     *
     * @param options options that can be selected
     */
    public void setOptions(List<String> options) {
        menu.setOptions(options);
    }

    /**
     * Initializes this component with the ID of the master layout and session
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    @Override
    public void init(String masterId, WSession session) {
        super.init(masterId, session);
        menu.init(masterId, session);
    }

    /**
     * An event has taken place on this component so take action
     *
     * @param value the event data
     */
    @Override
    public void fireEvent(String value) {
        if (!isEnabled()) {
            return;
        }
        if (value.startsWith("#buttonhover")) {
            hoverListeners.update("BUTTON_HOVER");
        } else {
            setText(value, true, false);
        }
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
        return "<div id='" + getId() + "'><button type='button' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only "
                + menu.getId() + (enabled ? "" : " ui-state-disabled") + "' " + getProperties() + "style=\"" + getStyle() + "\" onmouseover=\"if ($(this).hasClass('ui-state-disabled')) { return; } "
                + "$(this).addClass('ui-state-hover'); changed" + layoutId + "({id: '" + getId() + "', value: '#buttonhover'});\" onmouseout=\"$(this).removeClass('ui-state-hover');\" "
                + "onclick=\"if ($(this).hasClass('ui-state-disabled')) { return; }\">"
                + value.replace("\"", "&quot;").replace("\\", "\\\\") + "</button>" + menu.toHtml() + "</div>";
    }

    /**
     * To be called when this component is no longer needed; clears listeners
     */
    @Override
    public void dispose() {
        super.dispose();
        hoverListeners.clear();
    }
}
