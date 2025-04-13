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
package gov.mil.navy.nswcdd.wachos.components.threejs;

import gov.mil.navy.nswcdd.wachos.components.Component;
import gov.mil.navy.nswcdd.wachos.components.ComponentListeners;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Threejs is a simple Threejs example component
 */
public class Threejs extends Component {

    /**
     * contains the event that happens when the value changes
     */
    public final ComponentListeners threejsListeners = new ComponentListeners();

    private String[] scripts;

    private String width = "600px", height = "400px";

    public Threejs(String... scripts) {
        this.scripts = scripts;
    }

    @Override
    public Threejs setWidth(String width) {
        this.width = width;
        return this;
    }

    @Override
    public Threejs setHeight(String height) {
        this.height = height;
        return this;
    }

    public void threeExec(String value) {
        exec("receiveFrom" + getId() + "(\"" + value + "\");");
    }

    /**
     * Client-side user interaction has fired an event on this component
     *
     * @param value the text value that has potentially changed
     */
    @Override
    public void fireEvent(String value) {
        threejsListeners.update(value);
    }

    /**
     * @return this object's unique identification
     */
    @Override
    public String getId() {
        return "three" + hashCode();
    }

    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        for (String script : scripts) {
            boolean includeScript = false;
            if (!script.toLowerCase().startsWith("http") && !script.equals("wjs/three.min.js")) {
                try {
                    String str = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + script))).lines().collect(Collectors.joining("\n"));
                    if (str.contains("@wachoscanvas") || str.contains("fireWachosEvent(") || str.contains("receiveFromWachos(")) {
                        String regex = "fireWachosEvent\\('(\\w+)'\\)";
                        String replacement = "changed" + layoutId + "({id: '" + getId() + "', value: '$1'})";
                        sb.append("<script>" + str.replace("@wachoscanvas", getId()).replaceAll(regex, replacement).replace("receiveFromWachos(", "receiveFrom" + getId() + "(") + "</script>\n");
                        includeScript = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!includeScript) {
                sb.append("<script src='").append(script).append("'></script>\n");
            }
        }
        return "<div id=\"" + getId() + "\" style='width: " + width + "; height: " + height + "; border: 0'>\n"
                + sb.toString()
                + "</div>";
    }

    /**
     * To be called when this component is no longer needed; clears
     * valueChangedListeners
     */
    @Override
    public void dispose() {
        threejsListeners.clear();
    }

}
