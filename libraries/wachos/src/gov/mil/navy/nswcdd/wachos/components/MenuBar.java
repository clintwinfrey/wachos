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
 * The MenuBar class represents a graphical user interface component that
 * provides a set of menus for the user to interact with. It typically contains
 * a collection of MenuItem objects organized within various Menu objects.
 */
public class MenuBar extends Component {

    /**
     * top-level MenuItems in the MenuBar
     */
    private final List<MenuItem> children = new ArrayList<>();

    /**
     * Looks for the MenuItem that was clicked and fires if found
     *
     * @param menuItemId the ID of the MenuItem that was clicked
     */
    @Override
    public void fireEvent(String menuItemId) {
        if (!isEnabled()) {
            return;
        }
        findMenuItemAndFireEvent(menuItemId, children);
    }

    /**
     * Recursively looks for the MenuItem that was clicked
     *
     * @param menuItemId the ID of the MenuItem that was clicked
     * @param menuItems the children to look through
     */
    private void findMenuItemAndFireEvent(String menuItemId, List<MenuItem> menuItems) {
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem mi = menuItems.get(i);
            if (mi.getId().equals(menuItemId)) {
                mi.fireClickEvent();
            }
            if (!mi.children.isEmpty()) {
                findMenuItemAndFireEvent(menuItemId, mi.children);
            }
        }
    }

    /**
     * @return the HTML used to represent this Component
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public String toHtml() {
        setSession(session, children);
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='" + getId() + "'><div id='" + getId() + "menu' class='ui-menu ui-menubar ui-widget ui-widget-content ui-corner-all ui-helper-clearfix' style='margin:0px; padding:0px; border:0px'>\n"
                + "<div tabindex='0' class='ui-helper-hidden-accessible'></div><ul class='ui-menu-list ui-helper-reset'>\n");
        for (MenuItem menuItem : children) {
            menuItem.append(layoutId, getId(), sb, menuItem);
        }
        sb.append("</ul></div><script id='" + getId() + "_s' type='text/javascript'>PrimeFaces.cw('Menubar', 'menubar', {id:'" + getId() + "menu', autoDisplay:false});</script></div>\n");
        return sb.toString();
    }

    /**
     * Initializes this component
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    @Override
    public void init(String masterId, WSession session) {
        super.init(masterId, session);
        setSession(session, children);
    }

    /**
     * Recursively sets the pushContext for the provided MenuItems
     *
     * @param session the user's session
     * @param menuItems sets the user's session for the provided MenuItems and
     * all of their children, recursively
     */
    protected static void setSession(WSession session, List<MenuItem> menuItems) {
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem mi = menuItems.get(i);
            mi.session = session;
            if (!mi.children.isEmpty()) {
                setSession(session, mi.children);
            }
        }
    }

    /**
     * @return a unique ID for this component
     */
    @Override
    public String getId() {
        return "menubar" + hashCode();
    }

    /**
     * Adds a MenuItem to this menu bar
     *
     * @param child the MenuItem to be added
     * @return this
     */
    public MenuBar addChild(MenuItem child) {
        children.add(child);
        child.updateMenuBar(this);
        redraw();
        return this;
    }

    /**
     * Adds a MenuItem at the provided index to this menu bar
     *
     * @param index the location to insert the MenuItem
     * @param child the MenuItem to be added
     * @return this
     */
    public MenuBar addChild(int index, MenuItem child) {
        children.add(index, child);
        child.updateMenuBar(this);
        redraw();
        return this;
    }

    /**
     * Removes a MenuItem from this menu bar
     *
     * @param child the MenuItem to be removed
     * @return this
     */
    public MenuBar removeChild(MenuItem child) {
        children.remove(child);
        redraw();
        return this;
    }

    /**
     * Removes a MenuItem from this menu bar
     *
     * @param index the location of the MenuItem to be removed
     * @return this
     */
    public MenuBar removeChild(int index) {
        children.remove(index);
        redraw();
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
