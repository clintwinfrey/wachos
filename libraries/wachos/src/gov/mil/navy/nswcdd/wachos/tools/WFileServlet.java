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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

/**
 * WFileServlet provides a way to reference local files on the system; it also
 * allows the user to
 */
public class WFileServlet extends HttpServlet {

    /**
     * send this if the file is not valid, or the color is 0,0,0,0
     */
    private static final BufferedImage BLANK_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    /**
     * sets the blank image's only pixel to be transparent
     */
    static {
        BLANK_IMAGE.setRGB(0, 0, (0xFF));
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        reallyProcessRequest(request, response);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Returns a file from one of the specified locations";
    }

    /**
     * @return the folders that can be accessed in this WFileServlet
     */
    public static List<String> getAccessibleFolders() {
        return new ArrayList<>(ResourceServlet.RESOURCE_FOLDERS);
    }

    /**
     * Determines whether the given folder can be accessed
     *
     * @param folder check this folder for accessibility
     * @return flag indicating if the folder can be accessed
     */
    public static boolean canAccess(File folder) {
        for (String accessibleFolder : ResourceServlet.RESOURCE_FOLDERS) {
            String path = getPath(folder);
            if (path.equals(accessibleFolder) || path.startsWith(accessibleFolder + File.separator)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the canonical path, or if that fails then it gets the absolute path
     *
     * @param file get the path for this
     * @return the path
     */
    public static String getPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * @return the highest folder to which this selector allows access
     */
    public static String getBaseAccessibleFolder() {
        String commonPath = "";
        String[][] folders = new String[ResourceServlet.RESOURCE_FOLDERS.size()][];
        for (int i = 0; i < ResourceServlet.RESOURCE_FOLDERS.size(); i++) {
            folders[i] = ResourceServlet.RESOURCE_FOLDERS.get(i).replace("\\", "/").split("/"); //split on file separator
        }
        for (int j = 0; j < folders[0].length; j++) {
            String thisFolder = folders[0][j]; //grab the next folder name in the first path
            boolean allMatched = true; //assume all have matched in case there are no more paths
            for (int i = 1; i < folders.length && allMatched; i++) { //look at the other paths
                if (folders[i].length < j) { //if there is no folder here
                    allMatched = false; //no match
                    break; //stop looking because we've gone as far as we can
                }
                //otherwise
                allMatched &= folders[i][j].equals(thisFolder); //check if it matched
            }
            if (allMatched) { //if they all matched this folder name
                commonPath += thisFolder + File.separator; //add it to the answer
            } else {//otherwise
                break;//stop looking
            }
        }
        return getPath(new File(commonPath));
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static void reallyProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileStr = getRequestVariable("file", request);
        File file = ResourceServlet.getFile(fileStr);
        if (file != null && (file.isDirectory() || !file.exists())) {
            file = null; //don't process a request on a folder
        }
        String color = getRequestVariable("color", request);

        try (OutputStream out = response.getOutputStream()) {
            if (fileStr != null) {
                if (fileStr.startsWith("jar:file:")) { //it's a file in a jar
                    ZipFile jarFile = new ZipFile(fileStr.substring(9, fileStr.indexOf("!")));
                    ZipEntry jarEntry = jarFile.getEntry(fileStr.substring(fileStr.indexOf("!") + 2));
                    InputStream is = jarFile.getInputStream(jarEntry);
                    byte[] buffer = new byte[10240]; //10 KB
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } else if (file != null && !file.isDirectory()) { //it's a regular file
                    InputStream is = new FileInputStream(file); //we'll respond with the file
                    byte[] buffer = new byte[10240]; //10 KB
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } else {
                    ImageIO.write(BLANK_IMAGE, "png", out); //nothing to draw
                }
            } else if (color != null && !color.equals("0,0,0,0")) {
                BufferedImage img;
                String[] rgb = color.split(",");
                img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setBackground(new java.awt.Color(0, 0, 0, 0));
                g.setColor(new java.awt.Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]), Integer.parseInt(rgb[3])));
                g.fillOval(0, 0, 20, 20);
                ImageIO.write(img, "png", out); //write it out
            } else {
                ImageIO.write(BLANK_IMAGE, "png", out); //nothing to draw
            }
            response.setContentType(color != null ? "image/png" : file == null ? "image/png" : file.getName().endsWith(".glb") ? "model/gltf-binary" : Files.probeContentType(file.toPath()));
        }
    }

    /**
     * Gets the value of the request variable
     *
     * @param id the identification of the variable to retrieve
     * @param request servlet request
     * @return the value, if it exists
     */
    private static String getRequestVariable(String id, HttpServletRequest request) {
        String val = request.getParameter(id);
        if (val == null) {
            val = request.getParameter("amp;" + id);
        }
        return val;
    }

}
