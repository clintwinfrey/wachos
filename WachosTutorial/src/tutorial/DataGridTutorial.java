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

import gov.mil.navy.nswcdd.wachos.components.DataGrid;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class DataGridTutorial {

    public static Tutorial create(WSession session) {

        //first, create a 2D set of data
        double[][] data = new double[10][5];
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                data[row][col] = row * col;
            }
        }

        //next, create the component
        DataGrid dataGrid = new DataGrid(data);

        //listen for if a cell has been selected
        dataGrid.cellSelectionListeners.add(cellData -> {
            String[] strs = cellData.split("\\s+");
            session.postInfo("Cell Selected", "row" + strs[0] + ", col" + strs[1] + " selected");
        });

        //listen for if a cell value has changed; if the value isn't a number, post an error to the user
        dataGrid.cellChangedListeners.add(cellData -> {
            String[] strs = cellData.split("\\s+");
            try {
                session.postSuccess("Cell Changed", "row" + strs[0] + ", col" + strs[1] + " changed to " + Double.parseDouble(strs[2]));
                dataGrid.setCellStyle(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), ""); //remove styling if previously an error
            } catch (NumberFormatException e) {
                dataGrid.setCellStyle(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), "background: red; color: white");
                session.postError("Not a Number", "row" + strs[0] + ", col" + strs[1] + " value=" + strs[2]); //style as error
            }
        });

        String code = "        //first, create a 2D set of data\n"
                + "        double[][] data = new double[10][5];\n"
                + "        for (int row = 0; row < data.length; row++) {\n"
                + "            for (int col = 0; col < data[0].length; col++) {\n"
                + "                data[row][col] = row * col;\n"
                + "            }\n"
                + "        }\n"
                + "\n"
                + "        //next, create the component\n"
                + "        DataGrid dataGrid = new DataGrid(data);\n"
                + "\n"
                + "        //listen for if a cell has been selected\n"
                + "        dataGrid.cellSelectionListeners.add(cellData -> {\n"
                + "            String[] strs = cellData.split(\"\\\\s+\");\n"
                + "            session.postInfo(\"Cell Selected\", \"row\" + strs[0] + \", col\" + strs[1] + \" selected\");\n"
                + "        });\n"
                + "\n"
                + "        //listen for if a cell value has changed; if the value isn't a number, post an error to the user\n"
                + "        dataGrid.cellChangedListeners.add(cellData -> {\n"
                + "            String[] strs = cellData.split(\"\\\\s+\");\n"
                + "            try {\n"
                + "                session.postSuccess(\"Cell Changed\", \"row\" + strs[0] + \", col\" + strs[1] + \" changed to \" + Double.parseDouble(strs[2]));\n"
                + "                dataGrid.setCellStyle(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), \"\"); //remove styling if previously an error\n"
                + "            } catch (NumberFormatException e) {\n"
                + "                dataGrid.setCellStyle(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), \"background: red; color: white\");\n"
                + "                session.postError(\"Not a Number\", \"row\" + strs[0] + \", col\" + strs[1] + \" value=\" + strs[2]); //style as error\n"
                + "            }\n"
                + "        });";

        return new Tutorial("DataGrid", "gov/mil/navy/nswcdd/wachos/components/DataGrid.html", dataGrid, code);
    }
}
