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
package gov.mil.navy.nswcdd.wachos.components;

import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuItem is an item that is placed in a Menu. It either acts like menu with
 * children, or like a button when clicked
 */
public class MenuItem {

    /**
     * the String value of this MenuItem
     */
    private String text;
    /**
     * listens for when this MenuItem is clicked
     */
    public final ComponentListeners clickListeners = new ComponentListeners();
    /**
     * flag indicating if this MenuItem can be clicked; if false, it is grayed
     * out
     */
    private boolean enabled = true;
    /**
     * child MenuItems that this MenuItem owns
     */
    protected final List<MenuItem> children = new ArrayList<>();
    /**
     * the user's session
     */
    WSession session;
    /**
     * the parent that this MenuItem ultimately belongs to; we need to know this
     * so we can call redraw if a child is added or removed
     */
    private MenuBar menubar;

    /**
     * Constructor
     *
     * @param text the label for this MenuItem
     */
    public MenuItem(String text) {
        this.text = text;
    }

    /**
     * Constructor
     *
     * @param text the label for this component
     * @param clickListener the event that happens when clicked
     */
    public MenuItem(String text, ComponentListener clickListener) {
        this.text = text;
        this.clickListeners.add(clickListener);
    }

    /**
     * @return a unique ID for this MenuItem
     */
    protected String getId() {
        return "mi" + hashCode();
    }

    /**
     * Adds html to the provided StringBuilder for the provided MenuItem
     *
     * @param layoutId the master layout
     * @param menubarId the ID of the menubar this belongs to
     * @param sb we're adding HTML to this
     * @param mi the MenuItem to add
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    protected void append(String layoutId, String menubarId, StringBuilder sb, MenuItem mi) {
        if (mi.children.isEmpty()) {
            sb.append("<li class='ui-menuitem ui-widget ui-corner-all' role='menuitem'>\n"
                    + "<a tabindex='-1' class='ui-menuitem-link ui-corner-all ' onclick=\"if (!$jq('#" + mi.getId() + "').hasClass('ui-state-disabled')) { changed" + layoutId + "({id: '" + menubarId + "', value: '" + mi.getId() + "'}); }\">\n"
                    + "<span id='" + mi.getId() + "' class='ui-menuitem-text" + (mi.enabled ? "" : " ui-state-disabled") + "'>" + mi.text + "</span>\n"
                    + "</a></li>\n");
        } else {
            sb.append("<li class='ui-widget ui-menuitem ui-corner-all ui-menu-parent' aria-haspopup='true'>\n"
                    + "<a class='ui-menuitem-link ui-submenu-link ui-corner-all' tabindex='-1'>\n"
                    + "<span class='ui-menuitem-text'>" + mi.text + "</span>\n"
                    + "<span class='ui-icon ui-icon-triangle-1-s'></span>\n"
                    + "</a>\n"
                    + "<ul class='ui-widget-content ui-menu-list ui-corner-all ui-helper-clearfix ui-menu-child ui-shadow' role='menu'>\n");
            for (MenuItem child : mi.children) {
                append(layoutId, menubarId, sb, child);
            }
            sb.append("    </ul></li>\n");
        }
    }

    /**
     * Enables this MenuItem for user interaction
     *
     * @param enabled flag indicating if the user can interact with this
     * MenuItem
     * @return this MenuItem
     */
    public MenuItem setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (session != null) { //not yet initialized, but it'll show up in the append method so don't worry
                session.exec("$jq('#" + getId() + "')." + (enabled ? "remove" : "add") + "Class('ui-state-disabled');");
            }
        }
        return this;
    }

    /**
     * @return flag indicating if the user can interact with this MenuItem
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * When called, the click listeners are notified that the MenuItem was
     * clicked
     */
    public void fireClickEvent() {
        if (!isEnabled()) {
            return;
        }
        clickListeners.update(text);
    }

    /**
     * Sets the value of this MenuItem
     *
     * @param text the new value of this MenuItem
     * @return this
     */
    public MenuItem setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * @return the MenuItem's String value
     */
    public String getText() {
        return text;
    }

    /**
     * Updates the reference of the MenuBar this belongs to
     *
     * @param menubar the MenuBar this should belong to
     */
    protected void updateMenuBar(MenuBar menubar) {
        if (menubar != null) {
            this.menubar = menubar;
        }
        for (MenuItem child : children) {
            child.updateMenuBar(menubar);
        }
    }

    /**
     * Returns true if children contains the specified element
     *
     * @param item element whose presence in this list is to be tested
     * @return true if children contains the specified element
     */
    public boolean contains(MenuItem item) {
        return children.contains(item);
    }

    /**
     * Adds a MenuItem to this menu bar
     *
     * @param child the MenuItem to be added
     * @return this
     */
    public MenuItem addChild(MenuItem child) {
        children.add(child);
        updateMenuBar(menubar);
        if (menubar != null) {
            menubar.redraw();
        }
        return this;
    }

    /**
     * Adds a MenuItem at the provided index to this menu bar
     *
     * @param index the location to insert the MenuItem
     * @param child the MenuItem to be added
     * @return this
     */
    public MenuItem addChild(int index, MenuItem child) {
        children.add(index, child);
        updateMenuBar(menubar);
        if (menubar != null) {
            menubar.redraw();
        }
        return this;
    }

    /**
     * Removes a MenuItem from this menu bar
     *
     * @param child the MenuItem to be removed
     * @return this
     */
    public MenuItem removeChild(MenuItem child) {
        children.remove(child);
        if (menubar != null) {
            menubar.redraw();
        }
        return this;
    }

    /**
     * Removes a MenuItem from this menu bar
     *
     * @param index the location of the MenuItem to be removed
     * @return this
     */
    public MenuItem removeChild(int index) {
        children.remove(index);
        if (menubar != null) {
            menubar.redraw();
        }
        return this;
    }

    /**
     * Returns the child MenuItem at the provided index
     *
     * @param index the index of the MenuItem to retrieve
     * @return the retrieved MenuItem
     */
    public MenuItem getChild(int index) {
        return children.get(index);
    }

    /**
     * @return all children of this menu bar
     */
    public List<MenuItem> getChildren() {
        return new ArrayList<>(children);
    }

    /**
     * @return the number of MenuItem children for the menu bar
     */
    public int getChildCount() {
        return children.size();
    }

}
