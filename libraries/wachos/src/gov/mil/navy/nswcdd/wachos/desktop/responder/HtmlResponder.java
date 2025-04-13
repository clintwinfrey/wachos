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
package gov.mil.navy.nswcdd.wachos.desktop.responder;

import java.util.List;
import java.util.Map;
import nanohttpd.Response;
import static nanohttpd.Response.newFixedLengthResponse;
import nanohttpd.Status;

/**
 * HtmlResponder responds to a server request with HTML code
 */
public class HtmlResponder implements Responder {

    /**
     * the expected start of the URI
     */
    private final String pageId;
    /**
     * the HTML to be served
     */
    private final String html;

    /**
     * Constructor
     *
     * @param pageId the expected start of the URI
     * @param html the HTML to be served
     */
    public HtmlResponder(String pageId, String html) {
        this.pageId = pageId;
        this.html = html;
    }

    /**
     * @return the expected start of the URI
     */
    @Override
    public String getPageId() {
        return pageId;
    }

    /**
     * Provides an HTML response to the server request
     *
     * @param uri the URI of the request
     * @param params any parameters that are a part of the request
     * @return a response to the given URI and parameters
     */
    @Override
    public Response getResponse(String uri, Map<String, List<String>> params) {
        return newFixedLengthResponse(Status.OK, "text/html", html);
    }

}
