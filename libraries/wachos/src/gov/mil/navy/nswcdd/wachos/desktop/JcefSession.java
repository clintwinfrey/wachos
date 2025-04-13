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

import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import java.io.File;
import java.util.Date;
import javax.swing.SwingUtilities;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefDisplayHandlerAdapter;

/**
 * JcefSession is a desktop-based WACHOS session for use in Swing applications
 * (using JCEF)
 */
public class JcefSession extends WSession {

    /**
     * serves the page for the browser
     */
    private final NanoServer server;
    /**
     * creates a component for use in the desktop application
     */
    private CefBrowser cefBrowser;
    /**
     * Code that must be executed after the webview has been created
     */
    private StringBuilder toExec = new StringBuilder();

    /**
     * Constructor; private, because developers should call the "createBrowser"
     * method in order to create the session
     *
     * @param server serves the page for the browser
     */
    private JcefSession(NanoServer server) {
        super(DesktopBuilder.createDesktopHttpSession());
        this.server = server;
    }

    /**
     * @return  the NanoServer
     */
    public NanoServer getServer() {
        return server;
    }

    /**
     * Initializes the layout of the application
     *
     * @param layout the master application layout
     * @param cefBrowser the component for use in the desktop application
     */
    public void init(Layout layout, CefBrowser cefBrowser) {
        this.cefBrowser = cefBrowser;
        exec(toExec.toString());
        toExec = null;
        setLayout(layout);
        layout.init(layout.getId(), this);
    }

    /**
     * Execute the provided JavaScript code
     *
     * @param javascript the JavaScript to execute
     */
    @Override
    public void exec(String javascript) {
        if (cefBrowser == null) {
            toExec.append("\n").append(javascript);
            return;
        }
        cefBrowser.executeJavaScript(javascript, cefBrowser.getURL(), 1);
    }

    /**
     * Execute the provided JavaScript code, with a delay
     *
     * @param javascript the JavaScript to execute
     * @param milliDelay number of milliseconds in which to execute the
     * JavaScript
     */
    @Override
    public void exec(String javascript, long milliDelay) {
        new Thread() {
            @Override
            public void run() {
                WTools.sleep(milliDelay);
                SwingUtilities.invokeLater(() -> exec(javascript));
            }
        }.start();
    }

    /**
     * Creates a CefBrowser for the given WACHOS Component
     *
     * @param server serves the page for the browser
     * @param layoutMaker creates a Layout by passing in a session
     * @return the CefBrowser that's created
     */
    public static CefBrowser createBrowser(NanoServer server, LayoutMaker layoutMaker) {
        JcefSession session = new JcefSession(server);
        Layout layout = DesktopBuilder.createLayout(server, layoutMaker, session);
        String url = "http://localhost:" + server.myPort + "/wachos" + layout.getId();
        CefClient cefClient;

        //Configure CefSettings
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;
        settings.background_color = settings.new ColorType(100, 255, 242, 211);//Set the background color to a custom color

        //set the root cache folder, and also delete it on exit
        File rootCache = new File("root_cache/" + new Date().getTime());
        settings.root_cache_path = rootCache.getAbsolutePath();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteDirectory(rootCache)));

        //Create a new CefApp instance with the provided settings
        CefApp myApp;
        try {
            myApp = CefApp.getInstance(new String[]{"--disable-web-security"}, settings);
        } catch (IllegalStateException e) {
            myApp = CefApp.getInstance(); //If an instance already exists, retrieve it
        }
        System.out.println("CEF version: " + myApp.getVersion());

        cefClient = myApp.createClient(); //Create the CefClient
        CefMessageRouter msgRouter = CefMessageRouter.create(); //create and add a message router with handlers
        cefClient.addMessageRouter(msgRouter);

        // handle console messages
        cefClient.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                DesktopBuilder.receiveMessage(layout, message, CefSettings.LogSeverity.LOGSEVERITY_ERROR == level);
                return true;
            }
        });

        CefBrowser browser = cefClient.createBrowser(url, false, false); //make the CefBrowser instance with the initial URL
        session.init(layout, browser); //init the session with the newly defined browser
        return browser;
    }

    /**
     * Deletes the directory, recursively deleting files inside of the directory
     *
     * @param directory the directory to delete
     * @return true if deleted
     */
    private static boolean deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles(); //list all files and directories in the current directory
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); //recursively delete each file or subdirectory
                    }
                    file.delete();
                }
            }
        }
        return directory.delete(); //delete the directory or file
    }
}
