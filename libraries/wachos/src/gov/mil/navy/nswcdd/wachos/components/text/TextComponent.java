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

import gov.mil.navy.nswcdd.wachos.components.Component;
import gov.mil.navy.nswcdd.wachos.components.ComponentListener;
import gov.mil.navy.nswcdd.wachos.components.ComponentListeners;
import gov.mil.navy.nswcdd.wachos.tools.WTools;

/**
 * TextComponent is a Component that has a text String
 *
 * @param <T> the type of TextComponent, which must be a TextComponent
 */
public abstract class TextComponent<T extends TextComponent> extends Component<T> {

    /**
     * the text value of this component
     */
    protected String value;
    /**
     * contains the event that happens when the value changes
     */
    public final ComponentListeners valueChangedListeners = new ComponentListeners();
    /**
     * the property type for this text component (attr.src, val, html, text)
     */
    protected String clientTextProperty = "text";

    /**
     * Constructor
     *
     * @param text this component's initial text
     */
    public TextComponent(String text) {
        this.value = text;
    }

    /**
     * Constructor
     *
     * @param text this component's initial text
     * @param valueChangedListener contains the event that happens when the
     * value changes
     */
    public TextComponent(String text, ComponentListener valueChangedListener) {
        this.value = text;
        valueChangedListeners.add(valueChangedListener);
    }

    /**
     * Sets the value of this component
     *
     * @param value the new value of this component
     * @return this
     */
    public TextComponent setText(String value) {
        return setText(value, true, true);
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
    public TextComponent setText(String value, boolean fireChangedEvent, boolean updateClient) {
        if (isRendered()) {
            value = WTools.desanitize(value).replace("\n", "&#13;").replace("'", "&apos;");
        }
        if (this.value != null && this.value.equals(value)) {
            return this; //nothing to update in server
        }
        this.value = value;
        setProperty(clientTextProperty, value, updateClient);
        if (fireChangedEvent) {
            valueChangedListeners.update(value);
        }
        return this;
    }

    /**
     * Client-side user interaction has fired an event on this component
     *
     * @param value the text value that has potentially changed
     */
    @Override
    public void fireEvent(String value) {
        if (!isEnabled()) {
            return;
        }
        if (this instanceof Button && value.startsWith("#buttonclick")) {
            ((Button) this).fireClickEvent();
        } else {
            setText(WTools.sanitize(value), true, false);
        }
    }

    /**
     * @return the component's String value
     */
    public String getText() {
        return value;
    }

    /**
     * @return the component's String value
     */
    @Override
    public String toString() {
        return getText();
    }

    /**
     * @return this object's unique identification
     */
    @Override
    public String getId() {
        return "vc" + hashCode();
    }

    /**
     * To be called when this component is no longer needed; clears
     * valueChangedListeners
     */
    @Override
    public void dispose() {
        valueChangedListeners.clear();
    }
}
