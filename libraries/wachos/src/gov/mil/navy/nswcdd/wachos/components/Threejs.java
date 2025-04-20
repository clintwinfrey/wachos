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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * A component that integrates a Three.js visualization into the WACHOS
 * framework. This class extends Component to provide a customizable
 * Three.js canvas with support for loading JavaScript scripts.
 */
public class Threejs extends Component {

    /**
     * listener for handling value change events triggered by client-side
     * interactions
     */
    public final ComponentListeners threejsListeners = new ComponentListeners();
    /**
     * Array of JavaScript paths or URLs to be loaded for this component
     */
    private final String[] scripts;
    /**
     * the width of the Three.js container, defaulting to "600px"
     */
    private String width = "600px";
    /**
     * the height of the Three.js container, defaulting to "400px"
     */
    private String height = "400px";

    /**
     * Constructs a Three.js component with the specified JavaScript scripts.
     *
     * @param scripts the JavaScript files or URLs to be loaded for this
     * component
     */
    public Threejs(String... scripts) {
        this.scripts = scripts;
    }

    /**
     * Sets the width of the Three.js container
     *
     * @param width the width to set (e.g., "600px")
     * @return this Threejs instance for method chaining
     */
    @Override
    public Threejs setWidth(String width) {
        this.width = width;
        return this;
    }

    /**
     * Sets the height of the Three.js container
     *
     * @param height the height to set (e.g., "400px")
     * @return this Threejs instance for method chaining
     */
    @Override
    public Threejs setHeight(String height) {
        this.height = height;
        return this;
    }

    /**
     * Executes a JavaScript function on the client side with the provided value.
     * The real function name is dynamically generated based on the component's ID.
     *
     * @param value the value to pass to the client-side JavaScript function
     */
    public void threeExec(String value) {
        //call the client-side receive function with the provided value
        exec("receiveFrom" + getId() + "(\"" + value + "\");");
    }

    /**
     * Handles client-side events fired by user interactions with the Three.js
     * component. Notifies registered listeners of the updated value.
     *
     * @param value the text value that has changed due to the event
     */
    @Override
    public void fireEvent(String value) {
        threejsListeners.update(value); //notify listeners of the value change
    }

    /**
     * Returns a unique identifier for this Three.js component
     *
     * @return a unique ID prefixed with "three" followed by the object's hash
     * code
     */
    @Override
    public String getId() {
        return "three" + hashCode();
    }

    /**
     * Generates the HTML representation of the Three.js component, including
     * the container div and any associated JavaScript scripts. Scripts are
     * processed to replace WACHOS-specific placeholders and event handlers.
     *
     * @return the HTML string for rendering the component
     */
    @Override
    public String toHtml() {
        //initialize a StringBuilder to construct the HTML
        StringBuilder sb = new StringBuilder();

        //process each script provided in the constructor
        for (String script : scripts) {
            boolean includeScript = false;
            //check if the script is a local resource and not an external URL
            if (!script.toLowerCase().startsWith("http") && !script.equals("wjs/three.min.js")) {
                try {
                    //read the script content from the classpath
                    String str = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + script))).lines().collect(Collectors.joining("\n"));

                    //check if the script contains WACHOS-specific markers
                    if (str.contains("@wachoscanvas") || str.contains("fireWachosEvent(") || str.contains("receiveFromWachos(")) {
                        //define regex to replace fireWachosEvent calls
                        String regex = "fireWachosEvent\\((.*?)\\);";
                        //define replacement to transform fireWachosEvent to a changed event
                        String replacement = "changed" + layoutId + "({id: '" + getId() + "', value: $1})";

                        //modify the script: replace canvas ID, event handlers, and receive functions
                        sb.append("<script>" + str.replace("@wachoscanvas", getId()).replaceAll(regex, replacement).replace("receiveFromWachos(", "receiveFrom" + getId() + "(") + "</script>\n");
                        includeScript = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace(); //print any errors from loading scripts
                }
            }
            //if the script is an external URL or not processed locally, include it as a script tag
            if (!includeScript) {
                sb.append("<script src='").append(script).append("'></script>\n");
            }
        }

        //return the HTML div with the component's ID, dimensions, and scripts
        return "<div id=\"" + getId() + "\" style='width: " + width + "; height: " + height + "; border: 0'>\n"
                + sb.toString()
                + "</div>";
    }

    /**
     * Disposes of this component by clearing all registered listeners
     */
    @Override
    public void dispose() {
        threejsListeners.clear();
    }
}
