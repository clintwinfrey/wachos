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
import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import java.util.Comparator;

/**
 * RowComparator compares one row with another row for sorting purposes
 */
public class RowComparator implements Comparator<Row> {

    /**
     * the column to sort on
     */
    final String columnName;
    /**
     * the comparator by which to sort the column
     */
    final Comparator<Component> cellComparator;
    /**
     * flag indicating if the results should be in reverse sorting order
     */
    final boolean reverse;

    /**
     * Constructor
     *
     * @param columnName the column to sort on
     * @param cellComparator the comparator by which to sort the column
     * @param reverse flag indicating if the results should be in reserve
     * sorting order
     */
    public RowComparator(String columnName, Comparator<Component> cellComparator, boolean reverse) {
        this.columnName = columnName;
        this.cellComparator = cellComparator;
        this.reverse = reverse;
    }

    /**
     * Compares r1 with r2
     *
     * @param r1 the first row to compare
     * @param r2 the second row to compare
     * @return integer indicating which row is first
     */
    @Override
    public int compare(Row r1, Row r2) {
        Component c1 = r1.get(columnName);
        Component c2 = r2.get(columnName);
        if (!(c1 instanceof TextComponent) || !(c2 instanceof TextComponent)) {
            return 0; //nothing to compare;
        }
        if (!reverse) {
            return cellComparator.compare((TextComponent) c1, (TextComponent) c2);
        } else {
            return cellComparator.compare((TextComponent) c2, (TextComponent) c1);
        }
    }

}
