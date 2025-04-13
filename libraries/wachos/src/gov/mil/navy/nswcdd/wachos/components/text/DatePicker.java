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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DatePicker is a TextField that takes a date as input
 */
public class DatePicker extends TextField {

    /**
     * the date pattern used for this; not very much versatility, but it works
     * for now
     */
    String pattern = "MMM d, yyyy";

    /**
     * the date represented by this widget
     */
    Date date = new Date();

    /**
     * Constructor
     */
    public DatePicker() {
        this(new Date());
    }

    /**
     * Constructor
     *
     * @param date this component's value
     */
    public DatePicker(Date date) {
        super("");
        this.date = date;
        DatePicker.this.setDate(date);
    }

    /**
     * Constructor
     *
     * @param date this component's value
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public DatePicker(Date date, ComponentListener valueChangedListener) {
        this(date);
        super.valueChangedListeners.add(valueChangedListener);
    }

    /**
     * Sets the value of the DatePicker from when the user types a value in
     *
     * @param value the value received from user interaction
     */
    public void setValueFromTyping(String value) {
        this.value = value;
        valueChangedListeners.update(value);
    }

    /**
     * @return the Date of this DatePicker widget
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the Date of this DatePicker widget
     *
     * @param date the date to set it as
     */
    public void setDate(Date date) {
        this.date = date;
        super.setText(new SimpleDateFormat(pattern).format(date), true, true);
    }

    /**
     * Sets the Date of this DatePicker widget by parsing the value to a Date
     * Object
     *
     * @param value the String representation of the Date Object (must match the
     * format pattern of this DatePicker)
     * @return this
     */
    @Override
    public DatePicker setText(String value, boolean fireChangedEvent, boolean updateClient) {
        try {
            date = new SimpleDateFormat(pattern).parse(value);
            super.setText(value, fireChangedEvent, updateClient);
        } catch (ParseException e) {
            date = null;
        }
        return this;
    }

    /**
     * Provides the HTML representation of this Component
     *
     * @return the HTML for this Component
     */
    @Override
    public String toHtml() {
        return "<div id='" + getId() + "'>\n"
                + "<input type=\"text\" id='" + getId() + "Date' class='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all " + getId() + "Date" + (enabled ? "" : " ui-state-disabled") + "' value=\"" + value.replace("\"", "&quot;").replace("\\", "\\\\")
                + "\" " + getProperties() + "style=\"" + getStyle() + "\" oninput=\"tfThrottle(function() { changed" + layoutId + "({id: '" + getId() + "', value: document.getElementById('" + getId() + "Date').value}); }, 200);\"/>\n"
                + "<script>"
                + "  $jq(function() {\n"
                + "    $('#" + getId() + "Date').datepicker({\n"
                + "      dateFormat: \"M d, yy\"\n"
                + "    }).change(function() {\n"
                + "      changed" + layoutId + "({id: '" + getId() + "', value: document.getElementById('" + getId() + "Date').value});\n"
                + "    });"
                + "  });\n"
                + "</script></div>\n";

    }

}
