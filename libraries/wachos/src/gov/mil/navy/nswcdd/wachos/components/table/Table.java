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
import gov.mil.navy.nswcdd.wachos.components.ComponentListener;
import gov.mil.navy.nswcdd.wachos.components.layout.Grid;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.text.ImageButton;
import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Table is just a grid, but it gets drawn a little differently and can be
 * filtered on/sorted
 */
public class Table extends Grid implements Serializable {

    /**
     * the possible filters for each column
     */
    Map<String, List<String>> filterOptions = new HashMap<>();
    /**
     * The info for the first row of the table (the header)
     */
    public final Header header;
    /**
     * current filters to apply to this Table
     */
    private List<Filter> filters = new ArrayList<>();
    /**
     * all of the rows of this table
     */
    private final List<Row> rows = new ArrayList<>();
    /**
     * all of the rows of this table after filters are applied
     */
    private final List<Row> filteredRows = new ArrayList<>();
    /**
     * the column name to sort this table by
     */
    private String sortBy = "";
    /**
     * if true, then everything should be sorted backwards; happens after the
     * user sorts twice in a row
     */
    private boolean reverseSort = false;
    /**
     * this is a nice, convenient way to define table settings (i.e. filtering,
     * column hiding)
     */
    public TableSettingsDialog settingsDialog;
    /**
     * the currently selected row
     */
    private int selectedRow = -1;
    /**
     * buttons that are used for sorting the table
     */
    List<Component> sortButtons = new ArrayList<>();
    /**
     * if you're making a lot of changes, then set the to false while making
     * those changes; don't forget to change it back to true and redraw at the
     * end, though
     */
    public boolean allowRedrawing = true;

    /**
     * Constructor
     *
     * @param columns the columns (TextComponents or Strings that will be turned
     * into Labels) of this Table
     */
    public Table(Object... columns) {
        super(columns.length);
        isTable = true;
        this.header = new Header(columns);
        super.alignCenterH();
        settingsDialog = new TableSettingsDialog("Table Settings", this, session, this);
    }

    /**
     * Adds a row to this table at the last index (cell count must match column
     * count)
     *
     * @param cells the cell of each item in the row (can be a String or
     * TextComponent)
     */
    public void addRow(List cells) {
        rows.add(new Row(header, cells));
        redraw();
    }

    /**
     * Adds a row to this table at the provided index (cell count must match
     * column count)
     *
     * @param index add the row at this location (0 means first row)
     * @param cells the cell of each item in the row (can be a String or
     * TextComponent)
     */
    public void addRow(int index, List cells) {
        rows.add(index, new Row(header, cells));
        for (Object cell : cells) {
            if (cell instanceof Component) {
                ((Component) cell).init(layoutId, session);
            }
        }
        redraw();
    }

    /**
     * Removes the row at the given index
     *
     * @param index the index of the row to remove
     */
    public void removeRow(int index) {
        if (index < rows.size()) {
            rows.remove(index); //TODO don't we need to also remove listeners on each cell?
            redraw();
        }
    }

    /**
     * Removes the row of cells
     *
     * @param cells the row of cells to remove
     */
    public void removeRow(List cells) {
        List cellsCopy = new ArrayList(cells);
        for (Row row : rows) {
            for (Object cell : row.cells) {
                if (cellsCopy.contains(cell) || cellsCopy.contains(cell.toString())) {
                    cellsCopy.remove(cell);
                } else {
                    break;
                }
            }
            if (cellsCopy.isEmpty()) {
                rows.remove(row); //TODO don't we need to also remove listeners on each cell?
                break;
            }
        }
        redraw();
    }

    /**
     * Overrides redraw, because the selected row needs to be reset
     */
    @Override
    public void redraw() {
        if (allowRedrawing) {
            selectedRow = -1;
            super.redraw();
        }
    }

    /**
     * @return the table's header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @return the table's rows
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Filters the table based on the filters that are set
     */
    private void filterRows() {
        filteredRows.clear();
        for (Row row : rows) {
            boolean valid = true;
            for (Filter filter : filters) {
                Component c = row.get(filter.columnName);
                valid = valid && c instanceof TextComponent && filter.isValid((TextComponent) c);
            }
            if (valid) {
                filteredRows.add(row);
            }
        }
        if (!sortBy.equals("")) {
            Collections.sort(filteredRows, new RowComparator(sortBy, header.getComparator(sortBy), reverseSort));
        }

        components.clear();
        sortButtons.clear();
        for (String columnName : header.getVisibleColumnNames()) {
            if (header.isSortable(columnName)) {
                ImageButton sortButton = new ImageButton(getSortIcon(columnName), action -> sort(columnName));
                sortButtons.add(sortButton);
                components.add(new HBox(header.get(columnName), sortButton).setProperty("css.background-color", header.get(columnName).getProperty("css.background-color")).alignCenterH());
            } else {
                components.add(header.get(columnName));
            }
        }
        columns = components.size();
        for (Row row : filteredRows) {
            for (String columnName : header.getVisibleColumnNames()) {
                components.add(row.get(columnName));
            }
        }
    }

    /**
     * Gets all of the components that are children of this Table
     *
     * @return the child components
     */
    @Override
    public List<Component> getComponents() {
        List<Component> ret = new ArrayList<>();
        ret.addAll(header.getColumns());
        for (Row row : rows) {
            ret.addAll(row.cells);
        }
        ret.addAll(sortButtons);
        return ret;
    }

    /**
     * Provides the icon used for sorting (up, down, or diamond)
     *
     * @param columnName the column that is to be sorted
     * @return the sorting icon
     */
    private String getSortIcon(String columnName) {
        if (isSortedAscending(columnName)) {
            return "fa-sort-up";
        } else if (isSortedDescending(columnName)) {
            return "fa-sort-down";
        } else {
            return "fa-sort";
        }
    }

    /**
     * Sets which columns should be displayed
     *
     * @param visibleColumns the columns that should be displayed
     */
    public void setVisibleColumns(List<String> visibleColumns) {
        header.setVisibleColumnNames(visibleColumns);
        redraw();
    }

    /**
     * Sets the filters for this table
     *
     * @param filters the filters that will filter out table data
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
        redraw();
    }

    /**
     * @return the filters currently applied to this Table
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Gets the filter options for a given column
     *
     * @param columnName the name of the column we need filters for
     * @return a list of filter option names
     */
    public List<String> getFilterOptions(String columnName) {
        return filterOptions.get(columnName);
    }

    /**
     * Sets the filter options for a column
     *
     * @param columnName the column to set filters for
     * @param filterOptions the allowed filtering options
     */
    public void setFilterOptions(String columnName, String... filterOptions) {
        this.filterOptions.put(columnName, Arrays.asList(filterOptions));
    }

    /**
     * Sets the filter options for a column
     *
     * @param columnName the column to set filters for
     * @param filterOptions the allowed filtering options
     */
    public void setFilterOptions(String columnName, List<String> filterOptions) {
        this.filterOptions.put(columnName, filterOptions);
    }

    /**
     * The sort button has been pressed, we need to sort the table for the
     * column being sorted on
     *
     * @param columnName the column the table is being sorted on
     */
    public void sort(String columnName) {
        reverseSort = columnName.equals(sortBy) && !reverseSort;
        sortBy = columnName;
        redraw();
    }

    /**
     * Tells us if the given column is sorted ascending
     *
     * @param columnName the column that may be sorted ascending
     * @return flag indicating if the given column is sorted ascending
     */
    public boolean isSortedAscending(String columnName) {
        return sortBy.equals(columnName) && !reverseSort;
    }

    /**
     * Tells us if the given column is sorted descending
     *
     * @param columnName the column that may be sorted descending
     * @return flag indicating if the given column is sorted descending
     */
    public boolean isSortedDescending(String columnName) {
        return sortBy.equals(columnName) && reverseSort;
    }

    /**
     * Sets the comparator for comparing cells in a column
     *
     * @param columnName the name of the column to be sorted
     * @param comparator the way its cells should be sorted
     */
    public void setComparator(String columnName, Comparator<Component> comparator) {
        header.setComparator(columnName, comparator);
        redraw();
    }

    /**
     * Sets the comparison order for comparing cells in a column
     *
     * @param columnName the name of the column to be sorted
     * @param comparisonOrder if you pass in "MON", "TUE", "WED", etc., then the
     * cells will be sorted with MON first, then TUE second, etc.
     */
    public void setComparator(String columnName, String... comparisonOrder) {
        header.setComparator(columnName, comparisonOrder);
        redraw();
    }

    /**
     * Tells the given column to be sorted by alphanumeric comparison
     *
     * @param columnName the column to set sorting for
     */
    public void setComparatorAlphaNumeric(String columnName) {
        header.setComparatorAlphaNumeric(columnName);
        redraw();
    }

    /**
     * Sets this column's type, which is useful for filtering purposes
     *
     * @param columnName the column to set the type of
     * @param columnType the column's type (i.e. boolean, string, number?)
     */
    public void setColumnType(String columnName, Header.ColumnType columnType) {
        header.setColumnType(columnName, columnType);
    }

    /**
     * @return the HTML representation of this Table
     */
    @Override
    public String toHtml() {
        filterRows();//ensures that filtered rows are updated

        //create a filter message, if anything is filtered out
        filterMessage = "";
        if (filteredRows.size() != rows.size()) {
            filterMessage = "Showing " + filteredRows.size() + " out of " + rows.size() + (rows.size() == 1 ? " row" : " rows");
        }
        int missingColumns = header.getColumnNames().size() - columns;
        if (filterMessage.length() != 0 && missingColumns != 0) {
            filterMessage += "; ";
        }
        if (missingColumns != 0) {
            filterMessage += (missingColumns == 1 ? "a column is hidden" : missingColumns + " columns are hidden");
        }

        return super.toHtml();
    }

    /**
     * Sets the listener for when a row has been selected
     *
     * @param listener the thing listening for when a row is selected
     * @return this
     */
    public Table setRowSelectionListener(ComponentListener listener) {
        rowSelectListener = listener;
        return this;
    }

    /**
     * @return flag indicating if you can select rows in this table
     */
    public boolean isRowSelectable() {
        return rowSelectListener != null;
    }

    /**
     * Sets the selected row based on user click, updates the row selection
     * listener
     *
     * @param rowIndex the index of the row that was clicked
     */
    public void setSelectedRow(int rowIndex) {
        this.selectedRow = rowIndex;
        rowSelectListener.update(rowIndex + "");
    }

    /**
     * @return the currently selected row
     */
    public Row getSelectedRow() {
        return selectedRow == -1 ? null : filteredRows.get(selectedRow);
    }

    /**
     * Use this if you want specific foreground and background colors for a row
     * that is selected
     *
     * @param foreground the color of the font when selected
     * @param background the color of the cell when selected
     */
    public static void setRowSelectionColors(Color foreground, Color background) {
        ROW_SELECTION_STYLE = "<style>\n"
                + ".selected {\n"
                + "    color: #" + WTools.toHex(foreground) + ";\n"
                + "    background-color: #" + WTools.toHex(background) + ";\n"
                + "}\n"
                + "</style>\n";
    }

}
