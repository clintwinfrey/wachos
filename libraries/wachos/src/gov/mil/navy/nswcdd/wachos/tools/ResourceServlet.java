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
package gov.mil.navy.nswcdd.wachos.tools;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.omnifaces.servlet.FileServlet;

/**
 * ResourceServlet is a wrapper around omnifaces' FileServlet, but it
 * specifically defines the "resource" folder. The resource folder can be
 * specified as follows: ResourceServlet.FOLDER =
 * "C:/OSM/web/libraries/wachos/dist/javadoc";
 */
@WebServlet("/resource/*")
public class ResourceServlet extends FileServlet {

    /**
     * for security reasons, only files within this folder can be downloaded
     */
    protected static final List<String> RESOURCE_FOLDERS = new ArrayList<>();

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Returns a file from one of the resource locations";
    }

    /**
     * Adds a resource folder that can be accessed via this servlet
     *
     * @param workingDir the directory we're working from
     * @param resourceFolder the resource folder we're allowing access to
     */
    public static void addResourceFolder(String workingDir, String resourceFolder) {
        try {
            String str = new File(workingDir, resourceFolder).getCanonicalPath();
            if (!RESOURCE_FOLDERS.contains(str) && new File(str).exists()) {
                RESOURCE_FOLDERS.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the folders that can be accessed in this WFileServlet
     */
    public static List<String> getResourceFolders() {
        return new ArrayList<>(RESOURCE_FOLDERS);
    }

    /**
     * Gets the file per the request
     *
     * @param request the requested file
     * @return the requested file
     */
    @Override
    protected File getFile(HttpServletRequest request) {
        return getFile(request.getPathInfo());
    }

    /**
     * Gets a file with the relative path to one of the resource folders
     *
     * @param relativePath the path to the requested file
     * @return the file
     */
    public static File getFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty() || "/".equals(relativePath)) {
            throw new IllegalArgumentException();
        }
        for (String resourceFolder : RESOURCE_FOLDERS) {
            try {
                String path = new File(relativePath).isAbsolute() ? new File(relativePath).getCanonicalPath() : new File(resourceFolder, relativePath).getCanonicalPath();
                File ret = new File(path);
                if (ret.exists() && (path.equals(resourceFolder) || path.startsWith(resourceFolder + File.separator))) {
                    return ret;
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

}
