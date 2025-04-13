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
import gov.mil.navy.nswcdd.wachos.tools.Color;

/**
 * Quill is a free, open source WYSIWYG editor built for the modern web. With
 * its modular architecture and expressive API, it is completely customizable to
 * fit any need.
 */
public class Quill extends TextArea {

    /**
     * the width of this component
     */
    private String width = "100%";
    /**
     * the height of this component
     */
    private String height = "100%";
    /**
     * the background color of this component
     */
    private Color background = Color.WHITE;
    /**
     * flag indicating if this is enabled for editing
     */
    private boolean editable = true;

    /**
     * Constructor
     */
    public Quill() {
        this("");
    }

    /**
     * Constructor
     *
     * @param text this component's initial text
     */
    public Quill(String text) {
        super(text);
    }

    /**
     * Constructor
     *
     * @param text this component's initial text
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public Quill(String text, ComponentListener valueChangedListener) {
        super(text, valueChangedListener);
    }

    /**
     * @return a unique ID for this component
     */
    @Override
    public String getId() {
        return "qwl" + hashCode();
    }

    /**
     * Sets the content of the Quill editor
     *
     * @param value the content, expected to be in a format specific to Quill
     * @return this Quill
     */
    @Override
    public Quill setText(String value) {
        super.setText(value);
        exec("quill" + getId() + ".setContents(" + WTools.desanitize(value) + ");");
        return this;
    }

    /**
     * Sets the text of the Quill editor; use this if you only have the text; no
     * formatting, etc.
     *
     * @param value the string of information
     * @return this Quill
     */
    public Quill setTextFromString(String value) {
        return setText("{\"ops\":[{\"insert\":\"" + value + "\\n\"}]}");
    }

    /**
     * Quill doesn't use properties, so don't do anything with this
     *
     * @param id the identification of the property to set
     * @param value the value of the property to set
     * @param updateClient if true, updates the value in the client
     * @return this component
     */
    @Override
    public Quill setProperty(String id, String value, boolean updateClient) {
        return this;
    }

    /**
     * Enables this component for user interaction
     *
     * @param editable flag indicating if the user can interact with this
     * component
     * @return this Component
     */
    @Override
    public Quill setEnabled(boolean editable) {
        if (this.editable == editable) {
            return this;
        }
        this.editable = editable;
        this.enabled = editable;
        if (isRendered()) {
            exec("$('#toolbar" + getId() + "')." + (editable ? "show" : "hide") + "(); quill" + getId() + ".enable(" + editable + ");");
        }
        return this;
    }

    /**
     * Sets the desired width of this component, using px, %, etc.
     *
     * @param width the desired width to use
     * @return this Component
     */
    @Override
    public Quill setWidth(String width) {
        this.width = width;
        redraw();
        return this;
    }

    /**
     * @return the width of this component
     */
    @Override
    public String getWidth() {
        return width;
    }

    /**
     * Sets the desired height of this component, using px, %, etc.
     *
     * @param height the desired height to use
     * @return this Component
     */
    @Override
    public Quill setHeight(String height) {
        this.height = height;
        redraw();
        return this;
    }

    /**
     * @return the height of this component
     */
    @Override
    public String getHeight() {
        return height;
    }

    /**
     * Sets the background color of this Component; use null to remove coloring
     *
     * @param background the background color
     * @return this Component
     */
    @Override
    public Quill setBackground(Color background) {
        this.background = background;
        redraw();
        return this;
    }

    /**
     * @return the background color of this Component; null indicates that there
     * is no background color
     */
    @Override
    public Color getBackground() {
        return background;
    }

    /**
     * @return the HTML used to represent this Component
     */
    @Override
    public String toHtml() {
        return "<div id='" + getId() + "' style='overflow: auto; height: " + height + "; width: " + width + "'>"
                + "<div style='height: 100%; width: 100%; background-color: " + WTools.toHex(background) + "; display:flex; flex-flow: column'>\n"
                + "  <div id='toolbar" + getId() + "' style='flex: 0 1 auto; padding: 3px'>\n"
                + "    <span class='ql-formats'><select class='ql-font'></select><select class='ql-size'></select></span>\n"
                + "    <span class='ql-formats'><button class='ql-bold'></button><button class='ql-italic'></button><button class='ql-underline'></button><button class='ql-strike'></button></span>\n"
                + "    <span class='ql-formats'><select class='ql-color'></select><select class='ql-background'></select></span>\n"
                + "    <span class='ql-formats'><button class='ql-script' value='sub'></button><button class='ql-script' value='super'></button></span>\n"
                + "    <span class='ql-formats'><button class='ql-header' value='1'></button><button class='ql-header' value='2'></button><button class='ql-blockquote'></button><button class='ql-code-block'></button></span>\n"
                + "    <span class='ql-formats'><button class='ql-list' value='ordered'></button><button class='ql-list' value='bullet'></button><button class='ql-indent' value='-1'></button><button class='ql-indent' value='+1'></button></span>\n"
                + "    <span class='ql-formats'><button class='ql-direction' value='rtl'></button><select class='ql-align'></select></span>\n"
                + "    <span class='ql-formats'><button class='ql-link'></button><button class='ql-image'></button><button class='ql-video'></button><button class='ql-formula'></button></span>\n"
                + "    <span class='ql-formats'><button class='ql-clean'></button></span>\n"
                + "  </div>\n"
                + "  <div id='editor" + getId() + "' style='flex: 1 1 auto'></div>\n"
                + "  <script>"
                + "    var quill" + getId() + " = new Quill('#editor" + getId() + "', { modules: { formula: true, syntax: true, toolbar: '#toolbar" + getId() + "' }, theme: 'snow' });"
                + (value.equals("") ? "" : "quill" + getId() + ".setContents(" + WTools.desanitize(value) + ");")
                + "    quill" + getId() + ".on('text-change', function(delta, source) {"
                + "      tfThrottle(function() { changed" + layoutId + "({id: '" + getId() + "', value: JSON.stringify(quill" + getId() + ".getContents()) }); }, 2000);" //whenever the text changes, update content (throtted at two seconds)
                + "    });"
                + (editable ? "" : "$('#toolbar" + getId() + "').hide(); quill" + getId() + ".enable(false);")
                + "</script>\n"
                + "</div>"
                + "</div>";
    }

}
