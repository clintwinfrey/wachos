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

import gov.mil.navy.nswcdd.wachos.components.MenuBar;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.desktop.JcefSession;
import gov.mil.navy.nswcdd.wachos.desktop.responder.MapTileResponder;
import gov.mil.navy.nswcdd.wachos.desktop.responder.Responder;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.omnifaces.cdi.PushContext;

/**
 * WSession provides data specific to the user
 */
public class WSession {

    //application settings
    public String title = "Hello WACHOS"; //the title of your web application
    public String icon = "w.png"; //the icon of the web application
    public String theme = Theme.getOrDefault(Theme.CUPERTINO); //the jquery theme
    public String fontFamily = "Arial"; //the font family to use in the theme
    public String fontSize = "12"; //the font size to use in the theme

    /**
     * if this is a desktop application, then it must use a desktop session (not
     * a web one)
     */
    public static WSession DESKTOP_SESSION;

    /**
     * allows for socket communication
     */
    public final PushContext pushContext;
    /**
     * the "real" user session
     */
    public final HttpSession httpSession;
    /**
     * flag indicating if this session is valid
     */
    private boolean valid = true;
    /**
     * keeps track of whether the browser is still opened
     */
    private long heartbeat = 0l;

    /**
     * Constructor
     *
     * @param pushContext allows for socket communication
     */
    public WSession(PushContext pushContext) {
        this.pushContext = pushContext;
        this.httpSession = getHttpSession();
        httpSession.setAttribute("socketChannel", pushContext);
        httpSession.setAttribute("wsession", WSession.this);
    }

    /**
     * Constructor
     *
     * @param pushContext allows for socket communication
     * @param heartbeatSeconds how frequently (seconds) to check that the
     * application is alive
     */
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public WSession(PushContext pushContext, int heartbeatSeconds) {
        this(pushContext);
        WTimer heartbeatTimer = new WTimer(this, heartbeatSeconds * 1000);
        heartbeatTimer.setTask(() -> {
            Layout layout = getLayout();
            if (layout != null) {
                exec("changed" + layout.getId() + "({id: 'HEARTBEAT" + hashCode() + "', value: Date.now() + 'beat'});");
                long heartbeatAge = Math.abs(new Date().getTime() - heartbeat);
                if (heartbeatAge > (heartbeatSeconds * 2000)) {
                    heartbeatTimer.stop();
                    invalidate();
                }
            }
        });

        new Thread() {
            @Override
            public void run() {
                WTools.sleep(Math.min(10, heartbeatSeconds) * 1000);
                heartbeat();
                heartbeatTimer.start();
            }
        }.start();
    }

    /**
     * Constructor for JCEF desktop mode
     *
     * @param httpSession the user-defined httpSession, which is really just an
     * implementation of the abstract class
     */
    public WSession(HttpSession httpSession) {
        this.pushContext = null;
        this.httpSession = httpSession;
        DESKTOP_SESSION = this; //if it's not a WSession but instead extends WSession, then this must be a destkop session
    }

    /**
     * Updates how recently the heart has beaten
     */
    public void heartbeat() {
        this.heartbeat = new Date().getTime();
    }

    /**
     * @return the "real" user session
     */
    private static HttpSession getHttpSession() {
        if (DESKTOP_SESSION != null) {
            return DESKTOP_SESSION.httpSession;
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return null;
        }
        return ((HttpSession) fc.getExternalContext().getSession(false));
    }

    /**
     * @return the user's WSession
     */
    public static final WSession getSession() {
        if (DESKTOP_SESSION != null) {
            return DESKTOP_SESSION;
        }
        try {
            return (WSession) getHttpSession().getAttribute("wsession");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Adds an action to perform when this session is closed
     *
     * @param closeListener do this when closing the session
     */
    public void onClose(CloseListener closeListener) {
        getCloseListeners().add(closeListener);
    }

    /**
     * Closes this session by calling CloseListeners; removing properties and
     * listeners
     */
    public void invalidate() {
        if (!valid) {
            return;
        }
        for (CloseListener listener : getCloseListeners()) {
            listener.close();
        }
        httpSession.removeAttribute("wProperties" + hashCode());
        httpSession.removeAttribute("wPropertyListeners" + hashCode());
        httpSession.removeAttribute("wCloseListeners" + hashCode());
        this.valid = false;
    }

    /**
     * @return flag indicating if this session is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the 'project' variable, e.g. an OsmProject if you are using OSM
     *
     * @param project the project object
     */
    public void setProject(Object project) {
        setProperty("w.osmproject", project);
    }

    /**
     * Provides the 'project' variable, e.g. an OsmProject if you are using OSM
     *
     * @param <T> the inferred variable type
     * @return the project object
     */
    public <T> T getProject() {
        return getProperty("w.osmproject");
    }

    /**
     * Allows adding of a Responder to the NanoServer
     *
     * @param responder the Responder to add for URL request management
     */
    public void addResponder(Responder responder) {
        if (this instanceof JcefSession) {
            ((JcefSession) this).getServer().add(responder);
        }
    }

    /**
     * @return the user's working directory
     */
    public String getWorkingDirectory() {
        return System.getProperties().getProperty("working.directory");
    }

    /**
     * Sets the user's working director
     *
     * @param workingDir the user's working directory
     */
    public void setWorkingDirectory(String workingDir) {
        try {
            System.getProperties().setProperty("working.directory", new File(workingDir).getCanonicalPath() + "\\");
        } catch (IOException e) {
            System.getProperties().setProperty("working.directory", new File(workingDir).getAbsolutePath() + "\\");
        }
    }

    /**
     * Any files under e.g. "http://localhost:8080/resource/path/to/file" will
     * really map to these directories
     *
     * @param resourceDirs the directories that can be accessed as resources
     */
    public void addResourceDirectories(String... resourceDirs) {
        for (String resourceDir : resourceDirs) {
            ResourceServlet.addResourceFolder(getWorkingDirectory(), resourceDir);
        }
    }

    /**
     * Initializes the retrieval of map tiles
     *
     * @param cache the local cache directory
     * @param url the url of where tiles are downloaded, excluding the pattern
     * @param pattern e.g. {z}/{y}/{x}.png
     * @param online if not online, then don't bother to download files
     * externally
     */
    public void setMapTiles(String cache, String url, String pattern, boolean online) {
        addResponder(new MapTileResponder());
        MapTileServlet.init(getWorkingDirectory(), cache, url, pattern, online);
    }

    /**
     * @return the layout of this user's session
     */
    public Layout getLayout() {
        return getProperty("tf.layout");
    }

    /**
     * Sets the layout of this user's session
     *
     * @param layout the main application layout
     */
    public void setLayout(Layout layout) {
        setProperty("tf.layout", layout);
    }

    /**
     * @return the application's MenuBar
     */
    public MenuBar getMenuBar() {
        return getProperty("tf.menubar");
    }

    /**
     * Sets the application's MenuBar
     *
     * @param menubar the application's MenuBar
     */
    public void setMenuBar(MenuBar menubar) {
        setProperty("tf.menubar", menubar);
    }

    /**
     * @return the thing that allows socket communication
     */
    public PushContext getPushContext() {
        return pushContext;
    }

    /**
     * Gets a property that matches the given ID
     *
     * @param <T> the class type to return
     * @param id the property identifier
     * @return the property value for the given ID
     */
    public <T> T getProperty(String id) {
        Object item = getProperties().get(id);
        return item == null ? null : (T) item;
    }

    /**
     * Sets the property in the session
     *
     * @param id the property identifier
     * @param obj the property value
     */
    public void setProperty(String id, Object obj) {
        getProperties().put(id, obj);
        propertyChanged(id);
    }

    /**
     * Removes the property for the given ID
     *
     * @param id the identifier of the property
     */
    public void removeProperty(String id) {
        getProperties().remove(id);
        propertyChanged(id);
    }

    /**
     * When a property value changes, the property listeners are notified
     *
     * @param id the ID of the property that has changed
     */
    private void propertyChanged(String id) {
        List<PropertyListener> propertyListeners = getPropertyListenersMap().get(id);
        if (propertyListeners == null) {
            return;
        }
        for (PropertyListener listener : propertyListeners) {
            listener.propertyChanged();
        }
    }

    /**
     * Adds a listener for when a property changes with the given ID
     *
     * @param propertyId the ID of the property that has changed
     * @param listener listens for when the property value changes
     */
    public void addPropertyListener(String propertyId, PropertyListener listener) {
        List<PropertyListener> propertyListeners = getPropertyListenersMap().get(propertyId);
        if (propertyListeners == null) {
            propertyListeners = new ArrayList<>();
            getPropertyListenersMap().put(propertyId, propertyListeners);
        }
        propertyListeners.add(listener);
    }

    /**
     * Removes a property listener from the session
     *
     * @param listener the thing to remove, since it is no longer listening
     */
    public void removePropertyListener(PropertyListener listener) {
        for (Map.Entry<String, List<PropertyListener>> entry : getPropertyListenersMap().entrySet()) {
            if (entry.getValue().contains(listener)) {
                entry.getValue().remove(listener);
            }
        }
    }

    /**
     * @return the name of the application
     */
    public String getApplicationName() {
        return FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath().replace("/", "");
    }

    /**
     * Set the page title
     *
     * @param pageTitle the new title of the page
     */
    public void setTitle(String pageTitle) {
        exec("$(document).prop('title', '" + pageTitle + "');");
    }

    /**
     * Execute the provided JavaScript code
     *
     * @param javascript the JavaScript to execute
     */
    public void exec(String javascript) {
        WTools.exec(pushContext, javascript);
    }

    /**
     * Execute the provided JavaScript code, with a delay
     *
     * @param javascript the JavaScript to execute
     * @param milliDelay number of milliseconds in which to execute the
     * JavaScript
     */
    public void exec(String javascript, long milliDelay) {
        WTools.exec(pushContext, javascript, milliDelay);
    }

    /**
     * Tells the user of a success that has occurred
     *
     * @param title the title of the toast
     * @param message the message of the toast
     */
    public void postSuccess(String title, String message) {
        exec("toastr.success(\"" + message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "<br/>") + "\", \"" + title + "\");");
    }

    /**
     * Tells the user of information
     *
     * @param title the title of the toast
     * @param message the message of the toast
     */
    public void postInfo(String title, String message) {
        exec("toastr.info(\"" + message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "<br/>") + "\", \"" + title + "\");");
    }

    /**
     * Tells the user of a warning that has occurred
     *
     * @param title the title of the toast
     * @param message the message of the toast
     */
    public void postWarning(String title, String message) {
        exec("toastr.warning(\"" + message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "<br/>") + "\", \"" + title + "\");");
    }

    /**
     * Tells the user of an error that has occurred
     *
     * @param title the title of the toast
     * @param message the message of the toast
     */
    public void postError(String title, String message) {
        exec("toastr.error(\"" + message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "<br/>") + "\", \"" + title + "\");");
    }

    /**
     * The user is uploading a file
     *
     * @param uploadListener notify this listener when the file is uploaded
     * @param fileExtensions file types that can be selected
     */
    public void loadFile(UploadListener uploadListener, String... fileExtensions) {
        if (WTools.isDesktopMode()) {
            new Thread() {
                @Override
                public void run() {
                    JFileChooser chooser = new JFileChooser();
                    for (int i = 0; i < fileExtensions.length; i++) {
                        fileExtensions[i] = fileExtensions[i].replace(".", ""); //JFileChooser expects "txt", not ".txt"
                    }
                    chooser.setFileFilter(new FileNameExtensionFilter("", fileExtensions));
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        try {
                            uploadListener.fileUploaded(file.getName(), Files.probeContentType(file.toPath()), new FileInputStream(file.getCanonicalFile()));
                        } catch (IOException e) {
                        }
                    }
                }
            }.start();
        } else {
            this.setProperty("wUploadListener", uploadListener);
            String fileExtensionsStr = "";
            for (int i = 0; i < fileExtensions.length; i++) {//String fileExtension : fileExtensions) {
                fileExtensionsStr += fileExtensions[i] + (i == fileExtensions.length - 1 ? "" : ",");
            }
            exec("$('#' + $('.fileLoader').attr('id').replaceAll(\":\", \"\\\\:\")).attr('accept', '" + fileExtensionsStr + "');\n"
                    + "$('#' + $('.fileLoader').attr('id').replaceAll(\":\", \"\\\\:\")).trigger('click');");
        }
    }

    /**
     * In web mode, this is called by SessionView to notify the listener that a
     * file was uploaded. In desktop mode, this method is not used.
     *
     * @param fileName the name of the file that was uploaded
     * @param contentType the type of file that was uploaded
     * @param inputStream the content of the uploaded file
     */
    public void fileLoaded(String fileName, String contentType, InputStream inputStream) {
        ((UploadListener) getProperty("wUploadListener")).fileUploaded(fileName, contentType, inputStream);
    }

    /**
     * Provides the properties of this WSession
     *
     * @return the properties map
     */
    private Map<String, Object> getProperties() {
        Object properties = httpSession.getAttribute("wProperties" + hashCode());
        if (properties == null) {
            properties = new HashMap<String, Object>();
            httpSession.setAttribute("wProperties" + hashCode(), properties);
        }
        return (Map<String, Object>) properties;
    }

    /**
     * @return the map of property listeners (property ID, List of listeners)
     */
    private Map<String, List<PropertyListener>> getPropertyListenersMap() {
        Object propertyListeners = httpSession.getAttribute("wPropertyListeners" + hashCode());
        if (propertyListeners == null) {
            propertyListeners = new HashMap<String, List<PropertyListener>>();
            httpSession.setAttribute("wPropertyListeners" + hashCode(), propertyListeners);
        }
        return (Map<String, List<PropertyListener>>) propertyListeners;
    }

    /**
     * @return the list of CloseListeners that need to be closed when the
     * session ends
     */
    private List<CloseListener> getCloseListeners() {
        Object closeListeners = httpSession.getAttribute("wCloseListeners" + hashCode());
        if (closeListeners == null) {
            closeListeners = new ArrayList<CloseListener>();
            httpSession.setAttribute("wCloseListeners" + hashCode(), closeListeners);
        }
        return (List<CloseListener>) closeListeners;
    }

    /**
     * UploadListener listens for a file upload
     */
    public static interface UploadListener {

        /**
         * In web mode, notifies this listener that a file was uploaded. In
         * desktop mode, this method is not used.
         *
         * @param fileName the name of the file that was uploaded
         * @param contentType the type of file that was uploaded
         * @param inputStream the content of the uploaded file
         */
        public void fileUploaded(String fileName, String contentType, InputStream inputStream);
    }

    /**
     * PropertyListener is notified when a session property has changed
     */
    public static interface PropertyListener {

        /**
         * Called when the property being listened to has changed in value
         */
        public void propertyChanged();
    }

    /**
     * CloseListener is to be called when the application is closing
     */
    public static interface CloseListener {

        /**
         * Called when the application is closing
         */
        public void close();
    }

}
