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
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Header is the Table's header information. It defines things like column type,
 * visible columns, and column comparators
 */
public class Header {

    /**
     * defines column types so that a column can be filtered based on type
     */
    public enum ColumnType {
        /**
         * this is a column of numbers
         */
        NUMBER,
        /**
         * this is a column of strings
         */
        STRING,
        /**
         * this is a column of booleans
         */
        BOOLEAN
    }

    /**
     * the Components of each column head
     */
    private final List<Component> columns = new ArrayList<>();
    /**
     * the names for each column
     */
    protected final List<String> columnNames = new ArrayList<>();
    /**
     * the types for each column
     */
    private final List<ColumnType> columnTypes = new ArrayList<>();
    /**
     * the columns that are currently visible
     */
    List<String> visibleColumnNames = new ArrayList<>();
    /**
     * a column named "Day of the Week" may have a a comparator that sorts by
     * day order, as opposed to alphabetically
     */
    Map<String, Comparator<Component>> comparators = new HashMap<>();

    /**
     * Compares fields by alphabet (A-Z) and/or number (smallest to largest)
     */
    protected static Comparator<Component> ALPHANUMERIC_COMPARATOR = (Component c1, Component c2) -> {
        double val1 = getValue(c1), val2 = getValue(c2);
        int ret = Double.compare(val1, val2);
        if (ret == 0) {
            return c1.toString().compareTo(c2.toString());
        }
        return ret;
    };

    /**
     * Constructor
     *
     * @param columns the array of columns for this Header; can be anything, but
     * Components will be treated special; anything else will have its toString
     * method called
     */
    public Header(Object... columns) {
        this(Arrays.asList(columns));
    }

    /**
     * Constructor
     *
     * @param columns the List of columns for this Header; can be anything, but
     * Components will be treated special; anything else will have its toString
     * method called
     */
    public Header(List columns) {
        for (Object column : columns) {
            if (column instanceof Component) {
                this.columns.add((Component) column);
            } else {
                this.columns.add(new Label(column.toString()));
            }
            columnNames.add(column.toString());
            visibleColumnNames.add(column.toString());
            columnTypes.add(ColumnType.STRING);
        }
    }

    /**
     * @return all of the Component items for each column
     */
    public List<Component> getColumns() {
        return new ArrayList<>(columns);
    }

    /**
     * @return all of the names for each column
     */
    public List<String> getColumnNames() {
        return new ArrayList<>(columnNames);
    }

    /**
     * Sets the column names that are visible
     *
     * @param visibleColumnNames the column names that should be shown
     */
    public void setVisibleColumnNames(List<String> visibleColumnNames) {
        this.visibleColumnNames = visibleColumnNames;
    }

    /**
     * @return the names of columns that are visible
     */
    public List<String> getVisibleColumnNames() {
        return new ArrayList<>(visibleColumnNames);
    }

    /**
     * Gets the Component associated with the given columnName
     *
     * @param columnName the string name of the column
     * @return the corresponding Component
     */
    public Component get(String columnName) {
        return columns.get(columnNames.indexOf(columnName));
    }

    /**
     * Sets the comparator for items in a given column
     *
     * @param columnName the column to set the comparator for
     * @param comparator the way to compare items in the column
     */
    protected void setComparator(String columnName, Comparator<Component> comparator) {
        this.comparators.put(columnName, comparator);
    }

    /**
     * Sets the way items are compared in a column; for example, the values
     * "JAN", "FEB", "MAR", "APR", ... would be appropriate when defining how to
     * sort a column of months
     *
     * @param columnName the column for which to compare items
     * @param comparisonOrder the order of item values that should be first,
     * second, third, etc.
     */
    protected void setComparator(String columnName, String... comparisonOrder) {
        List<String> order = Arrays.asList(comparisonOrder);
        setComparator(columnName, (Component c1, Component c2) -> Integer.compare(order.indexOf(c1.toString()), order.indexOf(c2.toString())));
    }

    /**
     * Sets the column name to compare items alpha-numerically
     *
     * @param columnName the column to compare alpha-numerically
     */
    protected void setComparatorAlphaNumeric(String columnName) {
        setComparator(columnName, ALPHANUMERIC_COMPARATOR);
    }

    /**
     * Gets the comparator for the given column name
     *
     * @param columnName the column for which to retrieve the comparator
     * @return the comparator for the column name
     */
    protected Comparator<Component> getComparator(String columnName) {
        return comparators.get(columnName);
    }

    /**
     * Determines whether the given column can be sorted
     *
     * @param columnName we want to know if this column is sortable
     * @return flag indicating if a comparator exists for this column
     */
    public boolean isSortable(String columnName) {
        return comparators.get(columnName) != null;
    }

    /**
     * Sets the column type to a string, number, or boolean
     *
     * @param columnName the column to set the column type of
     * @param type the column type that this column should be
     */
    protected void setColumnType(String columnName, ColumnType type) {
        columnTypes.set(columnNames.indexOf(columnName), type);
    }

    /**
     * Gets the column type for the column at the given index
     *
     * @param columnIndex the index of the column
     * @return the column's type
     */
    public ColumnType getColumnType(int columnIndex) {
        return columnTypes.get(columnIndex);
    }

    /**
     * Gets the column type for the column with the given name
     *
     * @param columnName the name of the column
     * @return the column's type
     */
    public ColumnType getColumnType(String columnName) {
        return columnTypes.get(columnNames.indexOf(columnName));
    }

    /**
     * Gets the value of the component as a double
     *
     * @param c the component to convert to a double
     * @return the component, as a double
     */
    private static double getValue(Component c) {
        if (!(c instanceof TextComponent)) {
            return Double.MAX_VALUE;
        }
        try {
            return Double.parseDouble(((TextComponent) c).getText());
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }

}
