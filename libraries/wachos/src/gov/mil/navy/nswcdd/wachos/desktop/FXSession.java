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

import com.sun.javafx.webkit.WebConsoleListener;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * FXSession is a desktop-based WACHOS session for use in Swing applications
 * (using JavaFX's webview)
 */
public class FXSession extends WSession {

    /**
     * the content of the FXPanel, which will house our WACHOS layout
     */
    WebView webview;
    /**
     * Code that must be executed after the webview has been created
     */
    private StringBuilder toExec = new StringBuilder();

    /**
     * Constructor; private, because developers should call the "createBrowser"
     * method in order to create the session
     */
    private FXSession() {
        super(DesktopBuilder.createDesktopHttpSession());
    }

    /**
     * Initializes the session
     *
     * @param layout the master layout of the session
     * @param webview the thing that displays the session and accepts execution
     * of JavaScript
     */
    public void init(Layout layout, WebView webview) {
        this.webview = webview;
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
        try {
            Platform.runLater(() -> webview.getEngine().executeScript(javascript));
        } catch (Exception e) {
        }
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
     * Creates a JFXPanel that contains the WACHOS layout, which is created by
     * layoutMaker
     *
     * @param server serves the webapp once created
     * @param layoutMaker the thing that is to be served
     * @return the JFXPanel
     */
    public static JComponent createBrowser(NanoServer server, LayoutMaker layoutMaker) {
        JFXPanel jfxPanel = new JFXPanel();
        FXSession session = new FXSession();

        // Set up JavaFX content using Platform.runLater
        Platform.runLater(() -> {
            // Create WebView and set its size to fill available space
            Layout layout = DesktopBuilder.createLayout(server, layoutMaker, session);
            WebView webview = new WebView();
            webview.getEngine().load("http://localhost:" + server.myPort + "/wachos" + layout.getId());
            WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
                DesktopBuilder.receiveMessage(layout, message, false);
            });
            session.init(layout, webview); //init the session with the newly defined browser module
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(webview);
            Scene scene = new Scene(stackPane, 800, 600);
            jfxPanel.setScene(scene);
        });

        return jfxPanel;
    }
}
