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

import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StringFilter is a table filter where you can filter Strings based on whether
 * they contain, don't contain, equal, or don't equal a user-provided String
 */
public class StringFilter extends Filter<String> {

    /**
     * the different ways we can filter
     */
    public static List<String> DESCRIPTORS = new ArrayList<>(Arrays.asList("contains", "not contains", "equal to", "not equal to"));

    /**
     * Constructor
     *
     * @param columnName the name of the column to filter on
     * @param descriptor the descriptor to filter on
     * @param value the value to filter against
     */
    public StringFilter(String columnName, String descriptor, String value) {
        super(columnName, descriptor, value);
    }

    /**
     * Returns true if this cell is not filtered out
     *
     * @param cell the column cell we will check on to see if it meets the
     * filter criteria
     * @return flag indicating if this cell is allowable after filtering
     */
    @Override
    public boolean isValid(TextComponent cell) {
        String cellValue = cell.getText();
        String[] filteredValues = value.trim().split(",");
        for (String filteredValue : filteredValues) {
            filteredValue = filteredValue.trim();
            if ((descriptor.equals("contains") && cellValue.contains(filteredValue)) || (descriptor.equals("equal to") && cellValue.equals(filteredValue))) {
                return true;
            }
            if ((descriptor.equals("not contains") && cellValue.contains(filteredValue)) || (descriptor.equals("not equal to") && cellValue.equals(filteredValue))) {
                return false;
            }
        }
        return !descriptor.equals("contains") && !descriptor.equals("equal to");
    }

}
