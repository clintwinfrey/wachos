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
package gov.mil.navy.nswcdd.wachos.components.table;

import gov.mil.navy.nswcdd.wachos.components.Component;
import gov.mil.navy.nswcdd.wachos.components.ListEditor;
import gov.mil.navy.nswcdd.wachos.components.layout.Dialog;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.table.Header.ColumnType;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.ComboBox;
import gov.mil.navy.nswcdd.wachos.components.text.ImageButton;
import gov.mil.navy.nswcdd.wachos.components.text.TextField;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TableSettingsDialog is a dialog that allows the user to hide data in a table
 */
public class TableSettingsDialog extends Dialog {

    /**
     * the table to hide data on
     */
    final Table table;
    /**
     * used to determine what rows are visible/not visible
     */
    ListEditor editor;

    /**
     * Constructor
     *
     * @param title the dialog's title
     * @param table the table for which to hide data
     * @param session the user's session
     * @param creator the layout that created this dialog
     */
    public TableSettingsDialog(String title, Table table, WSession session, Layout creator) {
        super(title, session, creator);
        this.table = table;
        editor = new ListEditor("Visible", "Hidden", table.header.visibleColumnNames, table.header.getColumnNames());
        updateContent();
    }

    /**
     * Updates the user interface of this dialog
     */
    private void updateContent() {

        //column editing
        editor.setItems(table.header.visibleColumnNames, table.header.getColumnNames());

        //filtering
        VBox filtersLayout = new VBox();
        filtersLayout.setBorder("Filters", true);
        filtersLayout.add(new Button("Add", action -> {
            filtersLayout.add(new FilterUI(filtersLayout, table, createFilter(table)));
        }));
        for (Filter filter : table.getFilters()) {
            filtersLayout.add(new FilterUI(filtersLayout, table, filter));
        }

        //ok button pressed
        Button okButton = new Button("OK", action -> {
            List<Filter> filters = new ArrayList<>();
            List<Component> components = filtersLayout.getComponents();
            for (int i = 1; i < components.size(); i++) {
                filters.add(((FilterUI) components.get(i)).toFilter());
            }
            table.setFilters(filters);
            table.setVisibleColumns(editor.getValues());
            this.close();
        });
        okButton.alignCenterH();

        content.removeAll();
        content.add(new VBox(editor).setBorder("Columns", true).alignCenterH(), filtersLayout, okButton);
    }

    /**
     * Opens this dialog
     */
    @Override
    public void open() {
        updateContent();
        super.open();
    }

    /**
     * Used to create a filter for the given table
     *
     * @param table the table to filter on
     * @return the filter
     */
    private Filter createFilter(Table table) {
        String columnName1 = table.header.getColumnNames().get(0);
        if (table.header.getColumnType(0) == ColumnType.NUMBER) {
            return new NumberFilter(columnName1, NumberFilter.DESCRIPTORS.get(0), 0.);
        } else {
            return new StringFilter(columnName1, StringFilter.DESCRIPTORS.get(0), "");
        }
    }

    /**
     * FilterUI is a simple GUI widget that allows the user to create a table
     * data filter
     */
    private static class FilterUI extends HBox {

        /**
         * the filters layout, which this belongs to
         */
        final Layout parent;
        /**
         * a button used to remove this FilterUI
         */
        final ImageButton removeButton;
        /**
         * the table to filter on
         */
        final Table table;
        /**
         * a combo box for selecting the filter's column
         */
        ComboBox columnsCombo = new ComboBox("a", Arrays.asList("a, b")), descriptorsCombo, valueCombo;
        /**
         * the value that the user types in (i.e. a number or a String)
         */
        TextField valueText;
        /**
         * the type of column associated with this column
         */
        ColumnType columnType;
        /**
         * the options for filtering (i.e. contains, not contains, >=, =, etc.)
         */
        List<String> filterOptions;

        /**
         * Constructor
         *
         * @param parent the filters layout, which this belongs to
         * @param table the table to filter on
         * @param filter the filter to represent by this UI
         */
        public FilterUI(VBox parent, Table table, Filter filter) {
            this.parent = parent;
            removeButton = new ImageButton("icons/remove.png", action -> {
                parent.remove(this);
            });
            this.table = table;
            update(filter);
        }

        /**
         * Updates this UI to match the new filter
         *
         * @param filter the new filter to represent
         */
        private void update(Filter filter) {
            this.removeAll();
            filterOptions = table.getFilterOptions(filter.columnName);
            columnsCombo = new ComboBox(filter.columnName, table.header.getColumnNames(), action -> update(toFilter()));
            columnType = table.header.getColumnType(filter.columnName);
            List<String> descriptorOptions;
            if (columnType == ColumnType.BOOLEAN) {
                descriptorOptions = BooleanFilter.DESCRIPTORS;
            } else if (columnType == ColumnType.NUMBER) {
                descriptorOptions = NumberFilter.DESCRIPTORS;
            } else if (filterOptions == null) {
                descriptorOptions = StringFilter.DESCRIPTORS;
            } else {
                descriptorOptions = Arrays.asList("equal to", "not equal to");
            }
            descriptorsCombo = new ComboBox(descriptorOptions.contains(filter.descriptor) ? filter.descriptor : descriptorOptions.get(0), descriptorOptions, action -> update(toFilter()));
            super.add(removeButton, columnsCombo, descriptorsCombo);
            if (columnType == ColumnType.BOOLEAN) {
                return;
            }

            if (filterOptions == null) {
                valueText = new TextField(filter.value.toString());
                add(valueText);
            } else {
                valueCombo = new ComboBox(filterOptions.get(0), filterOptions);
                add(valueCombo);
            }
        }

        /**
         * Converts this GUI to a table filter
         *
         * @return the table filter represented by this GUI
         */
        protected Filter toFilter() {
            if (columnType == ColumnType.BOOLEAN) {
                return new BooleanFilter(columnsCombo.getText(), descriptorsCombo.getText());
            }
            String value = filterOptions == null ? valueText.getText() : valueCombo.getText();
            if (columnType == ColumnType.NUMBER) {
                return new NumberFilter(columnsCombo.getText(), descriptorsCombo.getText(), getNumber(value));
            } else {
                return new StringFilter(columnsCombo.getText(), descriptorsCombo.getText(), value);
            }
        }

        /**
         * Returns a number for the provided String value
         *
         * @param value the String to convert to a number
         * @return a double that is parsed from the String
         */
        private double getNumber(String value) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return -999999;
            }
        }

    }

}
