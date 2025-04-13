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
 * Image allows a picture to be placed in the application (displays the image
 * URL provided)
 */
public class Image extends TextComponent {

    /**
     * Constructor
     *
     * @param url this component's image address
     */
    public Image(String url) {
        super(url);
        this.clientTextProperty = "attr.src";
    }

    /**
     * Sets the image icon of this button. Most likely, you don't need to call a
     * value listener whenever you set the button's content. As such, this is a
     * faster way because it only sets the value in the client
     *
     * @param src the image icon
     * @return this
     */
    public Image setImage(String src) {
        setText(src);
        redraw();
        return this;
    }

    /**
     * Sets the size of this ImageDropButton
     *
     * @param size width is set to this, and height is set to auto
     * @return this
     */
    public Image setSize(String size) {
        setStyle("width", size).setStyle("height", "auto");
        redraw();
        return this;
    }

    /**
     * @return html representation of this image
     */
    @Override
    public String toHtml() {
        return "<img id=\"" + getId() + "\" class=\"" + getId() + "\" " + getProperties() + "style=\"" + getStyle() + "\" title='' src=\"" + value.replace("\"", "&quot;").replace("\\", "\\\\") + "\"/>";
    }
}
