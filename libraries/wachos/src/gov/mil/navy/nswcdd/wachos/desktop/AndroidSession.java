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

import android.os.Handler;
import android.os.Looper;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.desktop.responder.ResourceResponder;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import gov.mil.navy.nswcdd.wachos.tools.WachosGui;
import java.io.IOException;
import nanohttpd.HTTPSession;
import nanohttpd.Method;
import nanohttpd.NanoHTTPD;
import nanohttpd.Status;

/**
 * JcefSession is a desktop-based WACHOS session for use in Swing applications
 * (using JCEF)
 */
public class AndroidSession extends WSession {

    /**
     * serves the page for the browser
     */
    private final NanoServer server;
    /**
     * creates a component for use in the desktop application
     */
    private WebView webview;
    /**
     * Code that must be executed after the webview has been created
     */
    private StringBuilder toExec = new StringBuilder();

    /**
     * Constructor; private, because developers should call the "createBrowser"
     * method in order to create the session
     *
     * @param server serves the page for the browser
     * @param webview displays the wachos layout
     */
    private AndroidSession(NanoServer server, WebView webview) {
        super(DesktopBuilder.createDesktopHttpSession());
        this.server = server;
        this.webview = webview;
    }

    /**
     * @return the NanoServer
     */
    public NanoServer getServer() {
        return server;
    }

    /**
     * Initializes the layout of the application
     *
     * @param layout the master application layout
     *
     */
    public void init(Layout layout) {
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
        if (webview == null) {
            toExec.append("\n").append(javascript);
            return;
        }
        // Executing JavaScript
        webview.evaluateJavascript(javascript, null);
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
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                webview.evaluateJavascript(javascript, null);
            }
        }, milliDelay);
    }

    /**
     * Creates new form SimpleGui
     *
     * @param wachosGui the WachosGui to deploy via webview
     * @param webview deploys the layout as a website
     * @return the session this creates
     */
    public static AndroidSession create(WachosGui wachosGui, WebView webview) {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        //webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        
        DesktopBuilder.MODE = DesktopBuilder.Mode.ANDROID; //set the mode

        //set up the server
        NanoServer server = new NanoServer();
        try {
            server.add(new ResourceResponder("resources", "META-INF/resources"));
            server.start();
        } catch (IOException e) {
        }

        // Create WebView and set its size to fill available space
        LayoutMaker layoutMaker = session -> {
            Layout webapp = wachosGui.create(session);
            webapp.setWidth("100%");
            return webapp;
        };

        AndroidSession session = new AndroidSession(server, webview);
        Layout layout = DesktopBuilder.createLayout(server, layoutMaker, session);
        session.init(layout);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                DesktopBuilder.receiveMessage(layout, message.message(), false);
                return true;
            }
        });
        webview.loadUrl("http://localhost:" + server.myPort + "/wachos" + layout.getId());

        return session;
    }

    /**
     * Stop the NanoServer
     */
    public void stop() {
        server.stop();
    }

}
