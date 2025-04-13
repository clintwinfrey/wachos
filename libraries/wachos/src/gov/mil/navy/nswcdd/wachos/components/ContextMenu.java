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

import java.util.ArrayList;
import java.util.List;

/**
 * ContextMenu is a menu that pops up when you do a right-click on the
 * application or a left-click on a component
 */
public class ContextMenu extends Component {

    /**
     * options that can be selected from this ContextMenu
     */
    private final List<String> options = new ArrayList<>();

    /**
     * perform this clickListener's action when the button is clicked
     */
    public final ComponentListeners clickListeners = new ComponentListeners();

    /**
     * Constructor
     *
     * @param options things that can be clicked on
     */
    public ContextMenu(List<String> options) {
        ContextMenu.this.setOptions(options);
    }

    /**
     * Constructor
     *
     * @param options things that can be clicked on
     * @param clickListener contains the event that happens when the user clicks
     */
    public ContextMenu(List<String> options, ComponentListener clickListener) {
        this(options);
        clickListeners.add(clickListener);
    }

    /**
     * Sets the list of options the user can click on
     *
     * @param options the list of options the user can click on
     */
    public void setOptions(List<String> options) {
        if (this.options.equals(options)) {
            return; //nothing to update
        }
        this.options.clear();
        this.options.addAll(options);
        if (isRendered()) { //if it's been drawn already, we need to update it
            String html = toHtml().replace("#LAYOUT_ID#", layoutId).replace("\"", "\\\"").replace("\n", "\\n");
            exec("$jq.contextMenu('destroy', '." + getId() + "');\n"
                    + "$('#" + getId() + "').replaceWith(\"" + html + "\");");
        }
    }

    /**
     * @return the list of options the user can click on
     */
    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    /**
     * When called, the click listener is notified that the button was clicked
     *
     * @param event contains the menu item that was selected
     */
    @Override
    public void fireEvent(String event) {
        if (!isEnabled()) {
            return;
        }
        clickListeners.update(event.replace("#contextclick ", ""));
    }

    /**
     * Converts this ContextMenu to an HTML String
     *
     * @return an HTML String representing this ContextMenu
     */
    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            sb.append("\"i").append(i).append("\": {name: \"").append(options.get(i).replace("\"", "\\\\\"")).append("\", className: 'ui-widget tf-contextmenu'},\n");
        }
        return "<script id=\"" + getId() + "\">\n"
                + "    $jq(function () {\n"
                + "        $jq.contextMenu({\n"
                + "            selector: '." + getId() + "',\n"
                + "            trigger: 'left',\n"
                + "            build: function(trigger, e) {\n"
                //we can't create a style that extends ui-widget, so we're going to do that dynamically right now for tf-contextmenu
                + "                if($('#tfContextDivExists').length == 0) {\n" //only do this if the div hasn't been added already
                + "                    $(\"head\").append('<style type=\"text/css\"></style>');\n"
                + "                    var style = $(\"head\").children(':last');\n"
                + "                    var color = $('.ui-widget').css('color');\n"
                + "                    var background = $('.ui-widget').css('background-color');\n"
                + "                    if (background.split(',').length == 4) {\n" //this is transparent, don't use it
                + "                        background = $('.ui-panel').css('background-image');\n"
                + "                        background = background.substring(background.indexOf('rgb'));\n"
                + "                        background = background.substring(0, background.indexOf(')') + 1);\n"
                + "                    }\n"
                + "                    style.html('.tf-contextmenu { color:' + color + '; background:' + background + '; }');\n"
                + "                    $(\"html\").append(\"<div id='tfContextDivExists'></div>\");\n" //add that div so this doesn't happen every time you create a menu
                + "                }\n"
                //color the context-menu-list we're creating, on a timer because I can't figure out how to color this beforehand or immediately when it's created
                + "                var timeouts = [1, 10, 100, 200, 1000];\n" //set the style at this many milliseconds, and keep trying in case this takes up to a full second
                + "                for (var i = 0; i < timeouts.length; i++) {\n"
                + "                    setTimeout(function() { \n"
                + "                        $('.context-menu-list').css('background-color', $('.tf-contextmenu').css('background-color'));\n"
                + "                    }, timeouts[i]);\n"
                + "                }\n"
                //make the items and the callback for when an item is clicked
                + "                return {\n"
                + "                    callback: function(key, options) {\n"
                + "                        changed" + layoutId + "({id: '" + getId() + "', value: '#contextclick ' + key.substring(1)});"
                + "                    },\n"
                + "                    items: {\n"
                + sb.toString()
                + "                    }\n"
                + "                };\n"
                + "            }\n"
                + "        });\n"
                + "    });\n"
                + "</script>";
    }

    /**
     * Removes this ContextMenu from the DOM
     */
    @Override
    public void dispose() {
        exec("$jq.contextMenu('destroy', '." + getId() + "');");
        clickListeners.clear();
    }

    /**
     * @return a custom DOM identification
     */
    @Override
    public String getId() {
        return "cm" + hashCode();
    }

}
