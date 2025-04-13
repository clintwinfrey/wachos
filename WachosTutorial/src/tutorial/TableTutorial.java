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
package tutorial;

import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.table.Header;
import gov.mil.navy.nswcdd.wachos.components.table.Table;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.LinkButton;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.Arrays;

public class TableTutorial {

    public static Tutorial create(WSession session) {

        //create a table with four columns; the first is a LinkButton, demonstrating that this can be a usable Component
        Table table = new Table(new LinkButton("Number", value -> session.postSuccess("Success", "You clicked on a header")),
                new Label("Letter"), "Day of Week", "Boolean");
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
        String[] days = {"M", "T", "W", "Th", "F", "S", "Su"};
        for (int i = 0; i < 15; i++) {
            Label num = new Label(i + ""); //let's demonstrate that you can actually put a Component in a cell
            num.setToolTip("This is " + i); //and let's give it a tooltip
            table.addRow(Arrays.asList(num, letters[i], days[i % 7], (i % 2 == 0) + "")); //add a row to the table
        }
        table.setComparatorAlphaNumeric("Number"); //make the number column sortable
        table.setComparatorAlphaNumeric("Letter"); //make the letter column sortable
        table.setComparator("Day of Week", "Su", "M", "T", "W", "Th", "F", "S"); //make the day of week column sortable with a custom comparator
        table.setColumnType("Number", Header.ColumnType.NUMBER); //mark this column as numbers, so we can filter
        table.setColumnType("Boolean", Header.ColumnType.BOOLEAN); //mark this column as booleans, so we can filter
        table.setFilterOptions("Day of Week", days); //allow this column to be filtered according to the 'days' options
        Button filterButton = new Button("Filter", action -> table.settingsDialog.open()).alignRight(); //make a button for filtering this table

        String code = "        //create a table with four columns; the first is a LinkButton, demonstrating that this can be a usable Component\n"
                + "        Table table = new Table(new LinkButton(\"Number\", value -> session.postSuccess(\"Success\", \"You clicked on a header\")),\n"
                + "                new Label(\"Letter\"), \"Day of Week\", \"Boolean\");\n"
                + "        String[] letters = {\"A\", \"B\", \"C\", \"D\", \"E\", \"F\", \"G\", \"H\", \"I\", \"J\", \"K\", \"L\", \"M\", \"N\", \"O\"};\n"
                + "        String[] days = {\"M\", \"T\", \"W\", \"Th\", \"F\", \"S\", \"Su\"};\n"
                + "        for (int i = 0; i < 15; i++) {\n"
                + "            Label num = new Label(i + \"\"); //let's demonstrate that you can actually put a Component in a cell\n"
                + "            num.setToolTip(\"This is \" + i); //and let's give it a tooltip\n"
                + "            table.addRow(Arrays.asList(num, letters[i], days[i % 7], (i % 2 == 0) + \"\")); //add a row to the table\n"
                + "        }\n"
                + "        table.setComparatorAlphaNumeric(\"Number\"); //make the number column sortable\n"
                + "        table.setComparatorAlphaNumeric(\"Letter\"); //make the letter column sortable\n"
                + "        table.setComparator(\"Day of Week\", \"Su\", \"M\", \"T\", \"W\", \"Th\", \"F\", \"S\"); //make the day of week column sortable with a custom comparator\n"
                + "        table.setColumnType(\"Number\", Header.ColumnType.NUMBER); //mark this column as numbers, so we can filter\n"
                + "        table.setColumnType(\"Boolean\", Header.ColumnType.BOOLEAN); //mark this column as booleans, so we can filter\n"
                + "        table.setFilterOptions(\"Day of Week\", days); //allow this column to be filtered according to the 'days' options\n"
                + "        Button filterButton = new Button(\"Filter\", action -> table.settingsDialog.open()).alignRight(); //make a button for filtering this table";
        return new Tutorial("Table", "gov/mil/navy/nswcdd/wachos/components/table/Table.html", new VBox(table, filterButton), code);
    }

}
