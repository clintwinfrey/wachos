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
package gov.mil.navy.nswcdd.wachos.desktop;

import gov.mil.navy.nswcdd.wachos.desktop.responder.FileResponder;
import static gov.mil.navy.nswcdd.wachos.desktop.responder.FileResponder.getResponse;
import gov.mil.navy.nswcdd.wachos.desktop.responder.Responder;
import gov.mil.navy.nswcdd.wachos.tools.ResourceServlet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import nanohttpd.HTTPSession;
import nanohttpd.NanoHTTPD;
import nanohttpd.Method;
import nanohttpd.Response;
import static nanohttpd.Response.newChunkedResponse;
import static nanohttpd.Response.newFixedLengthResponse;
import nanohttpd.Status;

/**
 * NanoServer is a NanoHTTPD that simplifies some complexities of implementing a
 * NanoHTTPD
 */
public class NanoServer extends NanoHTTPD {

    /**
     * the things that can respond to server requests from NanoHTTPD
     */
    private final List<Responder> responders = new ArrayList<>();

    /**
     * Constructor; finds an available port automatically
     */
    public NanoServer() {
        super(findAvailablePort()); //i.e. listen on the first port available
        NanoServer.this.add(new FileResponder("resource", "") {
            @Override
            public Response getResponse(String uri, Map<String, List<String>> params) {
                String normalizedUri = uri.replaceFirst("/" + getPageId() + "/", ""); //remove page id
                for (String resourceDir : ResourceServlet.getResourceFolders()) {
                    File file = new File(resourceDir, normalizedUri); //construct full path to file
                    if (file.exists()) {
                        return getResponse(file);
                    }
                }
                return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 Not Found"); //File not found or is a directory
            }
        });
    }

    /**
     * Finds an available port starting from a minimum port number.
     *
     * @return an available port number, or -1 if no port is available
     */
    private static int findAvailablePort() {
        for (int port = 49152; port <= 65535; port++) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                return port;//Port is available
            } catch (IOException e) {
            }
        }
        return 8080; // No available port found, see if 8080 is available
    }

    /**
     * Adds a Responder so that NanoServer can know how to respond when queried
     *
     * @param responder the thing that responds to the request
     */
    public void add(Responder responder) {
        responders.add(responder);
        Collections.sort(responders, (r1, r2) -> {
            return Integer.compare(r2.getPageId().length(), r1.getPageId().length()); //the biggest string goes first
        });
    }

    /**
     * Serves the application
     *
     * @param session the single user session
     * @return a Response that is created by a given Responder that was added
     */
    @Override
    public Response serve(HTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        for (Responder responder : responders) {
            String id = responder.getPageId();
            if ((Method.GET.equals(method) || Method.HEAD.equals(method)) && (uri.startsWith("/" + id + ""))) {
                Response response = responder.getResponse(session.getUri(), session.getParameters());
                if (response != null) {
                    return response;
                }
            }
        }

        //could be a META-INF resource
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("META-INF/resources" + uri);
        if (inputStream != null) {
            return newChunkedResponse(Status.OK, NanoHTTPD.getMimeTypeForFile(uri), inputStream);
        }

        System.err.println("Cannot find " + uri);
        return Response.newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "404 Not Found"); //not serving this page :(
    }

}
