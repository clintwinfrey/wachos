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
import gov.mil.navy.nswcdd.wachos.components.ComponentListeners;
import gov.mil.navy.nswcdd.wachos.components.Tabs;
import gov.mil.navy.nswcdd.wachos.components.TreeView;
import gov.mil.navy.nswcdd.wachos.components.text.DropButton;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.ArrayList;
import java.util.List;

/**
 * A Layout is similar to a "Panel" in Swing or a "Pane" in JavaFX; you put your
 * components inside the layout, which will handle how those components should
 * be displayed
 *
 * @param <T> the type of Layout
 */
public abstract class Layout<T extends Layout> extends Component<T> {

    /**
     * padding in pixels around this grid
     */
    protected int padding = 0;
    /**
     * the title of this layout
     */
    private String title = "";
    /**
     * if there is a border title, then a border with a title will be displayed
     * around this Layout
     */
    public TextComponent borderTitle;
    /**
     * should we draw a border around this?
     */
    public boolean showBorderLine;
    /**
     * all of the components that will be inside of this layout when rendered
     */
    protected final List<Component> components = new ArrayList<>();
    /**
     * all of the Dialogs that were created by this Layout
     */
    protected final List<Dialog> dialogs = new ArrayList<>();
    /**
     * listen for title changes
     */
    public final ComponentListeners titleListeners = new ComponentListeners();

    /**
     * Initializes this component with the ID of the master layout and session
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    @Override
    public void init(String masterId, WSession session) {
        super.init(masterId, session);
        for (Component component : components) {
            component.init(masterId, session);
        }
        for (Dialog dialog : dialogs) {
            dialog.init(masterId, session);
        }
        if (borderTitle != null) {
            borderTitle.init(masterId, session);
        }
    }

    /**
     * @return the layout's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this layout
     * @param title the desired title of the layout
     * @return this
     */
    public T setTitle(String title) {
        this.title = title;
        if (borderTitle != null) {
            borderTitle.setText(title);
        }
        titleListeners.update(title);
        return (T) this;
    }

    /**
     * Creates a border around this layout, with a title at the top
     *
     * @param borderTitle the title of the border
     * @param showBorderLine if true, draws a line around the entire layout
     * @return this
     */
    public T setBorder(String borderTitle, boolean showBorderLine) {
        return setBorder(new Label(borderTitle), showBorderLine);
    }

    /**
     * Creates a border around this layout, with a title at the top
     *
     * @param borderTitle the title of the border
     * @param showBorderLine if true, draws a line around the entire layout
     * @return this
     */
    public T setBorder(TextComponent borderTitle, boolean showBorderLine) {
        if (borderTitle == null) {
            this.borderTitle = null;
        } else {
            this.borderTitle = borderTitle;
            this.borderTitle.valueChangedListeners.add(value -> title = value);
            this.borderTitle.setProperty("css.font-size", ".7em !important");
            this.borderTitle.init(layoutId, session);
            setTitle(borderTitle.getText());
        }
        this.showBorderLine = showBorderLine;
        return (T) this;
    }

    /**
     * Sets the padding in pixels around this layout
     *
     * @param padding pixels around this layout
     * @return this
     */
    public T setPadding(int padding) {
        this.padding = padding;
        if (isRendered()) {
            exec("$('#" + getId() + "').css('padding', '" + padding + "px');");
        }
        return (T) this;
    }

    /**
     * Forces this element to be updated visually
     */
    @Override
    public void redraw() {
        if (isRendered()) { //if it's been drawn already, we need to update it
            String html = toHtml().replace("#LAYOUT_ID#", layoutId).replace("\n", "\\n");
            if (html.contains("`")) {
                html = html.replace("`", "w@c#0s");
                exec("var redraw" + getId() + " = `" + html + "`.replaceAll('w@c#0s', '`');\n"
                        + "$('#" + getId() + "').replaceWith(redraw" + getId() + ");");
            } else {
                exec("$('#" + getId() + "').replaceWith(`" + html + "`);");
            }
        }
    }

    /**
     * @return the number of components in this layout
     */
    public int size() {
        return components.size();
    }

    /**
     * @return the components to display in this layout
     */
    public List<Component> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * @return this object's unique identification
     */
    @Override
    public String getId() {
        return "layout" + hashCode();
    }

    /**
     * disposes all components in this Layout and removes them
     */
    @Override
    public void dispose() {
        //remove all the dialogs
        for (int i = dialogs.size() - 1; i >= 0; i--) {
            dialogs.get(i).dispose(); //dispose removes this Dialog from this Layout; no need to clear at the end
        }
        //dispose all the components and then clear the components list
        for (Component component : components) {
            component.dispose();
        }
        //make sure the border title is cleared out as well
        if (borderTitle != null) {
            borderTitle.dispose();
        }
        components.clear();
        titleListeners.clear();
    }

    /**
     * Creates a Dialog with the given title; note that if this Layout is not
     * added to the application, the Dialog cannot be shown
     *
     * @param title the banner title of this Dialog
     * @return a Dialog with the given title
     */
    public Dialog createDialog(String title) {
        Dialog dialog = new Dialog(title, session, this);
        dialog.init(layoutId, session);
        return dialog;
    }

    /**
     * Creates a Dialog without a title, in the center of the screen; note that
     * if this Layout is not added to the application, the Splash cannot be
     * shown
     *
     * @param content the component to show
     * @return a Splash with the given content
     */
    public Splash createSplash(Component content) {
        Splash splash = new Splash(session, this);
        splash.init(layoutId, session);
        splash.getContent().add(content);
        splash.open();
        return splash;
    }

    /**
     * Allows Dialog to remove itself
     *
     * @param dialog the item to be removed
     */
    protected void removeDialog(Dialog dialog) {
        dialogs.remove(dialog);
    }

    /**
     * Finds the component by the given ID and fires an event
     *
     * @param componentId the ID of the component
     * @param value the new value to fire for the event
     */
    public void fireEvent(String componentId, String value) {
        if (!isEnabled()) {
            return;
        }
        findComponentAndFireEvent(this, componentId, value);
    }

    /**
     * Finds the component by the given ID and fires an event
     *
     * @param layout the owner of the component
     * @param componentId the ID of the component
     * @param value the new value to fire for the event
     * @return true if we found it, otherwise false; we do this so we can
     * recursively call and stop as soon as we find the right component
     */
    private boolean findComponentAndFireEvent(Layout<?> layout, String componentId, String value) {
        //see if the layout is the thing that threw the event
        if (layout.getId().equals(componentId)) {
            layout.fireEvent(value);
            return true;
        }

        //look through this layout's dialogs for the component
        for (Dialog dialog : layout.dialogs) {
            if (dialog.getId().equals(componentId)) {
                dialog.close(); //it's a close event
                return true;
            } else if (findComponentAndFireEvent(dialog.content, componentId, value)) {
                return true;
            }
        }

        //look through the layout's components
        for (Component component : layout.getComponents()) {
            if (component.getId().equals(componentId)) {
                component.fireEvent(value);
                return true;
            } else if (component instanceof DropButton && ((DropButton) component).menu.getId().equals(componentId)) {
                ((DropButton) component).menu.fireEvent(value);
                return true; //found it, stop looking
            } else if (component instanceof Tabs) {
                if (((Tabs) component).plusMenu.getId().equals(componentId)) {
                    ((Tabs) component).plusMenu.fireEvent(value);
                    return true; //found it, stop looking
                } else if (((Tabs) component).tabMenu.getId().equals(componentId)) {
                    ((Tabs) component).tabMenu.fireEvent(value);
                    return true; //found it, stop looking
                } else {
                    for (Tabs.Tab tab : ((Tabs) component).tabs) {
                        if (tab.label.getId().equals(componentId)) {
                            tab.label.fireEvent(value);
                            return true;
                        } else if (findComponentAndFireEvent(tab.layout, componentId, value)) {
                            return true; //stop looking, we found the layout that contains this componentId
                        }
                    }
                }
            } else if (component instanceof TreeView) {
                for (Component nodeComponent : ((TreeView) component).getNodeComponents()) {
                    if (nodeComponent.getId().equals(componentId)) {
                        nodeComponent.fireEvent(value);
                        return true;
                    } else if (nodeComponent instanceof Layout && findComponentAndFireEvent((Layout) nodeComponent, componentId, value)) {
                        return true;
                    }
                }
            } else if (component instanceof Layout && findComponentAndFireEvent((Layout) component, componentId, value)) {
                return true; //stop looking, we found and updated recursively!
            }
        }
        return false;
    }

    /**
     * Drawing 3D widgets in WCDocker can cause problems, so this helps to
     * identify how the widget should be drawn in the docker
     *
     * @return false by default, but override if using a 3D widget
     */
    public boolean isWebGL() {
        return false;
    }

}
