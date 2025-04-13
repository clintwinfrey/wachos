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
import gov.mil.navy.nswcdd.wachos.tools.WTools;

/**
 * A Slider is a widget that allows the user to drag within a range in order to
 * set a number value
 */
public class Slider extends TextComponent {

    /**
     * minimum value of the slider
     */
    private double min;
    /**
     * maximum value of the slider
     */
    private double max;
    /**
     * step size when moving the slider
     */
    private double step;

    /**
     * Constructor
     *
     * @param value the initial value of the slider
     * @param min the minimum value of the slider
     * @param max the maximum value of the slider
     * @param step the step size when moving the slider
     */
    public Slider(double value, double min, double max, double step) {
        super(value + "");
        this.min = min;
        this.max = max;
        this.step = step;
        Slider.this.setWidth("100%");
    }

    /**
     * Constructor
     *
     * @param value the initial value of the slider
     * @param min the minimum value of the slider
     * @param max the maximum value of the slider
     * @param step the step size when moving the slider
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public Slider(double value, double min, double max, double step, ComponentListener valueChangedListener) {
        super(value + "", valueChangedListener);
        this.min = min;
        this.max = max;
        this.step = step;
        Slider.this.setWidth("100%");
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
    public Slider setText(String value, boolean fireChangedEvent, boolean updateClient) {
        if (this.value != null && this.value.equals(value)) {
            return this; //nothing to update
        }
        this.value = value;
        if (updateClient && isRendered()) {
            exec("if (!dragging" + getId() + ") {\n"
                    + "    animating" + getId() + " = true;\n"
                    + "    $('#sld" + getId() + "').slider(\"option\", \"value\", " + value + ");\n"
                    + "    animating" + getId() + " = false;\n"
                    + "}");
        }
        if (fireChangedEvent) {
            valueChangedListeners.update(value);
        }
        return this;
    }

    /**
     * Sets the minimum value of the time slider
     *
     * @param min the new minimum value
     */
    public void setMin(double min) {
        this.min = min;
        exec("$('#sld" + getId() + "').slider(\"option\", \"min\", " + min + ");");
    }

    /**
     * @return the minimum value of the time slider
     */
    public double getMin() {
        return min;
    }

    /**
     * Sets the maximum value of the time slider
     *
     * @param max the new minimum value
     */
    public void setMax(double max) {
        this.max = max;
        exec("$('#sld" + getId() + "').slider(\"option\", \"max\", " + max + ");");
    }

    /**
     * @return the maximum value of the time slider
     */
    public double getMax() {
        return max;
    }

    /**
     * Sets the step size of the time slider
     *
     * @param step the step size when moving the slider
     */
    public void setStep(double step) {
        this.step = step;
        exec("$('#sld" + getId() + "').slider(\"option\", \"step\", " + step + ");");
    }

    /**
     * @return the step size of the time slider
     */
    public double getStep() {
        return step;
    }

    /**
     * @return html representation of this time slider
     */
    @Override
    public String toHtml() {
        return "<div id='" + getId() + "' style='padding: .3em .7em'>\n" //slider needs a little padding, because otherwise it gets cut off
                + "<div id='sld" + getId() + "'></div>\n"
                + "<script>\n"
                + "    var animating" + getId() + " = false;\n"
                + "    var dragging" + getId() + " = false;\n"
                + "    $(function() {\n"
                + "        $('#sld" + getId() + "').slider({\n"
                + "            range: 'max',\n"
                + "            min: " + getMin() + ",\n"
                + "            max: " + getMax() + ",\n"
                + "            step: " + getStep() + ",\n"
                + "            value: " + getText() + ",\n"
                + "            change: function(event, ui) {\n"
                + "                if (!animating" + getId() + ") {\n"
                + "                    tfThrottle(function() { changed" + layoutId + "({id: '" + getId() + "', value: ui.value}); }, " + (WTools.isDesktopMode() ? "20" : "100") + ");\n"
                + "                }\n"
                + "            },\n"
                + "            slide: function(event, ui) {\n"
                + "                if (!animating" + getId() + ") {\n"
                + "                    tfThrottle(function() { changed" + layoutId + "({id: '" + getId() + "', value: ui.value}); }, " + (WTools.isDesktopMode() ? "20" : "100") + ");\n"
                + "                    dragging" + getId() + " = true;\n"
                + "                }\n"
                + "            },\n"
                + "            stop: function(eevent, ui) {\n"
                + "                if (!animating" + getId() + ") {\n"
                + "                    tfThrottle(function() { changed" + layoutId + "({id: '" + getId() + "', value: ui.value}); }, " + (WTools.isDesktopMode() ? "20" : "100") + ");\n"
                + "                    dragging" + getId() + " = false;\n"
                + "                }\n"
                + "            }\n"
                + "        });\n"
                + "    });\n"
                + "</script>\n"
                + "</div>";
    }

}
