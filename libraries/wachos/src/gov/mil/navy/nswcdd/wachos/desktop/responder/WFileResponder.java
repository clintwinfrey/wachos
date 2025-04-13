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

import gov.mil.navy.nswcdd.wachos.tools.ResourceServlet;
import jakarta.servlet.ServletException;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import nanohttpd.NanoHTTPD;
import nanohttpd.Response;
import static nanohttpd.Response.newFixedLengthResponse;
import nanohttpd.Status;

/**
 * WFileResponder responds with a file according to the specified accessible
 * folders
 */
public final class WFileResponder implements Responder {

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
     * Constructor
     */
    public WFileResponder() {
    }

    /**
     * @return the expected start of the URI
     */
    @Override
    public String getPageId() {
        return "FileServlet";
    }

    /**
     * Provides a file resource response to the server request
     *
     * @param uri the URI of the request
     * @param params any parameters that are a part of the request
     * @return a file response to the given URI and parameters
     */
    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Response getResponse(String uri, Map<String, List<String>> params) {
        try {
            return getFileResponse(params);
        } catch (Exception e) {
            System.err.println("Failure: " + params.toString().replaceAll("\n", ""));
            try {
                InputStream is = toInputStream(BLANK_IMAGE);
                return newFixedLengthResponse(Status.OK, "", is, (long) is.available());
            } catch (Exception e2) {
                return null;
            }
        }
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
    private Response getFileResponse(Map<String, List<String>> params) throws IOException {
        String mimeType = "image/png";
        InputStream is;

        String fileStr = getParameter(params, "file");
        File file = fileStr == null ? null : getFile(fileStr);
        if (file != null && (file.isDirectory() || !file.exists())) {
            file = null; //don't process a request on a folder
        }
        String color = getParameter(params, "color");
        if (fileStr != null && fileStr.startsWith("jar:file:")) { //it's a file in a jar
            ZipFile jarFile = new ZipFile(fileStr.substring(9, fileStr.indexOf("!")));
            ZipEntry jarEntry = jarFile.getEntry(fileStr.substring(fileStr.indexOf("!") + 2));
            is = jarFile.getInputStream(jarEntry);
            mimeType = NanoHTTPD.getMimeTypeForFile(jarEntry.getName());
        } else if (file != null && !file.isDirectory()) { //regular file, not a directory
            return FileResponder.getResponse(file);
        } else if (color != null && !color.equals("0,0,0,0")) {
            BufferedImage img;
            String[] rgb = color.split(",");
            img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setBackground(new java.awt.Color(0, 0, 0, 0));
            g.setColor(new java.awt.Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]), Integer.parseInt(rgb[3])));
            g.fillOval(0, 0, 20, 20);
            is = toInputStream(img);
        } else {
            is = toInputStream(BLANK_IMAGE);
        }
        return newFixedLengthResponse(Status.OK, mimeType, is, (long) is.available());
    }

    /**
     * Converts a BufferedImage to an InputStream
     *
     * @param image the image to convert
     * @return the InputStream
     * @throws IOException if this process fails
     */
    private InputStream toInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * Gets the file that matches the provided String
     *
     * @param fileStr find the file that matches this
     * @return the file, if found
     */
    public File getFile(String fileStr) {
        if (fileStr == null) {
            return null;
        }

        //remove start and end single quotes if they exist
        if (fileStr.startsWith("'") && fileStr.endsWith("'") && fileStr.length() > 1) {
            fileStr = fileStr.substring(1, fileStr.length() - 1);
        }

        return ResourceServlet.getFile(fileStr);
    }

}
