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
import gov.mil.navy.nswcdd.wachos.components.ComponentListener;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Grid is a layout where all of the Components get placed in order of
 * appearance. The ordering is left to right, with a new row being created
 * whenever the number of items in a row matches the specified number of
 * columns.
 *
 * @param <T> the type of Grid
 */
public class Grid<T extends Grid> extends Layout<T> {

    /**
     * the number of items in a row before a new row is created
     */
    protected int columns;
    /**
     * the number of pixels between each Component, horizontally
     */
    protected int hSpacing = 3;
    /**
     * the number of pixels between each Component, vertically
     */
    protected int vSpacing = 3;
    /**
     * the desired behavior when content does not fit in the parent element box
     * (overflows) in the horizontal and/or vertical direction
     */
    protected String overflow = "";
    /**
     * flag indicating if this is supposed to be viewed like a table (i.e. there
     * is a border, a header row, etc.)
     */
    protected boolean isTable = false;
    /**
     * the message that gets displayed if grid data has been filtered out by the
     * user (i.e. we are only showing a subset of rows, or some columns are
     * missing)
     */
    protected String filterMessage = "";
    /**
     * if not null, you can select a row in a table
     */
    protected ComponentListener rowSelectListener;
    /**
     * if a row can be selected, the selection colors must have been set
     */
    protected static String ROW_SELECTION_STYLE = "<style>\n.selected {\n    color: #" + WTools.toHex(Color.WHITE)
            + ";\n    background-color: #" + WTools.toHex(Color.DARK_GRAY) + ";\n}\n</style>\n";

    /**
     * Constructor
     *
     * @param columns the number of items in a row before a new row is created
     * @param components all of the Components to show in this Grid
     */
    public Grid(int columns, Component... components) {
        this.components.addAll(Arrays.asList(components));
        this.columns = columns;
        this.inner = " > table";
    }

    /**
     * Add components to this layout, as an array
     *
     * @param components the Components to add
     */
    public void add(Component... components) {
        add(Arrays.asList(components));
    }

    /**
     * Add components to this layout, as a List
     *
     * @param components the Components to add
     */
    public void add(List<Component> components) {
        add(this.components.size(), components);
    }

    /**
     * Add components to this layout, starting at the given index, as an array
     *
     * @param index the place to put these components
     * @param components the Components to add
     */
    public void add(int index, Component... components) {
        add(index, Arrays.asList(components));
    }

    /**
     * Add components to this layout, starting at the given index, as a List
     *
     * @param index the place to put these components
     * @param components the Components to add
     */
    public void add(int index, List<Component> components) {
        this.components.addAll(index, components);
        for (Component component : components) {
            component.init(layoutId, session);
        }
        redraw();
        WTools.initToolTips(components, session);
    }

    /**
     * Remove these components (as an array) from the layout
     *
     * @param components the components to remove
     */
    public void remove(Component... components) {
        remove(Arrays.asList(components));
    }

    /**
     * Remove these components (as a List) from the layout
     *
     * @param components the components to remove
     */
    public void remove(List<Component> components) {
        components.removeAll(components);
        redraw();
    }

    /**
     * Remove components at these indices from the layout
     *
     * @param indices the components to remove
     */
    public void remove(int... indices) {
        List<Component> remove = new ArrayList<>();
        for (int index : indices) {
            if (index < components.size()) {
                remove.add(components.get(index));
            }
        }
        remove(remove);
    }

    /**
     * Clears all of the components from the layout
     */
    public void removeAll() {
        remove(components);
    }

    /**
     * Sets the components in this layout to be the ones provided in this array
     * of Components
     *
     * @param components the Components to display in this layout
     */
    public void setComponents(Component... components) {
        setComponents(Arrays.asList(components));
    }

    /**
     * Sets the components in this layout to be the ones provided in this List
     * of Components
     *
     * @param components the Components to display in this layout
     */
    public void setComponents(List<Component> components) {
        removeAll();
        this.components.addAll(components);
        redraw();
    }

    /**
     * Sets the number of pixels between each Component, horizontally
     *
     * @param pixels the amount of space between each Component
     * @return this
     */
    public Grid<T> setHorizontalSpacing(int pixels) {
        this.hSpacing = pixels;
        return this;
    }

    /**
     * Sets the number of pixels between each Component, vertically
     *
     * @param pixels the amount of space between each Component
     * @return this
     */
    public Grid<T> setVerticalSpacing(int pixels) {
        this.vSpacing = pixels;
        return this;
    }

    /**
     * Sets the desired behavior when content does not fit in the parent element
     * box (overflows) in the horizontal and/or vertical direction
     *
     * @param overflow e.g. visible, hidden, etc.
     * @return this
     */
    public Grid<T> setOverflow(String overflow) {
        this.overflow = "; overflow: " + overflow;
        return this;
    }

    /**
     * @return the HTML of this Grid
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public String toHtml() {
        StringBuilder html = new StringBuilder();

        //if it's a table, only draw the components that are actually visible
        List<Component> visibleComponents = this instanceof CardLayout ? new ArrayList<>() : new ArrayList<>(components);
        if (this instanceof CardLayout) {
            for (Component component : components) {
                if (component.isVisible()) {
                    visibleComponents.add(component);
                }
            }
        }

        //if there's nothing to draw, "make" something to draw, even though it's empty; we need this in case we add a component in later
        if (visibleComponents.isEmpty()) {
            visibleComponents.add(new Label("") {
                @Override
                public String toHtml() {
                    return "";
                }
            });
        }

        //HBox always has the same number of columns as components to draw
        if (this instanceof HBox) {
            columns = visibleComponents.size();
        }

        //start the div
        html.append("<div id=\"" + getId() + "\" style='height: 100%; width: 100%; padding: " + padding + "px; box-sizing: border-box" + overflow + "'>");
        if (borderTitle != null) {
            String width = getWidth() == null ? (showBorderLine ? "calc(100%-6px)" : "0") : getWidth();
            String fieldsetStyle = showBorderLine ? "class='ui-widget-content' style='border-radius:3px; padding:3px; width: " + width + "'" : "style='border-radius:0px; padding:0px; border: 0px; width: " + width + "'";
            html.append("<fieldset " + fieldsetStyle + "><legend id='lgnd" + getId() + "'>" + borderTitle.toHtml() + "</legend>");
        }

        //start the table div
        String alignment = halign == null || halign.equals("left") ? "" : halign.equals("right") ? " margin-left: auto;" : "margin: 0px auto;";
        html.append("<table id=\"table" + getId() + "\" class=\"tf" + (isTable ? "table" : "grid") + "\" style=\"" + getStyle() + alignment + "\">");

        //this table has been filtered, we need to tell the user!
        if (!filterMessage.equals("")) {
            html.append("<tr><td colspan=\"" + columns + "\" class=\"ui-widget\">" + filterMessage + "</td></tr>");
        }

        //create all of the rows in this grid
        int componentIndex = 0; //which component are we on?
        while (columns != 0 && componentIndex < visibleComponents.size()) {
            html.append("<tr>"); //start the row
            boolean topRow = componentIndex < columns;

            for (int i = 0; i < columns; i++) {
                if (visibleComponents.size() > componentIndex) {
                    Component c = visibleComponents.get(componentIndex);
                    String type = ((topRow && isTable) ? "th" : "td"); //if it's the top row and this is a table, use "th"; otherwise, use "td"
                    //this next line of code is where we place the Component
                    String cBackground = c.getProperty("css.background-color");
                    html.append("<" + (type.startsWith("th") && cBackground.equals("") ? type + " class=\"ui-state-default\"" : type) + (isTable && !cBackground.equals("") ? " bgcolor=\"" + cBackground + "\"" : "")
                            + " " + getStyle(c, topRow, i == 0, type.startsWith("th")) + ">" + c.toHtml() + "</" + type + ">");
                } else {
                    i = columns; //new column!
                }
                componentIndex++;
            }
            html.append("</tr>"); //finish the row
        }

        html.append("</table>"); //end the table
        if (isTable && rowSelectListener != null) {
            html.append(ROW_SELECTION_STYLE
                    + "<script>\n"
                    + "var clickIndex = 0;\n"
                    + "var table" + getId() + " = document.getElementById('" + getId() + "'),\n"
                    + "    selected" + getId() + " = table" + getId() + ".getElementsByClassName('selected');\n"
                    + "table" + getId() + ".onclick = highlight" + getId() + ";\n"
                    + "function highlight" + getId() + "(e) {\n"
                    + "    if (selected" + getId() + "[0]) selected" + getId() + "[0].className = '';\n"
                    + "    if (clickIndex !== -1) {\n"
                    + "        e.target.parentNode.className = 'selected';\n"
                    + "    }\n"
                    + "    tableRowSelected" + getId() + "([{name:'index', value:clickIndex}]);" //calls the p:remoteCommand
                    + "}\n"
                    + "$('#" + getId() + "').find('tr').eq(0).click( function(){\n"
                    + "clickIndex = ($(this).index()-1);\n"
                    + "});\n"
                    + "</script>\n");
        }
        if (borderTitle != null) {
            html.append("</fieldset>");
        }
        html.append("</div>");
        return html.toString();
    }

    /**
     * Returns the styling for the th or td that this component will go into
     *
     * @param component the component around which to style
     * @param topRow are we on the top row? if so, don't pad the top
     * @param leftCol are we in the left column? if so, don't pad the left
     * @param header is this a header? if so, we're gonna make it sticky!
     * @return the styling for this th or td
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    protected String getStyle(Component component, boolean topRow, boolean leftCol, boolean header) {
        String objStyle = component.getAlignment();
        StringBuilder style = new StringBuilder();
        if (isTable) {
            style.append(header ? "position:sticky;top:0;" : ""); //table header should be sticky
        } else {
            style.append("padding:" + (topRow ? "0" : vSpacing + "px") + " 0  0 " + (leftCol ? "0;" : hSpacing + "px;"));
        }
        if (component.getWidth() != null) {
            style.append("width: " + component.getWidth() + ";");
        }
        if (component.getHeight() != null) {
            style.append("height: " + component.getHeight() + ";");
        }
        if (style.toString().equals("")) {
            return objStyle;
        } else if (objStyle.equals("")) {
            return "style=\"" + style.toString() + "\"";
        } else if (objStyle.contains("style=\"")) {
            return objStyle.replaceFirst("style=\"", "style=\"" + style.toString());
        } else {
            return objStyle + " style=\"" + style + "\"";
        }
    }

}
