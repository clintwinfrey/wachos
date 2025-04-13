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

import gov.mil.navy.nswcdd.wachos.components.Component;
import gov.mil.navy.nswcdd.wachos.components.Tabs;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.desktop.DesktopBuilder;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.omnifaces.cdi.PushContext;

/**
 * WTools provides a set of convenience methods for common things to be done in
 * a WACHOS application
 */
public class WTools {

    /**
     * the user cookies, for desktop mode
     */
    private static final Properties COOKIES = getCookies();

    /**
     * @return the user cookies, for desktop mode
     */
    private static Properties getCookies() {
        if (COOKIES != null) {
            return COOKIES;
        }
        if (DesktopBuilder.MODE == DesktopBuilder.Mode.ANDROID) {
            return new Properties(); //cookies won't work in android yet :(
        }
        File cookies = new File(System.getProperty("user.name") + "_cookies.txt");
        if (!cookies.exists()) {
            try {
                cookies.createNewFile();
            } catch (IOException e) {
            }
        }
        try (InputStream input = new FileInputStream(cookies.getName())) {
            Properties prop = new Properties();
            prop.load(input); //load a properties file
            input.close();
            return prop;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return a flag indicating if this application is being run in desktop
     * mode (not web mode)
     */
    public static boolean isDesktopMode() {
        return DesktopBuilder.MODE != DesktopBuilder.Mode.WEB;
    }

    /**
     * Sets the cookie name/value pairing for this user. Cookie sizes are
     * limited to 4KB, and only so many cookies are allowed.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     */
    public static void setCookie(String name, String value) {
        if (isDesktopMode()) {
            getCookies().setProperty(name, value);
            try (FileOutputStream output = new FileOutputStream(System.getProperty("user.name") + "_cookies.txt")) {
                COOKIES.store(output, null);
            } catch (Exception e) {
            }
            return;
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("maxAge", 31536000);
        properties.put("path", "/");
        try {
            FacesContext.getCurrentInstance().getExternalContext().addResponseCookie(getApplicationName() + name, URLEncoder.encode(value, "UTF-8"), properties);
        } catch (UnsupportedEncodingException e) {
        }
    }

    /**
     * Retrieves a cookie value for the given cookie name
     *
     * @param name the name of the cookie
     * @return the value of the cookie
     */
    public static String getCookie(String name) {
        if (isDesktopMode()) {
            String value = getCookies().getProperty(name);
            return value == null ? "" : value;
        }
        try {
            Cookie cookie = (Cookie) FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap().get(getApplicationName() + name);
            return cookie == null ? "" : URLDecoder.decode(cookie.getValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * Retrieves a cookie value for the given cookie name, or returns the
     * default value if a cookie with the name does not exist
     *
     * @param name the name of the cookie
     * @param defaultIfMissing the value to return if a cookie is not found
     * @return the value of the cookie, or default value if the cookie doesn't
     * exist
     */
    public static String getCookie(String name, String defaultIfMissing) {
        String ret = getCookie(name);
        return ret.equals("") ? defaultIfMissing : ret;
    }

    /**
     * @return the application title
     */
    public static String getApplicationName() {
        return FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath().replace("/", "");
    }

    /**
     * Executes the provided JavaScript, using the web-based PushContext
     *
     * @param pushContext allows for socket communication
     * @param javascript the JavaScript to execute
     */
    public static void exec(PushContext pushContext, String javascript) {
        if (javascript.length() == 0) {
            return; //nothing to execute anyway
        }
        if (pushContext != null) {
            pushContext.send(javascript); //preferred method is to execute via socket, which doesn't need current user interaction
        } else {
            FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add(javascript); //execute using JSF
        }
    }

    /**
     * Executes the provided JavaScript, after a specified number of
     * milliseconds has passed
     *
     * @param pushContext allows for socket communication
     * @param javascript the JavaScript to execute
     * @param milliDelay the number of milliseconds to wait before execution
     */
    public static void exec(PushContext pushContext, String javascript, long milliDelay) {
        new Thread() {
            @Override
            public void run() {
                WTools.sleep(milliDelay);
                exec(pushContext, javascript);
            }
        }.start();
    }

    /**
     * @return the application URL
     */
    public static String getUrl() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getRequestURL().toString();
    }

    /**
     * Converts a hexadecimal String to a color
     *
     * @param color the hex string to convert
     * @return a color that matches the provided hex string
     */
    public static Color fromHex(String color) {
        return color == null ? null : Color.decode(color);
    }

    /**
     * Converts a Color to a hexadecimal String
     *
     * @param color the Color to represent
     * @return a hexadecimal String representation
     */
    public static String toHex(Color color) {
        return color == null ? "" : "#" + String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds, subject to the
     * precision and accuracy of system timers and schedulers. The thread does
     * not lose ownership of any monitors.
     *
     * @param millis the length of time to sleep in milliseconds
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //this won't happen
        }
    }

    /**
     * Initializes tooltips for the provided component
     *
     * @param component contains tooltips to initialize
     * @param session the user session
     */
    public static void initToolTips(Component component, WSession session) {
        initToolTips(Arrays.asList(component), session);
    }

    /**
     * Initializes tooltips for the provided components
     *
     * @param components each component contains tooltips to initialize
     * @param session the user session
     */
    public static void initToolTips(List<Component> components, WSession session) {
        StringBuilder script = new StringBuilder();
        for (Component component : components) {
            createToolTipsScript(component, script);
        }
        session.exec(script.toString());
    }

    /**
     * Creates JavaScript that adds a tooltip
     *
     * @param component the component that potentially needs a tooltip
     * @param script the big script that this is being appended to
     */
    public static void createToolTipsScript(Component component, StringBuilder script) {
        String tooltip = component.getToolTip();
        if (!tooltip.equals("")) {
            if (component instanceof Layout && ((Layout) component).borderTitle != null) {
                script.append("$jq('#" + ((Layout) component).borderTitle.getId() + "').qtip({ content: \"" + tooltip.replace("\"", "\\\"").replace("\n", "<br/>") + "\", show: { delay: 1000 }, style: { widget: true,  def: true }});\n");
            } else if (!(component instanceof Layout)) {
                script.append("$jq('#" + component.getId() + "').qtip({ content: \"" + tooltip.replace("\"", "\\\"").replace("\n", "<br/>") + "\", show: { delay: 1000 }, style: { widget: true,  def: true }});\n");
            }
        }
        if (component instanceof Tabs) {
            for (Tabs.Tab tab : ((Tabs) component).tabs) {
                createToolTipsScript(tab.label, script);
                for (Component c : tab.layout.getComponents()) {
                    createToolTipsScript(c, script);
                }
            }
        } else if (component instanceof Layout) {
            if (((Layout) component).borderTitle != null) {
                createToolTipsScript(((Layout) component).borderTitle, script);
            }
            for (Component c : ((Layout<?>) component).getComponents()) {
                createToolTipsScript(c, script);
            }
        }
    }

    /**
     * Allows the user to download a file with the given name and content
     *
     * @param session the user's session
     * @param fileName the name of the file to download
     * @param fileContent the content of the file to download
     */
    public static void downloadFile(WSession session, String fileName, String fileContent) {
        WTools.exec(session.pushContext, "tfDownloadFileName = '" + fileName + "';\n"
                + "tfDownloadContent = \"" + fileContent.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "\\n").replace("\n", "\\n").replace("\r", "\\r") + "\";\n"
                + "tfDownload();");
    }

    /**
     * Sanitizes the text to prevent cross-site scripting
     *
     * @param toSanitize the text to be sanitized
     * @return the text that has been sanitized
     */
    public static String sanitize(String toSanitize) {
        return toSanitize.replace("&", "&#38;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;").replace("/", "&sol;");
    }

    /**
     * Desanitizes the text in the event that certain characters are needed for
     * displaying things properly
     *
     * @param toDesanitize the text to desanitize
     * @return the text that has been desanitized
     */
    public static String desanitize(String toDesanitize) {
        return toDesanitize.replace("&#38;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&sol;", "/");
    }

}
