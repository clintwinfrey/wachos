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

import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.ImageButton;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import java.util.ArrayList;
import java.util.List;

/**
 * ListEditor contains two lists; the left side contains items that are
 * currently in the list, and the right side contains options that can be added
 * to the list
 */
public class ListEditor extends HBox {

    /**
     * a list of items that are currently selected
     */
    ListBox selectedListBox;
    /**
     * a list of items that are not currently selected
     */
    ListBox unselectedListBox;
    /**
     * all of the available items
     */
    List<String> availableItems;
    /**
     * the title for the selecteds listbox
     */
    final String selectedsId;
    /**
     * the title for the unselecteds listbox
     */
    final String unselectedsId;

    /**
     * Constructor
     *
     * @param selectedsId the title for the selecteds listbox
     * @param unselectedsId the title for the unselecteds listbox
     * @param initialSelectedItems the items that are selected on creation
     * @param initialAvailableItems all items that can be selected
     */
    public ListEditor(String selectedsId, String unselectedsId, List<String> initialSelectedItems, List<String> initialAvailableItems) {
        this.selectedsId = selectedsId;
        this.unselectedsId = unselectedsId;
        ListEditor.this.setItems(initialSelectedItems, initialAvailableItems);
    }

    /**
     * @return a list of all selected items, in order as shown in the ListBox
     */
    public List<String> getValues() {
        return selectedListBox.getOptions();
    }

    /**
     * Updates the selected items and the items that can be chosen from
     *
     * @param initialSelectedItems the selected items, a subset of the available
     * items
     * @param initialAvailableItems all items that can be selected
     */
    public void setItems(List<String> initialSelectedItems, List<String> initialAvailableItems) {
        this.removeAll();

        List<String> selectedItems = new ArrayList(initialSelectedItems);
        List<String> unselectedItems = new ArrayList<>();
        availableItems = new ArrayList<>(initialAvailableItems);
        for (String name : availableItems) {
            if (!selectedItems.contains(name)) {
                unselectedItems.add(name);
            }
        }
        unselectedListBox = new ListBox(unselectedItems);
        unselectedListBox.setHeight("200px");
        selectedListBox = new ListBox(selectedItems);
        selectedListBox.setHeight("200px");

        //create unselected listing layout
        Layout unselectedLayout = new VBox(new Label(unselectedsId), unselectedListBox, new ImageButton("fa-arrow-left", action -> {
            //figure out what items need to be selected
            List<String> itemsToSelect = new ArrayList(unselectedListBox.getSelectedItems());

            //remove items from unselectedListBox
            List<String> remainingHiddenNames = new ArrayList(unselectedListBox.getOptions());
            remainingHiddenNames.removeAll(itemsToSelect);
            unselectedListBox.setOptions(remainingHiddenNames);

            //add items to selectedListBox
            itemsToSelect.addAll(0, selectedListBox.getOptions());
            selectedListBox.setOptions(itemsToSelect);
        }).alignCenterH());

        //create selected listing layout
        Layout selectedLayout = new VBox(new Label(selectedsId), selectedListBox,
                new HBox(
                        new ImageButton("fa-arrow-up", action -> {
                            List<Integer> indices = selectedListBox.getSelectedIndices();
                            if (indices.isEmpty() || indices.get(0) == 0) {
                                return;
                            }
                            List<String> selections = new ArrayList(selectedListBox.getSelectedItems());
                            List<String> options = new ArrayList(selectedListBox.getOptions());
                            for (int i = 0; i < selections.size(); i++) {
                                options.remove(selections.get(i));
                                options.add(indices.get(i) - 1, selections.get(i));
                                indices.set(i, indices.get(i) - 1);
                            }
                            selectedListBox.setOptionsAndSelectedItems(options, selections);
                        }),
                        new ImageButton("fa-arrow-down", action -> {
                            List<String> options = new ArrayList<>(selectedListBox.getOptions());
                            List<Integer> indices = selectedListBox.getSelectedIndices();
                            if (indices.isEmpty() || indices.get(indices.size() - 1) == options.size() - 1) {
                                return;
                            }
                            List<String> selections = new ArrayList<>(selectedListBox.getSelectedItems());
                            for (int i = selections.size() - 1; i >= 0; i--) {
                                options.remove(selections.get(i));
                                options.add(indices.get(i) + 1, selections.get(i));
                                indices.set(i, indices.get(i) + 1);
                            }
                            selectedListBox.setOptionsAndSelectedItems(options, selections);
                        }),
                        new ImageButton("fa-arrow-right", action -> {
                            //figure out what new names need to be unselected
                            List<String> namesToHide = new ArrayList<>(selectedListBox.getSelectedItems());

                            //remove items from selectedListBox listing
                            List<String> remainingVisibleNames = new ArrayList<>(selectedListBox.getOptions());
                            remainingVisibleNames.removeAll(namesToHide);
                            selectedListBox.setOptionsAndSelectedIndices(remainingVisibleNames, new ArrayList<>());

                            //add items to unselectedListBox listing
                            namesToHide.addAll(unselectedListBox.getOptions());
                            List<String> sortedUnselectedItems = new ArrayList<>();
                            for (String colName : availableItems) {
                                if (namesToHide.contains(colName)) {
                                    sortedUnselectedItems.add(colName);
                                }
                            }
                            unselectedListBox.setOptions(sortedUnselectedItems);
                        })
                ).alignCenterH());
        selectedLayout.alignCenterH();
        unselectedLayout.alignCenterH();
        add(selectedLayout, unselectedLayout);
    }

}
