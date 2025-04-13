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
import java.util.ArrayList;
import java.util.List;

/**
 * ComboBox is a rectangular box that shows the current selection. When clicked,
 * a dropdown list appears that allows the user to select another value
 */
public class ComboBox extends TextComponent {

    /**
     * the options from which the user can select
     */
    List<String> options;
    /**
     * the currently selected index
     */
    private int index;

    /**
     * Constructor
     *
     * @param value this component's initial selection
     * @param options available options for the user to select from
     */
    public ComboBox(String value, List<String> options) {
        super(options.contains(value) ? value : options.isEmpty() ? "" : options.get(0));
        this.index = options.indexOf(value);
        this.options = options;
        this.inner = " > select";
        setProperty("css.max-width", "200px");
    }

    /**
     * Constructor
     *
     * @param value this component's initial selection
     * @param options available options for the user to select from
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public ComboBox(String value, List<String> options, ComponentListener valueChangedListener) {
        this(value, options);
        valueChangedListeners.add(valueChangedListener);
    }

    /**
     * Client-side user interaction has fired an event on this component
     *
     * @param indexStr the text value that has potentially changed
     */
    @Override
    public void fireEvent(String indexStr) {
        if (!isEnabled()) {
            return;
        }
        int newIndex = Integer.parseInt(indexStr);
        if (index == newIndex && (options.isEmpty() || value.equals(options.get(index)))) {
            return; //didn't change!
        }
        index = newIndex;
        value = options.get(index);
        valueChangedListeners.update(value);
    }

    /**
     * Sets the value of this component
     *
     * @param value the new value of this component
     * @param fireChangedEvent if true, executes the valueChangedListener's
     * update method
     * @param updateClient if true, updates the value in the client
     * @return this
     */
    @Override
    public TextComponent setText(String value, boolean fireChangedEvent, boolean updateClient) {
        if (this.value != null && this.value.equals(value)) {
            return this;
        }
        if (value == null || options.isEmpty()) {
            value = ""; //make sure not to set it to null or a value that's not in the combo
        }
        if (!options.isEmpty() && !options.contains(value)) {
            value = options.contains(this.value) ? this.value : options.get(0); //set the value to the first item in the list
        }
        
        this.value = value;
        if (!options.contains(value)) {
            return this;
        } else if (options.isEmpty()) {
            index = 0;
            this.value = "";
            if (updateClient && isRendered()) {
                exec("$('#slct" + getId() + "').val('');");
            }
        } else if (!options.get(index).equals(value)) {
            index = options.indexOf(value);
            if (updateClient && isRendered()) {
                exec("$('#slct" + getId() + "').prop('selectedIndex', " + index + ");");
            }
        }
        if (fireChangedEvent) {
            valueChangedListeners.update(value);
        }
        return this;
    }

    /**
     * @return the index of the currently selected value
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the selected value, according to its index
     *
     * @param index the index of the value being selected
     * @return this
     */
    public ComboBox setIndex(int index) {
        if (this.index == index && (options.isEmpty() || this.value.equals(options.get(index)))) {
            return this; //nothing to update
        }
        this.index = index;
        this.value = options.get(index);
        if (isRendered()) {
            exec("$('#slct" + getId() + "').prop('selectedIndex', " + index + ");");
        }
        valueChangedListeners.update(value);
        return this;
    }

    /**
     * @return the selection options available to the user
     */
    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    /**
     * Sets the selection options available to the user
     *
     * @param options the items that can be chosen as a value
     */
    public void setOptions(List<String> options) {
        if (options.equals(this.options)) {
            return; //nothing to set, this is already the value
        }
        this.options = new ArrayList<>(options);
        setText(value, true, false);
        if (isRendered()) {
            StringBuilder sb = new StringBuilder();
            sb.append("var $el = $('#" + getId() + " > select');\n$el.empty();\n"); //find the combo, remove old options
            for (String option : options) { //add all the new options
                sb.append("$el.append($(\"<option></option>\")" + (option.equals(value) ? ".attr(\"selected\", \"true\")" : "") + ".text(\"").append(option).append("\"));\n");
            }
            exec(sb.toString());
        }
    }

    /**
     * Adds a new option to the list of options
     *
     * @param option the option to add to the options
     */
    public void addOption(String option) {
        List<String> newOptions = getOptions();
        newOptions.add(option);
        setOptions(newOptions);
    }

    /**
     * Sets the ComboBox visibility
     *
     * @param visible flag indicating if this ComboBox is visible
     * @return this
     */
    @Override
    public ComboBox setVisible(boolean visible) {
        this.visible = visible;
        if (isRendered()) {
            exec("$('#" + getId() + "').css('visibility', '" + (visible ? "visible" : "hidden") + "');");
        }
        return this;
    }

    /**
     * Clears the options in the combo box
     */
    public void clear() {
        setOptions(new ArrayList<>());
    }

    /**
     * @return html representation of this combo box
     */
    @Override
    public String toHtml() {
        String img = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAICAMAAAGAI64SAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAABmUExURQAAAL+/v7+/v7+/vzAwMLOzs7+/v6Wlpba2toKCgre"
                + "3t7W1tbW1tb+/v5ubm729vbm5ubW1tbGxsb+/v7W1tbm5ub+/v7+/v05OTr+/v0VFRb+/v7+/v3p6egAAACcnJwMDA729vRsUDmsAAAAidFJOUwBcCBD/xyj07fShludQ8PWL9/QM0YAUHP0k/DT/8v////kKIvC"
                + "NAAAACXBIWXMAABcRAAAXEQHKJvM/AAAAR0lEQVQYVyWHBw6AQAzDzN4ce5YC//8kPREpjsP6IrCB2ExQ6z0CldWJyE4Ya24nOQxQesyFWq7Oe5M92gbeIO2H8zeIFoAPvJkDctQGBXwAAAAASUVORK5CYII=";
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='" + getId() + "' style=\"position:relative; display: inline-block; visibility: " + (visible ? "visible" : "hidden") + "\">\n"
                + "<img src=\"" + img + "\" style=\"pointer-events:none; position:absolute; top:42%; right: 4px; z-index:auto\"></img>\n"
                + "<select id='slct" + getId() + "' " + getProperties() + "style=\"" + getStyle() + "\" class='ui-inputfield ui-state-default ui-corner-all myselect " + (enabled ? "" : "ui-state-disabled") + "' onchange=\"changed" + layoutId + "({id: '" + getId() + "', value: this.selectedIndex});\">");
        for (int i = 0; i < options.size(); i++) {//String option : getOptions()) {
            String option = options.get(i);
            boolean selected = getText().equals(option);
            sb.append("<option").append(selected ? " selected=\"true\"" : "").append(" class=\"ui-selectonemenu-item ui-selectonemenu-list-item ui-corner-all\">").append(option.replace("\"", "&quot;").replace("\\", "\\\\")).append("</option>");
        }
        sb.append("</select></div>");
        return sb.toString();
    }

}
