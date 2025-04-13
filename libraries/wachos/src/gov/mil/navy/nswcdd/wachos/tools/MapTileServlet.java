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
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.omnifaces.servlet.FileServlet;

/**
 * MapTileServlet is a wrapper around omnifaces' FileServlet, but it
 * specifically defines the way to retrieve map tiles from a given location. It
 * will cache the map data locally as locations are visited.
 */
@WebServlet("/mapdata/*")
public class MapTileServlet extends FileServlet {

    /**
     * the local cache directory
     */
    private static String CACHE;
    /**
     * the url of where tiles are downloaded, excluding the pattern
     */
    private static String URL;
    /**
     * e.g. {z}/{y}/{x}.png
     */
    private static String PATTERN;
    /**
     * if not online, then don't bother to download files externally
     */
    private static boolean ONLINE;
    /**
     * the url of the map tiles
     */
    public static String MAP_TILE_URL;
    /**
     * If the server returns a file at a certain zoom rate that is not really
     * there, then the image might contain text along the lines of 'file not
     * found', which looks really bad. This ensures that an image like that
     * isn't returned. Instead, BLANK will be returned.
     */
    private static byte[] UNAVAILABLE;
    /**
     * a blank image, in the event the map tile is not available
     */
    private static File BLANK;

    /**
     * Initializes the map tile servlet
     *
     * @param workingDir the current working directory
     * @param cache the local cache directory
     * @param url the url of where tiles are downloaded, excluding the pattern
     * @param pattern e.g. {z}/{y}/{x}.png
     * @param online if not online, then don't bother to download files
     * externally
     */
    public static void init(String workingDir, String cache, String url, String pattern, boolean online) {
        CACHE = new File(workingDir, cache).getAbsolutePath();
        URL = url;
        PATTERN = pattern;
        ONLINE = online;
        MAP_TILE_URL = new File(cache).getName() + "/" + url.replace("https://", "").replace("http://", "") + "{z}/{x}/{y}" + pattern.substring(pattern.indexOf("."));
        try {
            String dir = new File(cache).getParent() + "/" + new File(cache).getName() + "/" + url.replace("https://", "").replace("http://", "");
            UNAVAILABLE = Files.readAllBytes(new File(dir + "/" + "unavailable" + pattern.substring(pattern.indexOf("."))).toPath());
            BLANK = new File(dir + "/" + "blank" + pattern.substring(pattern.indexOf(".")));
        } catch (IOException e) {
            UNAVAILABLE = null;
        }
    }

    /**
     * Gets the map tile
     *
     * @param request the requested tile
     * @return a 256x256 pixel map tile that matches the given request
     */
    @Override
    protected File getFile(HttpServletRequest request) {
        return getFile(request.getPathInfo());
    }

    /**
     * Find the map tile and return it
     * 
     * @param pathInfo
     * @return the map tile
     */
    public static File getFile(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            throw new IllegalArgumentException();
        }

        File file = new File(CACHE, pathInfo);
        if (ONLINE && !file.exists()) {
            //download the file
            String z = file.getParentFile().getParentFile().getName();
            String x = file.getParentFile().getName();
            String fileType = PATTERN.substring(PATTERN.indexOf("."));
            String y = file.getName().replace(fileType, "");
            String url = URL + PATTERN.replace("{x}", x).replace("{y}", y).replace("{z}", z);
            try (InputStream in = new URL(url).openStream()) {
                if (UNAVAILABLE != null && Integer.parseInt(z) > 13 && Arrays.equals(in.readAllBytes(), UNAVAILABLE)) {
                    return BLANK;
                } else {
                    file.getParentFile().mkdirs();
                    Files.copy(in, Paths.get(file.toString()));
                }
            } catch (Exception e) {
                return null;
            }
        }
        return file;
    }

}
