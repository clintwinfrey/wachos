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

import java.util.ArrayList;
import java.util.List;

/**
 * ListBox is a list of items where the user can select zero or more items
 */
public class ListBox extends Component<ListBox> {

    /**
     * the selected values
     */
    protected List<Integer> selectedIndices = new ArrayList<>();
    /**
     * the things that can be selected in this ListBox
     */
    List<String> options = new ArrayList<>();
    /**
     * listens for when the available options change
     */
    public final ComponentListeners optionsChangedListeners = new ComponentListeners();
    /**
     * listens for when the selected options change
     */
    public final ComponentListeners selectionsChangedListeners = new ComponentListeners();
    /**
     * listens for double-click event
     */
    public final ComponentListeners doubleClickListeners = new ComponentListeners();

    /**
     * Constructor
     */
    public ListBox() {
        this(new ArrayList<>());
    }

    /**
     * Constructor
     *
     * @param options the things that can be selected in this ListBox
     */
    public ListBox(List<String> options) {
        this.options = options;
    }

    /**
     * @return the indices that are currently selected by the user
     */
    public List<Integer> getSelectedIndices() {
        return new ArrayList<>(selectedIndices);
    }

    /**
     * Programmatically selects the indices of the available options
     *
     * @param selectedIndices the integer indices to select (0 = first index)
     */
    public void setSelectedIndices(List<Integer> selectedIndices) {
        setOptionsAndSelectedIndices(options, selectedIndices);
    }

    /**
     * Provides a list of the items that are currently selected
     *
     * @return a List of Strings that match the selected items
     */
    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        for (Integer selectedIndex : selectedIndices) {
            selectedItems.add(options.get(selectedIndex));
        }
        return selectedItems;
    }

    /**
     * Sets the items that are selected to match the provided list of Strings
     *
     * @param selectedItems the items to select
     * @return this
     */
    public ListBox setSelectedItems(List<String> selectedItems) {
        setOptionsAndSelectedItems(options, selectedItems);
        return this;
    }

    /**
     * @return the selection options available to the user
     */
    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    /**
     * Sets the selection options available to the user and retains selections
     * for previously selected options
     *
     * @param options the items that can be chosen as a value
     */
    public void setOptions(List<String> options) {
        //determine which options were previously selected
        List<String> oldSelectedOptions = new ArrayList<>();
        for (int selectedIndex : selectedIndices) {
            oldSelectedOptions.add(this.options.get(selectedIndex));
        }
        //update selection indices to match new positions of previously selected options (if they are still in the new options)
        List<Integer> newSelectedIndices = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            if (oldSelectedOptions.contains(options.get(i))) {
                newSelectedIndices.add(i);
            }
        }
        setOptionsAndSelectedIndices(options, newSelectedIndices);
    }

    /**
     * Programmatically sets the options and selects indices in this ListBox
     *
     * @param options the options available in this ListBox
     * @param selectedIndices the indices of options to select
     */
    public void setOptionsAndSelectedIndices(List<String> options, List<Integer> selectedIndices) {
        setValuesInServer(options, selectedIndices);
        setValuesInClient(options, selectedIndices);
    }

    /**
     * Programmatically sets the options and selects items in this ListBox
     *
     * @param options the options available in this ListBox
     * @param selectedItems the names of options to select
     */
    public void setOptionsAndSelectedItems(List<String> options, List<String> selectedItems) {
        List<Integer> calculatedIndices = new ArrayList<>();
        for (String selectedItem : selectedItems) {
            calculatedIndices.add(options.indexOf(selectedItem));
        }
        setOptionsAndSelectedIndices(options, calculatedIndices);
    }

    /**
     * Sets the options and selected indices on the server side
     *
     * @param options the options that can be selected
     * @param selectedIndices the indices of the options to select
     */
    public void setValuesInServer(List<String> options, List<Integer> selectedIndices) {
        //fire options listener event
        if (!options.equals(this.options)) {
            this.options = options; //set the value of options
            optionsChangedListeners.update(options.toString());
        }

        //fire selections listener event
        if (!selectedIndices.equals(this.selectedIndices)) {
            this.selectedIndices = selectedIndices; //set the selected indices
            selectionsChangedListeners.update(selectedIndices.toString());
        }
    }

    /**
     * Updates the client with the given options and selections
     *
     * @param options items in the list that can be selected
     * @param selectedIndices indices of each item that is selected
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public void setValuesInClient(List<String> options, List<Integer> selectedIndices) {
        if (isRendered()) {
            StringBuilder sb = new StringBuilder();
            sb.append("var $el = $('#slct" + getId() + "');\n$el.empty();\n"); //find the combo, remove old options
            for (int i = 0; i < options.size(); i++) {
                sb.append("$el.append($(\"<option></option>\")" + (selectedIndices.contains(i) ? ".attr(\"selected\", \"true\")" : "") + ".text(\"").append(options.get(i).replace("\"", "&quot;").replace("\\", "\\\\")).append("\"));\n");
            }
            exec(sb.toString());
        }
    }

    /**
     * Fires the event that was triggered by the user
     *
     * @param value the event value
     */
    @Override
    @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
    public void fireEvent(String value) {
        if (!isEnabled()) {
            return;
        }
        if (value.startsWith("selection")) {
            value = value.replace("selection", "").trim();
            String[] indices = value.split("\\s+");
            List<Integer> calculatedIndices = new ArrayList<>();
            if (!indices[0].equals("")) {
                for (String index : indices) {
                    calculatedIndices.add(Integer.parseInt(index));
                }
            }
            this.setValuesInServer(options, calculatedIndices);
        } else if (value.startsWith("dblclick")) {
            doubleClickListeners.update(value.replace("dblclick ", ""));
        }
    }

    /**
     * Provides the HTML representation of this Component
     *
     * @return the HTML for this Component
     */
    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        List<String> selectedItems = getSelectedItems();
        for (String option : options) {
            sb.append("<option class=\"ui-selectlistbox-item ui-corner-all\"").append(selectedItems.contains(option) ? " selected=\"true\"" : "").append(">").append(option.replace("\"", "&quot;").replace("\\", "\\\\")).append("</option>");
        }
        return "<div id='" + getId() + "' style='width: 100%; height: 100%'><select id='slct" + getId() + "' " + getProperties() + "style=\"" + getStyle() + "\" class='ui-selectonelistbox ui-inputfield ui-widget ui-widget-content ui-corner-all' multiple=\"true\" onchange=\""
                + "var ret = '';"
                + "for (var i = 0; i != this.options.length; i++) {"
                + "if (this.options[i].selected) {"
                + "ret += i + ' ';"
                + "}"
                + "}"
                + "changed" + layoutId + "({id: '" + getId() + "', value: 'selection ' + ret});"
                + "\">"
                + sb.toString()
                + "</select>"
                + "<script>"
                + "    $('#slct" + getId() + "').dblclick(function () {\n"
                + "        var str = '';\n"
                + "        $('#slct" + getId() + " option:selected').each(function () {\n"
                + "            str = $(this).text();\n" //there's only going to be one, you can't double-click multiple items
                + "        });\n"
                + "        changed" + layoutId + "({id: '" + getId() + "', value: 'dblclick ' + str});"
                + "    })"
                + "</script>"
                + "</div>";
    }

    /**
     * @return a unique ID for this component
     */
    @Override
    public String getId() {
        return "list" + hashCode();
    }

    /**
     * To be called when this component is no longer needed; clears out indices,
     * options, and any listeners
     */
    @Override
    public void dispose() {
        selectedIndices.clear();
        options.clear();
        optionsChangedListeners.clear();
        selectionsChangedListeners.clear();
        doubleClickListeners.clear();
    }

}
