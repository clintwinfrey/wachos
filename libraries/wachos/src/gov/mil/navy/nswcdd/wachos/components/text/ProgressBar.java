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

/**
 * ProgressBar is a component that visually displays the progress of some task.
 * As the task progresses towards completion, the progress bar displays the
 * task's percentage of completion. This percentage is typically represented
 * visually by a rectangle which starts out empty and gradually becomes filled
 * in as the task progresses.
 *
 * The value must be an integer, between 0 and 100. If the progress is unknown,
 * use a value of -1 to indicate that it is indeterminate
 */
public class ProgressBar extends TextComponent<ProgressBar> {

    /**
     * Constructor
     */
    public ProgressBar() {
        this(0);
    }

    /**
     * Constructor
     *
     * @param startingPercent this value must be an integer, between 0 and 100.
     * If the progress is unknown, use a value of -1 to indicate that it is
     * indeterminate
     */
    public ProgressBar(int startingPercent) {
        super("" + startingPercent);
        ProgressBar.this.setWidth("100%");
    }

    /**
     *
     * @param value a value between 0 and 100 for the progress bar; -1 if
     * indeterminate
     */
    public void setValue(int value) {
        setText("" + value, true, true);
    }

    /**
     * @return value (0 to 100) of the progress bar; -1 if indeterminate
     */
    public int getIntValue() {
        return Integer.parseInt(getText());
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
            return this; //nothing to update in server
        }
        this.value = value;
        if (updateClient && isRendered()) {
            if (value.equals("-1")) {
                exec("$jq('#prgss" + getId() + "').progressbar('option',{value:100});\n$jq('#prgss" + getId() + " > div').css({'background': \"url('icons/animated-overlay.gif')\", 'opacity':'0.25'});");
            } else {
                exec("$jq('#prgss" + getId() + "').progressbar('option',{value:" + value + "});\n$jq('#prgss" + getId() + " > div').css({'background':'','opacity':'1'});");
            }
        }
        if (fireChangedEvent) {
            valueChangedListeners.update(value);
        }
        return this;
    }

    /**
     * @return html representation of this progress bar
     */
    @Override
    public String toHtml() {
        return "<div id='" + getId() + "'" + (visible ? "" : " style='visibility: hidden'") + ">\n"
                + "<div id='prgss" + getId() + "'></div>\n"
                + "<script>\n"
                + "    $(function() {\n"
                + "        $jq('#prgss" + getId() + "').progressbar({ value: " + (value.equals("-1") ? "100" : value) + " });\n"
                + (value.equals("-1") ? "$jq('#prgss" + getId() + " > div').css({'background': \"url('icons/animated-overlay.gif')\", 'opacity':'0.25'});" : "")
                + "    });\n"
                + "</script>\n"
                + "</div>";
    }
}
