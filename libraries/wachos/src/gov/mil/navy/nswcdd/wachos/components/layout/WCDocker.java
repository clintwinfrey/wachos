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
package gov.mil.navy.nswcdd.wachos.components.layout;

import gov.mil.navy.nswcdd.wachos.components.ComponentListeners;
import gov.mil.navy.nswcdd.wachos.components.MenuItem;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import java.util.ArrayList;
import java.util.List;

/**
 * wcDocker (Web Cabin Docker) is a powerful window layout system with a
 * responsive and completely interactive design. Move, remove, create, and
 * duplicate panel windows at any time! Organize how you wish!
 */
public class WCDocker extends Layout<WCDocker> {

    /**
     * the titles of the apps that are in this Docker
     */
    private final List<String> appTitles;
    /**
     * the apps that are in this Docker
     */
    private final List<Layout> apps;
    /**
     * if true, then 'loadApps' won't do anything since it's already been called
     */
    private boolean appsLoaded = false;
    /**
     * these are loaded into the docker; once 'loadApps' is called, the actual
     * content will be there. Note that WebGL apps don't go inside of containers
     */
    private final List<VBox> appContainers = new ArrayList<>();
    /**
     * MenuItems for the applications
     */
    private final List<MenuItem> appMenuItems = new ArrayList<>();
    /**
     * contains MenuItems to open apps that are closed
     */
    private final MenuItem dockerMenu;
    /**
     * when creating a WCDocker, the menubar needs to be accounted for; it's
     * assumed that the menubar and docker are the only items to load, and this
     * offsets the size of the docker accordingly
     */
    private final int menuBarHeight;
    /**
     * the default layout of the apps
     */
    private String defaultLayout = "";
    /**
     * if true, this will prevent events from being thrown
     */
    private boolean settingLayout = false;
    /**
     * contains the event that happens when the value changes
     */
    public final ComponentListeners layoutChangedListeners = new ComponentListeners();

    /**
     * Constructor
     *
     * @param appTitles the titles of the apps that are in this Docker
     * @param apps the apps that are in this Docker
     * @param menuBarHeight when creating a WCDocker, the menubar needs to be
     * accounted for; it's assumed that the menubar and docker are the only
     * items to load, and this offsets the size of the docker accordingly
     * @param defaultLayout the default layout of the apps
     */
    public WCDocker(List<String> appTitles, List<Layout> apps, int menuBarHeight, String defaultLayout) {
        this.appTitles = appTitles;
        this.apps = apps;
        for (Layout app : apps) {
            VBox appContainer = new VBox().setWidth("100%").setHeight("100%");
            appContainers.add(appContainer);
            if (app.isWebGL()) {
                components.add(app); //will be added directly
            } else {
                components.add(appContainer); //will be added in a container
            }
        }
        this.menuBarHeight = menuBarHeight;
        this.defaultLayout = WTools.getCookie("wcdockerLayout");
        if (this.defaultLayout.equals("")) {
            this.defaultLayout = defaultLayout; //forced layout if one wasn't found
        }

        //make MenuItems for the apps so the user can open them
        List<String> openedPanels = WCDocker.this.getOpenedPanels(); //figure out which panels are already opened, according to the layout
        dockerMenu = new MenuItem("Docker");
        for (String appTitle : appTitles) {
            MenuItem mi = new MenuItem(appTitle, action -> {
                String parent = openedPanels.isEmpty() ? "null" : "myDocker.findPanels(\"" + openedPanels.get(openedPanels.size() - 1) + "\")[0]";
                exec("myDocker.addPanel(\"" + appTitle + "\", wcDocker.DOCK.RIGHT, " + parent + ");\n");
                for (int i = 0; i < dockerMenu.getChildCount(); i++) {
                    if (dockerMenu.getChild(i).getText().equals(appTitle)) {
                        dockerMenu.getChild(i).setEnabled(false);
                    }
                }
            });
            mi.setEnabled(!openedPanels.contains(appTitle)); //if panel is opened, disable its menuitem
            dockerMenu.addChild(mi);
            appMenuItems.add(mi);
        }
    }

    /**
     * @return the titles of the apps in this docker
     */
    public List<String> getTitles() {
        return new ArrayList<>(appTitles);
    }

    /**
     * Provides the app with the given title, regardless of whether loadApps has
     * been called
     *
     * @param appTitle the title of the app to get
     * @return this
     */
    public Layout getApp(String appTitle) {
        return apps.get(appTitles.indexOf(appTitle));
    }

    /**
     * This is a security feature that only loads the apps when called (i.e.
     * call this when a user logs in). WebGL apps are already loaded by default,
     * so this doesn't prevent security concerns in those cases. Be careful not
     * to load anything in a WebGL app that you don't want your user to see
     * before logging in.
     *
     * @return this
     */
    public WCDocker loadApps() {
        if (appsLoaded) {
            return this;
        }
        appsLoaded = true;
        for (int i = 0; i < appTitles.size(); i++) {
            if (!apps.get(i).isWebGL()) { //if it's webgl, it would have been added automatically
                appContainers.get(i).add(apps.get(i));
            }
        }
        return this;
    }

    /**
     * @return the panels that are currently opened
     */
    private List<String> getOpenedPanels() {
        List<String> openedPanels = new ArrayList<>();
        for (String appTitle : appTitles) {
            if (WTools.desanitize(defaultLayout).contains("\"panelType\":\"" + appTitle + "\"")) {
                openedPanels.add(appTitle);
            }
        }
        return openedPanels;
    }

    /**
     * Sets the layout of the apps
     *
     * @param layout the layout to use for displaying the apps
     */
    public void setLayout(String layout) {
        if (settingLayout || layout == null || layout.equals(defaultLayout)) {
            return;
        }
        settingLayout = true;
        this.defaultLayout = layout;
        String cmd = "";
        for (String appTitle : appTitles) {
            cmd += "myDocker.removePanel(myDocker.findPanels(\"" + appTitle + "\")[0], false);\n";
        }
        exec(cmd + "myDocker.restore('" + WTools.desanitize(layout) + "');");

        //enable/disable the menu items
        List<String> openedPanels = getOpenedPanels();
        for (MenuItem item : dockerMenu.getChildren()) {
            item.setEnabled(!openedPanels.contains(item.getText()));
        }
        settingLayout = false;
    }

    /**
     * You can have a lot of apps loaded, but you may only want to see certain
     * ones for a given situation. For instance, if the user interacts a certain
     * way then it may trigger a change in available apps and how they should be
     * laid out
     *
     * @param titles the titles of the apps to view
     * @param layout the layout of the apps to view
     */
    public void setView(List<String> titles, String layout) {
        for (int i = dockerMenu.getChildCount() - 1; i >= 0; i--) {
            dockerMenu.removeChild(dockerMenu.getChild(i));
        }
        for (int i = 0; i < appMenuItems.size(); i++) {
            if (titles.contains(appMenuItems.get(i).getText())) {
                dockerMenu.addChild(appMenuItems.get(i));
            }
        }
        setLayout(layout);
    }

    /**
     * @return the HTML used to represent this WCDocker
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        StringBuilder panelSettings = new StringBuilder();
        for (int i = 0; i < appTitles.size(); i++) {
            String title = appTitles.get(i);
            String varTitle = title.replaceAll("[^a-zA-Z0-9]", "");
            if (apps.get(i).isWebGL()) {
                sb.append("<div id=\"" + varTitle + "Div\" class=\"ui-widget-content\" style=\"height:calc(100% - 2px)\">\n" + apps.get(i).toHtml() + "</div>\n");
            } else {
                sb.append("<div id=\"" + varTitle + "Div\" class=\"ui-widget-content\" style=\"height: 100%; width: 100%; overflow: hidden\">\n" + appContainers.get(i).toHtml().replaceFirst("; width: 100%; ", "; width: 100%; overflow: auto; ") + "</div>\n");
            }
            panelSettings.append("//" + title + " panel\n"
                    + "var " + varTitle + "instance = 0;\n"
                    + "myDocker.registerPanelType('" + title + "', {\n"
                    + "    isPersistent: false,\n"
                    + "    onCreate: function (myPanel) {\n"
                    + "        " + varTitle + "instance++;\n"
                    + "        var $mydiv = $jq('<div id=\"" + varTitle + "instanceDiv\" style=\"position:absolute;top:0px;left:0px;right:0px;bottom:0px;\"></div>');\n"
                    + "        myPanel.layout().addItem($mydiv, 0, 1);\n"
                    + "        myPanel.on(wcDocker.EVENT.INIT, function() {\n"
                    + "            $jq('#" + varTitle + "Div').appendTo(\"#" + varTitle + "instanceDiv\");\n"
                    + "            var height = $('#" + varTitle + "instanceDiv').height();\n"
                    + "            var width = $('#" + varTitle + "instanceDiv').width();\n"
                    + "            appResized('" + title + "', width, height);\n"
                    + "        });\n"
                    + "        myPanel.minSize(40, 40);\n"
                    + "        myPanel.closeable(true);\n"
                    + "        myPanel.on(wcDocker.EVENT.BUTTON, function(button) {\n"
                    + "            if (button.name === 'print') {\n"
                    + "            } else {\n"
                    + "                executeRemote([{name: 'commandName', value: button.name}, {name: 'panelName', value: '" + title + "'}]);\n"
                    + "            }\n"
                    + "        });\n"
                    + "        myPanel.on(wcDocker.EVENT.RESIZE_ENDED, function () {\n"
                    + "            var height = $('#" + varTitle + "instanceDiv').height();\n"
                    + "            var width = $('#" + varTitle + "instanceDiv').width();\n"
                    + "            appResized('" + title + "', width, height);\n"
                    + "            dockingLayoutChanged();\n"
                    + "        });\n"
                    + "        myPanel.on(wcDocker.EVENT.CLOSED, function () {\n"
                    + "            $jq('#" + varTitle + "Div').appendTo(\"#invisible\");\n"
                    + "            dockingLayoutChanged();\n"
                    + "            dockingItemClosed('" + title + "');\n"
                    + "        });\n"
                    + "        myPanel.on(wcDocker.EVENT.PERSISTENT_CLOSED, function () {\n"
                    + "            dockingLayoutChanged();\n"
                    + "            dockingItemClosed('" + title + "');\n"
                    + "        });\n"
                    + "        myPanel.on(wcDocker.EVENT.MOVE_ENDED, function () {\n"
                    + "            dockingLayoutChanged();\n"
                    + "        });\n"
                    + "        myPanel.on(wcDocker.EVENT.GAIN_FOCUS, function () {\n"
                    + "            dockingLayoutChanged();\n"
                    + "        });\n"
                    + "    }\n"
                    + "});\n");
        }

        return "<div id=\"dockParent\"></div>"
                + "<span><div id=\"invisible\" style=\"display:none\">\n"
                + sb.toString()
                + "</div>\n"
                + "<div id=\"dockerContainer\" class=\"dockerContainer\"/>\n"
                + "<script>\n"
                + "    var layout;\n"
                + "    var myDocker;\n"
                + "    $jq(document).ready(function () {\n"
                + "        myDocker = new wcDocker('.dockerContainer', {\n"
                + "            allowDrawers: true,\n"
                + "            responseRate: 10,\n"
                + "            allowContextMenu: false,\n"
                + "            themePath: 'wcdocker',\n"
                + "            theme: 'defaultTheme.min'\n"
                + "        });\n"
                + "        if (myDocker) {\n"
                + panelSettings.toString()
                + "            myDocker.on(wcDocker.EVENT.LOADED, function () {\n"
                + "                myDocker.finishLoading(500);\n"
                + "                $jq('#dockerContainer .wcDocker').attr(\"id\", \"wcdocker\");\n"
                + "                $(window).bind('resize', function () {\n"
                + "                    resizeDocker();\n"
                + "                });\n"
                + "                $jq('#dockerContainer').appendTo(\"#dockParent\");\n"
                + "                resizeDocker();\n"
                + "                myDocker.restore('" + WTools.desanitize(defaultLayout) + "');\n"
                + "            });\n"
                + "        }\n"
                + "    });\n"
                + "    function resizeDocker() {\n"
                + "        document.getElementById('wcdocker').style.height = ($(window).height() - " + menuBarHeight + ") + 'px';\n"
                + "        document.getElementById('dockParent').style.height = ($(window).height() - " + menuBarHeight + ") + 'px';"
                + "        changed" + layoutId + "({id: '" + getId() + "', value: 'resizedDocker'})\n"
                + "    }\n"
                + "    function dockingLayoutChanged() {\n"
                + "        var update = myDocker.save();\n"
                + "        if (update !== layout) {\n"
                + "            layout = update;\n"
                + "            changed" + layoutId + "({id: '" + getId() + "', value: 'layoutChanged ' + myDocker.save() });\n" //change this to capture the value and store as a cookie
                + "        }\n"
                + "        $jq('.wcFrameTitle').addClass('ui-widget-header');\n"
                + "        $jq('.wcFrameTitle').css('border', '0px');\n"
                + "        $jq('.wcFrameButton').css('color', $jq('.ui-widget-header').css('color'));\n"
                + "        $jq('.wcPanelTab').addClass('ui-state-default');\n"
                + "        $jq('.wcTabTop').addClass('ui-state-default');\n"
                + "    }\n"
                + "\n"
                + "    function dockingItemClosed(appTitle) {\n"
                + "        changed" + layoutId + "({id: '" + getId() + "', value: 'itemClosed ' + appTitle });\n"
                + "    }\n"
                + "\n"
                + "    var sizeMap = new Map();\n"
                + "    var sizeUpdateScheduled = false;\n"
                + "\n"
                + "    function appResized(appTitle, width, height) {\n"
                + "        sizeMap.set(appTitle, width + \",\" + height);\n"
                + "        if (sizeUpdateScheduled === false) {\n"
                + "            sizeUpdateScheduled = true;\n"
                + "            setTimeout(function () {\n"
                + "                var resizeStr = '';\n"
                + "                for (var [key, value] of sizeMap.entries()) {\n"
                + "                    resizeStr = resizeStr + key + ';=;' + value + \";M;\";\n"
                + "                }\n"
                //+ "                resizeApps([{name: 'appSizes', value: resizeStr}]);\n"
                + "                sizeUpdateScheduled = false;\n"
                + "            }, 100);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "</script></span>\n";
    }

    /**
     * Programmatically opens the panel of the given title
     *
     * @param panelTitle the name of the app to open
     */
    public void openPanel(String panelTitle) {
        exec("myDocker.addPanel(\"" + panelTitle + "\", wcDocker.DOCK.RIGHT, null);");
    }

    /**
     * Fires an event on this component
     *
     * @param event the event data
     */
    @Override
    public void fireEvent(String event) {
        if (!isEnabled()) {
            return;
        }
        if (settingLayout) {
            return; //don't do anything
        }
        if (event.startsWith("itemClosed ")) {
            for (int i = 0; i < dockerMenu.getChildCount(); i++) {
                MenuItem child = dockerMenu.getChild(i);
                if (child.getText().equals(event.replaceFirst("itemClosed ", ""))) {
                    child.setEnabled(true);
                }
            }
        } else if (event.startsWith("layoutChanged ")) {
            String layout = WTools.sanitize(event.replace("layoutChanged ", ""));
            if (!layout.equals(defaultLayout)) {
                defaultLayout = layout;
                WTools.setCookie("wcdockerLayout", layout);
                layoutChangedListeners.update(layout);
            }
        }
    }

    /**
     * @return the MenuItem that contains MenuItems to open apps
     */
    public MenuItem getMenu() {
        return dockerMenu;
    }

}
