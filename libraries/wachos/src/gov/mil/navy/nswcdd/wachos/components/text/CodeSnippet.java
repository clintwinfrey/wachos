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
 * CodeSnippet displays source code in a pretty way
 */
public class CodeSnippet extends TextComponent {

    /**
     * the code to display
     */
    private String code;

    /**
     * Constructor
     *
     * @param code the source code to display
     */
    public CodeSnippet(String code) {
        super(code);
        this.code = code;
    }

    /**
     * Sets the text of this code snippet
     *
     * @param code the source code to display
     * @return this
     */
    @Override
    public CodeSnippet setText(String code) {
        this.code = code;
        redraw();
        return this;
    }

    /**
     * @return the displayed source code as text
     */
    @Override
    public String getText() {
        return code;
    }

    /**
     * @return the HTML used to represent this Component
     */
    @Override
    public String toHtml() {
        return "<div id='" + getId() + "' style='font-family: monospace !important; font-size: .8em'><pre><code class=\"hljs\">" + code.replace("<", "&lt;").replace(">", "&gt;")
                .replace("/*", "/*TF").replaceAll("//([^\\n]*)\\n", "/*$1*/\n").replace("\n", "<br/>")
                + "</code></pre><script>hljs.highlightBlock($(\"#" + getId() + "\").get(0)); $('#" + getId() + " .hljs-comment').contents().each(function(){\n"
                + "if (this.textContent.includes('/*TF')) {\n"
                + "this.textContent = this.textContent.replace('/*TF','/*');\n"
                + "} else {\n"
                + "this.textContent = this.textContent.replace('/*','//').replace('*/', '');\n"
                + "}\n"
                + "});</script></div>";
    }

}
