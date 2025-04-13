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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Row is a row in a table
 */
public class Row {

    /**
     * the items in the row
     */
    protected List<String> cellNames = new ArrayList<>();
    /**
     * the actual components for each cell name
     */
    protected List<Component> cells = new ArrayList<>();

    /**
     * Constructor
     *
     * @param header the header object that defines this row
     * @param cells there must be a cell for each column defined in the header
     */
    public Row(Header header, Object... cells) {
        this(header, Arrays.asList(cells));
    }

    /**
     * Constructor
     *
     * @param header the header object that defines this row
     * @param cells there must be a cell for each column defined in the header
     */
    public Row(Header header, List cells) {
        for (int i = 0; i < header.getColumnNames().size(); i++) {
            if (cells.get(i) instanceof Component) {
                this.cellNames.add(header.getColumnNames().get(i));
                this.cells.add((Component) cells.get(i));
            } else {
                this.cellNames.add(header.getColumnNames().get(i));
                this.cells.add(new Label(cells.get(i).toString()));
            }
        }
    }

    /**
     * Gets the component for the given column name
     *
     * @param columnName the name of the column
     * @return the value of the cell at the corresponding column
     */
    public Component get(String columnName) {
        return cells.get(cellNames.indexOf(columnName));
    }

    /**
     * Gets the component for the given column index
     *
     * @param columnIndex the index of the column
     * @return the value of the cell at the corresponding column
     */
    public Component get(int columnIndex) {
        return cells.get(columnIndex);
    }

    /**
     * @return the number of columns in this row
     */
    public int size() {
        return cells.size();
    }

}
