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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import nanohttpd.NanoHTTPD;
import nanohttpd.Response;
import static nanohttpd.Response.newFixedLengthResponse;
import nanohttpd.Status;

/**
 * FileResponder responds to a server request with a file
 */
public class FileResponder implements Responder {

    /**
     * the expected start of the URI
     */
    private final String pageId;
    /**
     * the accessible location where the requested file resides
     */
    private final String folder;

    /**
     * Constructor
     *
     * @param pageId the expected start of the URI
     * @param folder the folder path where resources are stored
     */
    public FileResponder(String pageId, String folder) {
        this.pageId = pageId;
        this.folder = folder;
    }

    /**
     * @return the expected start of the URI
     */
    @Override
    public String getPageId() {
        return pageId;
    }

    /**
     * Provides a file response to the server request
     *
     * @param uri the URI of the request
     * @param params any parameters that are a part of the request
     * @return a response to the given URI and parameters
     */
    @Override
    public Response getResponse(String uri, Map<String, List<String>> params) {
        String normalizedUri = uri.replaceFirst("/" + getPageId() + "/", ""); //remove page id
        File file = new File(folder, normalizedUri); //construct full path to file
        return getResponse(file);
    }

    /**
     * Provides a file response to the server request
     *
     * @param file the file to respond with
     * @return a file response
     */
    public static Response getResponse(File file) {
        //Check if the file exists and is not a directory
        if (file.exists() && !file.isDirectory()) {
            try {
                // Open the file
                FileInputStream fis = new FileInputStream(file);
                Response response = newFixedLengthResponse(Status.OK, NanoHTTPD.getMimeTypeForFile(file.getName()), fis, file.length());
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Internal Server Error: " + e.getMessage());
            }
        } else {
            return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 Not Found"); //File not found or is a directory
        }
    }

}
