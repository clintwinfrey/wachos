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
package gov.mil.navy.nswcdd.wachos.components.text;

import gov.mil.navy.nswcdd.wachos.components.ComponentListener;

/**
 * A CheckBox allows the user to select/deselect by clicking inside of a square
 */
public class CheckBox extends TextComponent {

    /**
     * Constructor
     *
     * @param selected this component's value
     */
    public CheckBox(boolean selected) {
        super(selected + "");
    }

    /**
     * Constructor
     *
     * @param selected this component's value
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public CheckBox(boolean selected, ComponentListener valueChangedListener) {
        this(selected);
        valueChangedListeners.add(valueChangedListener);
    }

    /**
     * Sets the value of this component
     *
     * @param value the new value of this component
     * @param fireChangedEvent if true, executes the valueChangedListener's
     * update method
     * @param updateClient if true, updates the value in the client
     * @return this
     */
    @Override
    public TextComponent setText(String value, boolean fireChangedEvent, boolean updateClient) {
        if (this.value != null && this.value.equals(value)) {
            return this; //nothing to update
        }
        this.value = value;
        if (updateClient && isRendered()) {
            exec(Boolean.parseBoolean(value) ? "$('#" + getId() + "').removeClass('ui-icon-blank');\n$('#" + getId() + "').addClass('ui-icon-check');\n"
                    : "$('#" + getId() + "').removeClass('ui-icon-check');\n$('#" + getId() + "').addClass('ui-icon-blank');\n");
        }
        if (fireChangedEvent) {
            valueChangedListeners.update(value);
        }
        return this;
    }

    /**
     * Sets this CheckBox's value
     *
     * @param selected the value to set it as
     * @return this
     */
    public CheckBox setSelected(boolean selected) {
        setText(selected + "", true, true);
        return this;
    }

    /**
     * @return the current selection status of this CheckBox
     */
    public boolean isSelected() {
        return Boolean.parseBoolean(value);
    }

    /**
     * Enables this component for user interaction
     *
     * @param enabled flag indicating if the user can interact with this
     * component
     * @return this CheckBox
     */
    @Override
    public CheckBox setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setStyle("pointer-events", enabled ? "auto" : "none"); //need to override for checkbox because without this line, the user could still toggle the disabled checkbox
        return this;
    }

    /**
     * @return the HTML used to represent this Component
     */
    @Override
    public String toHtml() {
        return "<div id='" + getId() + "' class='ui-chkbox ui-widget ui-corner-all ui-state-default'>\n"
                + "<span id='chk" + getId() + "' " + getProperties() + "style='cursor: pointer' class='ui-chkbox-icon ui-icon ui-c ui-icon-" + (isSelected() ? "check " : "blank ") + (enabled ? "" : "ui-state-disabled") + "' onclick=\"clicked" + getId() + "();\"></span>\n"
                + "<script>\n"
                + "function clicked" + getId() + "() {\n"
                + "    if ($('#chk" + getId() + "').hasClass('ui-icon-blank')) {\n"
                + "        $('#chk" + getId() + "').removeClass('ui-icon-blank');\n"
                + "        $('#chk" + getId() + "').addClass('ui-icon-check');\n"
                + "        changed" + layoutId + "({id: '" + getId() + "', value: 'true'});\n"
                + "    } else {\n"
                + "        $('#chk" + getId() + "').removeClass('ui-icon-check');\n"
                + "        $('#chk" + getId() + "').addClass('ui-icon-blank');\n"
                + "        changed" + layoutId + "({id: '" + getId() + "', value: 'false'});\n"
                + "    }\n"
                + "}\n"
                + "</script>\n"
                + "</div>";
    }

}
