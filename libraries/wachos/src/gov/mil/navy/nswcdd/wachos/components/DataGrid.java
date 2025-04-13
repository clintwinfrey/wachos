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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a grid of editable data
 */
public class DataGrid extends Component {

    /**
     * the data that makes up this grid
     */
    public String[][] data;
    /**
     * captures the formatting of a given cell; key is [row],[col]; value is the
     * content of the 'style=' tag
     */
    private final Map<String, String> formatMap = new HashMap<>();
    /**
     * captures editability for a given cell; key is [row],[col]; value is flag
     * indicating if editing can happen; unless flag is false, editing is
     * possible!
     */
    private final Map<String, String> editMap = new HashMap<>();
    /**
     * default width and height of widget
     */
    private int width = 520, height = 300;
    /**
     * width of each cell in pixels
     */
    private int cellWidth = 60;
    /**
     * listens for when the available options change
     */
    public final ComponentListeners cellChangedListeners = new ComponentListeners();
    /**
     * listens for when the available options change
     */
    public final ComponentListeners cellSelectionListeners = new ComponentListeners();

    /**
     * Constructor
     *
     * @param data the data that makes up this grid
     */
    public DataGrid(double[][] data) {
        this.data = toStrings(data);
    }

    /**
     * Constructor
     *
     * @param data the data that makes up this grid
     */
    public DataGrid(String[][] data) {
        this.data = data;
    }

    /**
     * Converts the 2D array of doubles to a 2D array of Strings
     *
     * @param data convert this
     * @return the String representations of the doubles
     */
    private String[][] toStrings(double[][] data) {
        String[][] dataStrs = new String[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                dataStrs[i][j] = data[i][j] + "";
            }
        }
        return dataStrs;
    }

    /**
     * Sets the data of this grid to be this new 2D array
     *
     * @param data the new 2D array that makes up this grid
     * @return this
     */
    public DataGrid setData(double[][] data) {
        return setData(toStrings(data));
    }

    /**
     * Sets the data of this grid to be this new 2D array
     *
     * @param data the new 2D array that makes up this grid
     * @return this
     */
    public DataGrid setData(String[][] data) {
        this.data = data;
        if (isRendered()) {
            redraw();
        }
        return this;
    }

    /**
     * Sets the style of the specified row
     *
     * @param row the index of the row to style
     * @param style the css styling for the row
     * @return this
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public DataGrid setRowStyle(int row, String style) {
        StringBuilder rowStyles = new StringBuilder();
        formatMap.put(row + ",∞", style);
        if (isRendered()) {
            rowStyles.append("FormatMap.set(\"" + row + ",∞\", \"" + style + "\");\n"
                    + getId() + "grid.invalidateRow(" + row + ");\n"
                    + getId() + "grid.render();\n");
        }
        if (isRendered()) {
            exec(rowStyles.toString());
        }
        return this;
    }

    /**
     * Sets the style of the specified row
     *
     * @param col the index of the column to style
     * @param style the css styling for the column
     * @return this
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public DataGrid setColumnStyle(int col, String style) {
        StringBuilder colStyles = new StringBuilder();
        formatMap.put("∞," + col, style);

        if (isRendered()) {
            colStyles.append("FormatMap.set(\"∞," + col + "\", \"" + style + "\");\n"
                    + getId() + "grid.invalidate();\n"
                    + getId() + "grid.render();\n");
        }
        if (isRendered()) {
            exec(colStyles.toString());
        }
        return this;
    }

    /**
     * Sets the CSS style for the given row/col cell
     *
     * @param row the row index
     * @param col the column index
     * @param style the CSS style
     * @return this
     */
    public DataGrid setCellStyle(int row, int col, String style) {
        formatMap.put(row + "," + col, style);
        if (isRendered()) {
            exec(getId() + "FormatMap.set(\"" + row + "," + col + "\", \"" + style + "\");\n"
                    + getId() + "grid.invalidateRow(" + row + ");\n"
                    + getId() + "grid.render();");
        }
        return this;
    }

    /**
     * Sets whether the given row can be edited
     *
     * @param row the index of the row
     * @param canEdit flag indicating if it can be edited
     * @return this
     */
    public DataGrid setRowEditable(int row, boolean canEdit) {
        editMap.put(row + ",∞", canEdit + "");
        if (isRendered()) {
            exec(getId() + "EditMap.set(\"" + row + ",∞\", \"" + canEdit + "\");");
        }
        return this;
    }

    /**
     * Sets whether the given column can be edited
     *
     * @param col the index of the column
     * @param canEdit flag indicating if it can be edited
     * @return this
     */
    public DataGrid setColumnEditable(int col, boolean canEdit) {
        editMap.put("∞," + col, canEdit + "");
        if (isRendered()) {
            exec(getId() + "EditMap.set(\"∞," + col + "\", \"" + canEdit + "\");");
        }
        return this;
    }

    /**
     * Sets whether the given row/col cell is editable
     *
     * @param row the row index
     * @param col the column index
     * @param canEdit flag indicating if the cell can be edited
     * @return this
     */
    public DataGrid setEditable(int row, int col, boolean canEdit) {
        editMap.put(row + "," + col, canEdit + "");
        if (isRendered()) {
            exec(getId() + "EditMap.set(\"" + row + "," + col + "\", \"" + canEdit + "\");");
        }
        return this;
    }

    /**
     * Sets the size of the DataGrid widget
     *
     * @param width number of pixels for widget width
     * @param height number of pixels for widget height
     * @return this
     */
    public DataGrid setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (isRendered()) {
            redraw();
        }
        return this;
    }

    /**
     * Sets the width of the cells
     *
     * @param cellWidth width of each column, in pixels
     * @return this
     */
    public DataGrid setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
        if (isRendered()) {
            redraw();
        }
        return this;
    }

    /**
     * @return a unique ID for this component
     */
    @Override
    public String getId() {
        return "dg" + hashCode();
    }

    /**
     * focuses onto the DataGrid so arrow keys can be used to navigate
     */
    public void focus() {
        exec(getId() + "grid.getCanvasNode().focus();");
    }

    /**
     * Notifies cellChangedListeners that a value has changed
     *
     * @param value a string that contains the row, column, and cell value
     */
    @Override
    public void fireEvent(String value) {
        if (!isEnabled()) {
            return;
        }
        String[] strs = value.split("\\s+");
        if (strs[0].equals("selected")) {
            cellSelectionListeners.update(strs[1] + " " + strs[2]);
        } else {
            String newValue = value.replaceFirst(strs[0], "").replaceFirst(strs[1], "").trim();
            data[Integer.parseInt(strs[0])][Integer.parseInt(strs[1])] = newValue;
            cellChangedListeners.update(value);
        }
    }

    /**
     * Called when this component is no longer needed; clears data and listeners
     */
    @Override
    public void dispose() {
        data = null;
        formatMap.clear();
        editMap.clear();
        cellChangedListeners.clear();
    }

    /**
     * Provides the HTML representation of this Component
     *
     * @return the HTML for this Component
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public String toHtml() {
        StringBuilder gridStr = new StringBuilder("var " + getId() + "data = [\n");
        for (int i = 0; i < data.length; i++) {
            gridStr.append("[");
            for (int j = 0; j < data[i].length; j++) {
                gridStr.append("\"").append(data[i][j].replace("\"", "\\\"")).append("\"").append(j == data[i].length - 1 ? "" : ",");
            }
            gridStr.append("]").append(i == data.length - 1 ? "\n" : ",\n");
        }
        gridStr.append("];\n");

        StringBuilder formatting = new StringBuilder("var " + getId() + "FormatMap = new Map();\n");
        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            formatting.append(getId() + "FormatMap.set(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");\n");
        }
        formatting.append("function " + getId() + "FormatFunction(row, cell, value, columnDef, dataContext) {\n"
                + "    var format = " + getId() + "FormatMap.get(row + ',' + cell);\n"
                + "    if (format === undefined) {\n"
                + "      format = " + getId() + "FormatMap.get('∞,' + cell);\n"
                + "    }\n"
                + "    if (format === undefined) {\n"
                + "      format = " + getId() + "FormatMap.get(row + ',∞');\n"
                + "    }\n"
                + "    if (format === undefined) {\n"
                + "      return value;\n"
                + "    }\n"
                + "    return \"<div style='\" + format + \"'>\" + value + \"</div>\";\n"
                + "  }\n");

        StringBuilder editing = new StringBuilder("var " + getId() + "EditMap = new Map();\n");
        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            editing.append(getId() + "EditMap.set(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");\n");
        }

        return "<div id='" + getId() + "' style='position:relative'>\n"
                + "  <div style='width:" + Math.min(width, data[0].length * cellWidth + 19) + "px;'>\n"
                + "    <div id='grid" + getId() + "' style='width:100%;height:" + height + "px;'></div>\n"
                + "  </div>\n"
                + " <script>\n"
                + gridStr
                + "\n"
                + formatting
                + editing
                + "  var columnCount = " + data[0].length + ";\n"
                + "  var " + getId() + "columns = [];\n"
                + "  for (var i = 0; i < columnCount; i++) {\n"
                + "    " + getId() + "columns.push({ id: i, name: (i + 1), field: i, width: " + cellWidth + ", formatter: " + getId() + "FormatFunction, editor: Slick.Editors.Text });\n"
                + "  }\n"
                + "  var " + getId() + "grid;\n" //declare it outside of the function so it can be accessed in setStyle method
                + "\n"
                + "  $(function () {\n"
                + "    " + getId() + "CellSelection = '';\n"
                + "    " + getId() + "grid = new Slick.Grid('#grid" + getId() + "', " + getId() + "data, " + getId() + "columns, { editable: true, enableAddRow: false, enableCellNavigation: true, asyncEditorLoading: false, autoEdit: false });\n"
                + "    " + getId() + "grid.setSelectionModel(new Slick.CellSelectionModel());\n"
                + "    " + getId() + "grid.registerPlugin(new Slick.AutoTooltips());\n"
                + "    " + getId() + "grid.getCanvasNode().focus();\n" //set keyboard focus on the grid
                + "    " + getId() + "grid.registerPlugin(new Slick.CellExternalCopyManager({ readOnlyMode: false, includeHeaderWhenCopying: false }));\n"
                + "    " + getId() + "grid.onBeforeEditCell.subscribe(function(e,args) {\n"
                + "      var canEdit = " + getId() + "EditMap.get(args.row + ',' + args.cell) !== 'false' && " + getId() + "EditMap.get(args.row + ',∞') !== 'false' && " + getId() + "EditMap.get('∞,' + args.cell) !== 'false';\n"
                + "      return canEdit;\n" //yes, it's editable
                + "    });\n"
                + "    " + getId() + "grid.onCellChange.subscribe(function(e,args) {\n"
                + "      " + createEvent("args.row + ' ' + args.cell + ' ' + " + getId() + "data[args.row][args.cell]") + "\n"
                + "    });\n"
                + "    " + getId() + "grid.onActiveCellChanged.subscribe(function(e,args) {\n"
                + "      var curSelection = args.row + ' ' + args.cell;\n"
                + "      if (" + getId() + "CellSelection !== curSelection) {\n"
                + "        " + getId() + "CellSelection = curSelection;\n"
                + "        " + createEvent("'selected ' + curSelection") + "\n"
                + "      }\n"
                + "    });"
                + "  })\n"
                + " </script>\n"
                + "</div>";
    }
}
