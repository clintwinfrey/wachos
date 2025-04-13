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
package gov.mil.navy.nswcdd.wachos.view;

import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * The view for the layout.xhtml composite component
 */
@Named(value = "layout")
@ViewScoped
public class LayoutView implements Serializable {

    /**
     * Generates the HTML to represent this Layout
     *
     * @param layout the Layout we're representing
     * @param session the user's session
     * @return the HTML that was generated
     */
    public String getHtml(Layout layout, WSession session) {
        layout.init(layout.getId(), session); //needs to init with itself as the master layout, which will recursively init everything in it
        StringBuilder sb = new StringBuilder();
        WTools.createToolTipsScript(layout, sb);
        layout.exec("$('#tooltipInit" + layout.getId() + "').remove();", 5000); //in five seconds, remove the tooltipInit script from the dom because it'll have been run already; this is for tidying
        return (layout.toHtml() + "<script id='tooltipInit" + layout.getId() + "'>" + sb.toString() + "</script>").replace("#LAYOUT_ID#", layout.getId());
    }

    /**
     * The user has triggered an update; fire an event on the server side; don't
     * do any redrawing, as the user already redrew it
     *
     * @param layout this thing contains the component that actually changed
     * @param session the user's session
     */
    public void fireEvent(Layout layout, WSession session) {
        String componentId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (componentId.equals("HEARTBEAT" + session.hashCode())) {
            session.heartbeat();
        } else {
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("value");
            layout.fireEvent(componentId, value);
        }
    }

    /**
     * If the margin has been specified for this Layout, then we will return
     * that; otherwise, we will return "8px"
     *
     * @param margin the margin value, which may be ""
     * @return the provided margin, unless that is "", in which case we return
     * "8px"
     */
    public String getMargin(String margin) {
        return margin.equals("") ? "8px" : margin;
    }

}
