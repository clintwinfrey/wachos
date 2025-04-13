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

import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Component class serves as the foundation for creating WACHOS graphical
 * user interface (GUI) components. It encapsulates common functionality and
 * properties shared among various GUI elements.
 *
 * <p>
 * This class provides a template for creating custom GUI components by offering
 * methods and attributes that are essential for rendering, event handling, and
 * overall interaction within a graphical user interface.</p>
 *
 * <p>
 * <strong>Key Features:</strong></p>
 * <ul>
 * <li>Size and Position Management: Set and retrieve dimensions and coordinates
 * of the component.</li>
 * <li>Rendering: Abstract methods for rendering the component, allowing
 * customization in subclasses.</li>
 * <li>Event Handling: Basic support for handling mouse and keyboard
 * events.</li>
 * <li>Visibility Control: Toggle the visibility of the component.</li>
 * </ul>
 *
 * <p>
 * <strong>Notes:</strong></p>
 * <ul>
 * <li>This class is intended to be extended for creating specific GUI
 * components. The 'toHtml' method is key for defining how a component functions
 * and is visualized.</li>
 * <li>Override the rendering methods for custom appearance and behavior.</li>
 * <li>Event handling can be further customized to respond to user
 * interactions.</li>
 * </ul>
 *
 * @param <T> the type of Component that this extends
 */
public abstract class Component<T extends Component> implements Serializable {

    /**
     * HTML properties for this component
     */
    protected final Map<String, String> properties = new HashMap<>();
    /**
     * identification of the master layout this is drawn on
     */
    public String layoutId = "#LAYOUT_ID#";
    /**
     * the vertical alignment of this component (top, center, bottom), set by
     * calling the correlating align methods
     */
    public String valign = null;
    /**
     * the horizontal alignment of this component (left, center, right), set by
     * calling the correlating align methods
     */
    public String halign = null;
    /**
     * handles whether this thing is drawn or not
     */
    protected boolean visible = true;
    /**
     * flag indicating if the user can interact with this component
     */
    protected boolean enabled = true;
    /**
     * the user's session
     */
    public WSession session;
    /**
     * the tip that's shown when hovering over the component
     */
    private String tooltip = "";
    /**
     * some items have an inner type, such as "> table" or "> button"
     */
    protected String inner = "";
    /**
     * degrees to rotate this Component
     */
    private int rotation = 0;

    /**
     * Initializes this component with the ID of the master layout and session
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    public void init(String masterId, WSession session) {
        if (!masterId.equals("#LAYOUT_ID#")) { //"#LAYOUT_ID#" isn't a valid master ID so don't use it
            layoutId = masterId;
        }
        if (session != null) {
            this.session = session;
        }
    }

    /**
     * @return a unique ID for this component
     */
    public String getId() {
        return "cpt" + hashCode();
    }

    /**
     * @return the HTML used to represent this Component
     */
    public abstract String toHtml();

    /**
     * Aligns this element to the top in the parent layout
     *
     * @return this Component
     */
    public T alignTop() {
        valign = "top";
        redraw();
        return (T) this;
    }

    /**
     * Aligns this element to the center (vertically) in the parent layout
     *
     * @return this Component
     */
    public T alignCenterV() {
        valign = "center";
        redraw();
        return (T) this;
    }

    /**
     * Aligns this element to the bottom in the parent layout
     *
     * @return this Component
     */
    public T alignBottom() {
        valign = "bottom";
        redraw();
        return (T) this;
    }

    /**
     * Aligns this element to the left in the parent layout
     *
     * @return this Component
     */
    public T alignLeft() {
        halign = "left";
        redraw();
        return (T) this;
    }

    /**
     * Aligns this element to the center (horizontally) in the parent layout
     *
     * @return this Component
     */
    public T alignCenterH() {
        halign = "center";
        redraw();
        return (T) this;
    }

    /**
     * Aligns this element to the right in the parent layout
     *
     * @return this Component
     */
    public T alignRight() {
        halign = "right";
        redraw();
        return (T) this;
    }

    /**
     * Sets this component's visibility
     *
     * @param visible flag indicating if this component is to be shown
     * @return this Component
     */
    public T setVisible(boolean visible) {
        this.visible = visible;
        this.setProperty("css.visibility", visible ? "visible" : "hidden");
        return (T) this;
    }

    /**
     * @return flag indicating if this component is to be shown
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Forces this element to be updated visually; by default, this will remove
     * the component from the DOM and put a fresh copy in its place
     */
    public void redraw() {
        if (this.isRendered()) {
            exec("$('#" + getId() + "').replaceWith(\"" + toHtml().replace("\"", "\\\"").replace("\n", "\\n") + "\");");
        }
    }

    /**
     * Gets the String value of the given property
     *
     * @param propertyId the identification of the property to provide
     * @return the value of the property
     */
    public String getProperty(String propertyId) {
        String property = properties.get(propertyId);
        return property == null ? "" : property;
    }

    /**
     * Sets the property of the given ID with the given value
     *
     * @param id the identification of the property to set
     * @param value the value of the property to set
     * @return this component
     */
    public final T setProperty(String id, String value) {
        return setProperty(id, value, true);
    }

    /**
     * Gets the String value of the given style
     *
     * @param cssId the identification of the style to provide
     * @return the value of the style
     */
    public String getStyle(String cssId) {
        String style = properties.get("css." + cssId);
        return style == null ? "" : style;
    }

    /**
     * Sets the style of the given ID with the given value
     *
     * @param id the identification of the style to set
     * @param value the value of the style to set
     * @return this component
     */
    public final T setStyle(String id, String value) {
        return setProperty("css." + id, value, true);
    }

    /**
     * Sets the property of the given ID with the given value
     *
     * @param id the identification of the property to set
     * @param value the value of the property to set
     * @param updateClient if true, updates the value in the client
     * @return this component
     */
    public T setProperty(String id, String value, boolean updateClient) {
        value = value.replace("\\", "\\\\");
        if (properties.get(id) != null && properties.get(id).equals(value)) {
            return (T) this; //nothing changed, leave me alone
        }
        properties.put(id, value);
        if (!updateClient || !isRendered()) {
            return (T) this; //the item hasn't been initialized yet, so don't bother doing anything with it
        }
        String propStr = id.contains(".") ? id.split("\\.")[0] + "('" + id.split("\\.")[1] + "', '" : id + "('";
        exec("$('#" + getId() + inner + "')." + propStr + value.replace("&#13;", "\\n").replace("&apos;", "\\'") + "')");
        return (T) this;
    }

    /**
     * Sets the foreground color of this Component; use null to remove coloring
     *
     * @param color the foreground color
     * @return this Component
     */
    public T setColor(Color color) {
        return setProperty("css.color", WTools.toHex(color));
    }

    /**
     * @return the foreground color of this Component; null indicates that there
     * is no foreground color
     */
    public Color getColor() {
        return WTools.fromHex(getProperty("css.color"));
    }

    /**
     * Sets the background color of this Component; use null to remove coloring
     *
     * @param background the background color
     * @return this Component
     */
    public T setBackground(Color background) {
        return setProperty("css.background-color", WTools.toHex(background));
    }

    /**
     * @return the background color of this Component; null indicates that there
     * is no background color
     */
    public Color getBackground() {
        return WTools.fromHex(getProperty("css.background-color"));
    }

    /**
     * Sets the desired width of this component, using px, %, etc.
     *
     * @param width the desired width to use
     * @return this Component
     */
    public T setWidth(String width) {
        return (T) setProperty("css.width", width);
    }

    /**
     * @return the width of this component
     */
    public String getWidth() {
        return properties.get("css.width");
    }

    /**
     * Sets the desired height of this component, using px, %, etc.
     *
     * @param height the desired height to use
     * @return this Component
     */
    public T setHeight(String height) {
        return (T) setProperty("css.height", height);
    }

    /**
     * @return the height of this component
     */
    public String getHeight() {
        return properties.get("css.height");
    }

    /**
     * Rotates the Component by the provided number of degrees
     *
     * @param degrees the amount to rotate the Component
     * @return this Component
     */
    public T setRotation(int degrees) {
        this.rotation = degrees;
        if (this.isRendered()) {
            exec("$('#" + getId() + inner + "').css({'display': 'inline-block', 'transform': 'rotate(" + degrees + "deg)', 'transform-origin': 'top left'});");
        }
        return (T) this;
    }

    /**
     * Retrieves the alignment for this component
     *
     * @return the alignment styling
     */
    public String getAlignment() {
        return (halign == null ? "" : "align=\"" + halign + "\"") + (valign == null ? "" : (halign != null ? " " : "") + valign == null ? "" : "valign=\"" + valign + "\"") + " ";
    }

    /**
     * Tells us if this has been rendered in the HTML yet
     *
     * @return flag indicating if it's drawn yet
     */
    public boolean isRendered() {
        return !layoutId.equals("#LAYOUT_ID#");
    }

    /**
     * @return this component's tooltip text
     */
    public String getToolTip() {
        return tooltip;
    }

    /**
     * Sets this component's tooltip text, to be shown on hover
     *
     * @param tooltip the text to be shown
     * @return this Component
     */
    public T setToolTip(String tooltip) {
        if (tooltip == null) {
            tooltip = "";
        }
        if (this.tooltip.equals(tooltip)) {
            return (T) this;
        }
        this.tooltip = tooltip;
        if (tooltip.equals("")) {
            tooltip = " "; //prevents a bug in qtip2 where "" doesn't replace an old tooltip; prevents an old tooltip from showing up that's incorrect, because delay will be 999999999
        }
        if (isRendered()) {
            //a Layout might have a border title, and that's where its tooltip is supposed to go
            if (this instanceof Layout) {
                Layout layout = (Layout) this;
                if (layout.borderTitle != null) {
                    exec("$jq('#" + layout.borderTitle.getId() + "').qtip({ content: \"" + tooltip.replace("\"", "\\\"").replace("\n", "<br/>") + "\", show: { delay: " + (tooltip.equals(" ") ? "999999999" : "1000") + " }, style: { widget: true,  def: true }});");
                }
            } else {
                //everyone else just sets the tooltip property like normal
                exec("$jq('#" + getId() + "').qtip({ content: \"" + tooltip.replace("\"", "\\\"").replace("\n", "<br/>") + "\", show: { delay: " + (tooltip.equals(" ") ? "999999999" : "1000") + " }, style: { widget: true,  def: true }});");
            }
        }
        return (T) this;
    }

    /**
     * Enables this component for user interaction
     *
     * @param enabled flag indicating if the user can interact with this
     * component
     * @return this Component
     */
    public T setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return (T) this;
        }
        this.enabled = enabled;
        if (isRendered()) {
            exec("$('#" + getId() + "')." + (enabled ? "remove" : "add") + "Class('ui-state-disabled');");
        }
        return (T) this;
    }

    /**
     * @return flag indicating if the user can interact with this component
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return a String of inline css for this Component, excluding the 'style='
     * and quotes
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    protected String getStyle() {
        StringBuilder style = new StringBuilder();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            if (property.getKey().startsWith("css.")) {
                String value = property.getValue();
                if (value != null && !value.trim().equals("")) {
                    style.append(property.getKey().substring(4) + ": " + property.getValue() + "; ");
                }
            }
        }
        if (rotation != 0) {
            style.append("display: inline-block; transform: rotate(" + rotation + "deg); transform-origin:  top left;");
        }
        return style.toString();
    }

    /**
     * @return a String of inline properties for this Component
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    protected String getProperties() {
        StringBuilder props = new StringBuilder();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            if (!property.getKey().startsWith("css.")) {
                String value = property.getValue();
                if (value != null && !value.trim().equals("")) {
                    props.append(property.getKey() + "=\"" + property.getValue() + "\" ");
                }
            }
        }
        return props.toString();
    }

    /**
     * Fires an event on this component; does nothing by default
     *
     * @param event the event information
     */
    public void fireEvent(String event) {
    }

    /**
     * Creates a String that can be used to execute an event
     *
     * @param value the value of the event
     * @return a String as follows: "changed" + layoutId + "({id: '" + getId() +
     * "', value: " + value + "});"
     */
    protected String createEvent(String value) {
        return "changed" + layoutId + "({id: '" + getId() + "', value: " + value + "});";
    }

    /**
     * @return the HTML that represents this component
     */
    @Override
    public String toString() {
        return toHtml();
    }

    /**
     * To be called when this component is no longer needed; does nothing by
     * default
     */
    public void dispose() {
    }

    /**
     * Execute the provided JavaScript code
     *
     * @param javascript the JavaScript to execute
     */
    public void exec(String javascript) {
        if (session == null) {
            session = WSession.getSession(); //can happen if the component hasn't been added to a layout yet
        }
        session.exec(javascript);
    }

    /**
     * Execute the provided JavaScript code
     *
     * @param javascript the JavaScript to execute
     * @param milliDelay milliseconds before executing this script
     */
    public void exec(String javascript, long milliDelay) {
        if (session == null) {
            session = WSession.getSession(); //can happen if the component hasn't been added to a layout yet
        }
        session.exec(javascript, milliDelay);
    }
}
