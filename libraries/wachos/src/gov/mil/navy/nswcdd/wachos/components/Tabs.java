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
package gov.mil.navy.nswcdd.wachos.components;

import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A component that lets the user switch between a group of components by
 * clicking on a tab with a given title; can also be used in a "tabless" mode
 * where the current tab is selected externally
 */
public class Tabs extends Component {

    /**
     * if true, tabs can be removed by clicking an x
     */
    private final boolean closable;
    /**
     * if true, the tabs will be shown at the top
     */
    private final boolean showTabs;
    /**
     * if true, has a dropdown arrow through which the user can select a tab
     */
    private final boolean showNavigation;
    /**
     * a container for the title to be shown, when showTabs is false
     */
    private final HBox titleContainer;
    /**
     * shows the index of the selected title, when showTabs is false
     */
    private Label titleCount = new Label("");
    /**
     * the currently selected tab index
     */
    private int selectedIndex = 0;
    /**
     * the Tabs that are contained in this Component
     */
    public final List<Tab> tabs = new ArrayList<>();
    /**
     * made this because it was getting used so frequently in this class
     */
    private final String evtStart = "changed" + layoutId + "({id: '" + getId() + "', value: '#";
    /**
     * listens for when a tab is saved
     */
    public final ComponentListeners saveListeners = new ComponentListeners();
    /**
     * listens for when the plus is hovered over
     */
    public final ComponentListeners addHoverListeners = new ComponentListeners();
    /**
     * listens for when a tab is added
     */
    public final ComponentListeners addListeners = new ComponentListeners();
    /**
     * listens for when tab order is changed
     */
    public final ComponentListeners orderListeners = new ComponentListeners();
    /**
     * listens for when a tab is removed
     */
    public final ComponentListeners removeListeners = new ComponentListeners();
    /**
     * listens for when the selected tab index changes
     */
    public final ComponentListeners indexListeners = new ComponentListeners();
    /**
     * if the user can add a Tab, these options are provided to the user
     */
    private final List<String> plusOptions = new ArrayList<>();
    /**
     * if the user can add a Tab, this is the menu that provides the options
     */
    public final ContextMenu plusMenu = new ContextMenu(new ArrayList<>(), index -> addListeners.update(plusOptions.isEmpty() ? "0" : index));
    /**
     * the number of tabs in this component
     */
    private int tabCount = -1;
    /**
     * a dropdown menu to navigate to different tabs
     */
    public final ContextMenu tabMenu = new ContextMenu(new ArrayList<>(), index -> selectTab(Integer.parseInt(index)));
    /**
     * the minimum width for the Tabs, as a css property string
     */
    private String minWidth = "";

    /**
     * NORMAL is simple tabs that can't be closed CLOSABLE is tabs that can be
     * sorted and closed CLOSABLE_UNTABBED allows closing and navigation through
     * a tabless GUI CLOSABLE_UNTABBED_NONAV does not have the navigation
     * buttons
     */
    public enum TabsType {
        /**
         * nothing special about these tabs
         */
        NORMAL,
        /**
         * the tabs are closable
         */
        CLOSABLE,
        /**
         * the tabs are closable and do not have tabs to click on
         */
        CLOSABLE_UNTABBED,
        /**
         * the tabs are closable, do not have tabs to click on, and do not have
         * navigation ability
         */
        CLOSABLE_UNTABBED_NONAV
    }

    /**
     * Constructor
     */
    public Tabs() {
        this(TabsType.NORMAL);
    }

    /**
     * Constructor
     *
     * @param tabsType NORMAL is simple tabs that can't be closed; CLOSABLE is
     * tabs that can be sorted and closed; CLOSABLE_UNTABBED allows closing and
     * navigation through a tabless GUI; CLOSABLE_UNTABBED_NONAV does not have
     * the navigation buttons
     */
    public Tabs(TabsType tabsType) {
        if (null == tabsType) {
            this.closable = false;
            this.showTabs = true;
            showNavigation = true;
        } else {
            switch (tabsType) {
                case NORMAL:
                    closable = false;
                    showTabs = true;
                    showNavigation = true;
                    break;
                case CLOSABLE:
                    closable = true;
                    showTabs = true;
                    showNavigation = true;
                    break;
                case CLOSABLE_UNTABBED:
                    closable = true;
                    showTabs = false;
                    showNavigation = true;
                    break;
                case CLOSABLE_UNTABBED_NONAV:
                    closable = true;
                    showTabs = false;
                    showNavigation = false;
                    break;
                default:
                    closable = false;
                    showTabs = true;
                    showNavigation = true;
                    break;
            }
        }
        titleContainer = showTabs ? null : new HBox();
    }

    /**
     * Enables socket pushing for this component and all tabs
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    @Override
    public void init(String masterId, WSession session) {
        super.init(masterId, session);
        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).label.init(masterId, session);
            tabs.get(i).layout.init(masterId, session);
        }
        plusMenu.init(masterId, session);
        tabMenu.init(masterId, session);
        if (!showTabs) {
            titleContainer.init(masterId, session);
        }
    }

    /**
     * Sets the options for the plus button at the top right
     *
     * @param options options that can be selected
     */
    public void setPlusOptions(List<String> options) {
        int oldNumOptions = plusOptions.size();
        plusOptions.clear();
        plusOptions.addAll(options);
        plusMenu.setOptions(options);
        if (isRendered()) {
            //update plus button html only if the number of options goes from many to one or one to many
            if ((oldNumOptions <= 1 && options.size() > 1) || (oldNumOptions > 1 && options.size() <= 1)) {
                if (options.size() <= 1) {
                    //destroy the context menu, still appears even though the html is removed from the plus button
                    plusMenu.exec("$jq.contextMenu('destroy', '." + plusMenu.getId() + "');");
                }
                //update the html of the plus button so the menu appears/disappears, only want it to appear for multiple options
                String html = "<td id='plusbutton" + getId() + "' style='padding:0; cursor: pointer'><span class='ui-icon ui-icon-plus "
                        + plusMenu.getId() + "'" + (plusOptions.size() <= 1 ? " onclick=\"" + evtStart + "add'});\"" : "") + " onmouseover=\""
                        + evtStart + "plusHover'});\"></span>" + (plusOptions.size() <= 1 ? "" : plusMenu.toHtml()) + "</td>";
                html = html.replace("#LAYOUT_ID#", layoutId).replace("\"", "\\\"").replace("\n", "\\n");
                exec("$('#plusbutton" + getId() + "').replaceWith(\"" + html + "\");");
            }
        }
    }

    /**
     * Programmatically select the tab at the given index
     *
     * @param index index of the tab to select
     */
    public void selectTab(int index) {
        setSelectedIndex(index);
        tabs.get(index).lazyLoad();
        if (closable) {
            exec("$jq('#" + getTabsId() + "').scrollTabs('refresh');\n"
                    + "$jq('#" + getTabsId() + "').scrollTabs('option', 'active', " + index + ");");
        } else {
            exec("$jq('#" + getTabsId() + "').tabs().tabs('option', 'active', " + index + ");");
        }
    }

    /**
     * Adds a tab with the given name, whose content is the provided layout
     *
     * @param name the name of the tab
     * @param layout the content of the tab
     * @return the created Tab
     */
    public Tab addTab(String name, Layout layout) {
        return addTab(new Tab(name, layout));
    }

    /**
     * Adds a tab with the given TabLabel, whose content is the provided layout
     *
     * @param label the label of the tab
     * @param layout the content of the tab
     * @return the created Tab
     */
    public Tab addTab(TabLabel label, Layout layout) {
        return addTab(new Tab(label, layout));
    }

    /**
     * Adds the given tab to this Tabs
     *
     * @param tab the Tab to add
     * @return the tab that was passed in as a parameter
     */
    public Tab addTab(Tab tab) {
        tabs.add(tab);
        updateTabSelector();
        tab.label.init(layoutId, session);
        tab.layout.init(layoutId, session);
        if (!isRendered()) {
            return tab; //don't bother adding it to the current drawing, because it hasn't been drawn yet
        }
        if (!showTabs) {
            titleContainer.removeAll();
            titleContainer.add(tab.label, titleCount.setText(showNavigation ? "(" + tabs.size() + " of " + tabs.size() + ")" : ""));
        }
        tab.lazyLoad(); //no need to "lazy" load it, because we're actually selecting it
        exec("$jq('.header" + getId() + "').show();\n"
                + "var tabs = $jq('#" + getTabsId() + "');\n"
                + "$jq(\"" + getTabLi(tab).replace("#LAYOUT_ID#", layoutId).replace("\"", "\\\"") + "\").appendTo(tabs.find('ul').eq(0));\n"
                + "$jq(\"" + (getTabContent(tab)).replace("#LAYOUT_ID#", layoutId).replace("\"", "\\\"").replace("\n", "\\n") + "\").appendTo(tabs);\n"
                + (closable ? "$jq('#li" + tab.id + "').insertBefore($jq('#li" + tab.id + "').prev());\n$jq('#" + tab.id + "').insertBefore($jq('#" + tab.id + "').prev());\n" : "")
                + "tabs.scrollTabs('refresh');\n"
                + "tabs.scrollTabs('option', 'active', " + tabs.indexOf(tab) + ");\n"); //select the tab we just added
        WTools.initToolTips(tab.layout, session);
        setSelectedIndex(tabs.size() - 1);
        return tab;
    }

    /**
     * Removes the given tab and also disposes it
     *
     * @param tab the tab to remove and dispose
     */
    public void removeAndDisposeTab(Tab tab) {
        fireEvent("#close " + tab.id);
        exec("$jq('#" + tab.id + "').remove();\n"
                + "$jq('#li" + tab.id + "').remove();\n"
                + "$jq('#" + getTabsId() + "').scrollTabs('refresh');");
        tab.layout.dispose();
        if (tabs.indexOf(tab) == tabs.size() - 1) {
            setSelectedIndex(Math.max(0, selectedIndex - 1));
        }
    }

    /**
     * Removes and disposes all tabs in this Tabs
     */
    public void clearTabs() {
        for (int i = tabs.size() - 1; i >= 0; i--) {
            removeAndDisposeTab(i);
        }
    }

    /**
     * Removes and disposes the tab at the given index
     *
     * @param index the index of the Tab to remove and dispose
     */
    public void removeAndDisposeTab(int index) {
        removeAndDisposeTab(tabs.get(index));
    }

    /**
     * Updates the selector dropdown that allows you to select a Tab by name
     */
    private void updateTabSelector() {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
            options.add(tabs.get(i).label.getText());
        }
        tabMenu.setOptions(options);
    }

    /**
     * Gets the Tab at the given index
     *
     * @param index the index of the Tab to get
     * @return the Tab at the given index
     */
    public Tab getTab(int index) {
        return tabs.get(index);
    }

    /**
     * @return the number of tabs in this Tabs
     */
    public int size() {
        return tabs.size();
    }

    /**
     * @return the word 'tabs' plus this Object's hash code
     */
    private String getTabsId() {
        return "tabs" + hashCode();
    }

    /**
     * @return a custom DOM identification
     */
    @Override
    public String getId() {
        return "t" + hashCode();
    }

    /**
     * Moves the given Tab to a different location index
     *
     * @param newIndex the new index to place movedTab
     * @param movedTab the Tab to move
     */
    public void moveTab(int newIndex, Tab movedTab) {
        if (showNavigation) {
            exec("alert('Error: Unable to externally move tab that has navigation enabled');");
            return;
        }

        int oldIndex = tabs.indexOf(movedTab);
        if (oldIndex == newIndex) {
            return; //I don't think this can happen, but just in case
        } else if (newIndex > oldIndex) {
            String currentTabAtIndex = tabs.get(newIndex).id;
            exec("$jq('#" + movedTab.id + "').insertAfter('#" + currentTabAtIndex + "');\n"
                    + "$jq('#li" + movedTab.id + "').insertAfter('#li" + currentTabAtIndex + "');\n"
                    + "$jq('#" + getTabsId() + "').scrollTabs('refresh');");
        } else {
            String currentTabAtIndex = tabs.get(newIndex).id;
            exec("$jq('#" + movedTab.id + "').insertBefore('#" + currentTabAtIndex + "');\n"
                    + "$jq('#li" + movedTab.id + "').insertBefore('#li" + currentTabAtIndex + "');\n"
                    + "$jq('#" + getTabsId() + "').scrollTabs('refresh');");
        }
        tabs.remove(movedTab);
        tabs.add(newIndex, movedTab);
        orderListeners.update("");
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
        if (event.equals("#add")) {
            addListeners.update("add");
        } else if (event.startsWith("#order ") && event.endsWith(" #T#T ")) {
            List<String> oldOrder = new ArrayList<>();
            for (Tab tab : tabs) {
                oldOrder.add("li" + tab.id);
            }
            String selectedItem = "";
            List<String> eventOrder = Arrays.asList(event.substring(0, event.length() - 6).replace("#order ", "").replace("=false", "").replace("=true", "").split(" #T#T "));
            List<String> newOrder = new ArrayList<>();
            for (String tabItem : eventOrder) {
                if (oldOrder.contains(tabItem)) {
                    newOrder.add(tabItem);
                    if (event.contains(tabItem + "=true")) {
                        selectedItem = tabItem;
                    }
                }
            }
            Collections.sort(tabs, (tab1, tab2) -> Integer.compare(newOrder.indexOf("li" + tab1.id), newOrder.indexOf("li" + tab2.id)));
            orderListeners.update("");
            setSelectedIndex(newOrder.indexOf(selectedItem));
        } else if (event.startsWith("#close ")) {
            String layout = event.replace("#close ", "");
            for (int i = 0; i < tabs.size(); i++) {
                if (("tab" + tabs.get(i).layout.getId()).equals(layout)) {
                    tabs.get(i).content.dispose();
                    tabs.remove(i);
                    if (tabs.isEmpty()) {
                        exec("$jq('.header" + getId() + "').hide();\n");
                    } else if (i > 0) {
                        tabs.get(i - 1).lazyLoad();
                    } else if (i == 0 && tabs.size() >= 2) {
                        tabs.get(i + 1).lazyLoad();
                    }
                    break;
                }
            }
            if (tabs.isEmpty()) {
                tabCount = 0;
                indexListeners.update("-1");
            }
            removeListeners.update(layout);
        } else if (event.startsWith("#selection ")) {
            String id = event.replace("#selection ", "");
            for (int i = 0; i < tabs.size(); i++) {//Tab tab : tabs) {
                Tab tab = tabs.get(i);
                if (tab.id.equals(id)) {
                    if (!showTabs) {
                        titleContainer.removeAll();
                        titleContainer.add(tab.label, titleCount.setText(showNavigation ? "(" + (i + 1) + " of " + tabs.size() + ")" : ""));
                    }
                    tab.lazyLoad();
                    setSelectedIndex(tabs.indexOf(tab));
                    return;
                }
            }
        } else if (event.startsWith("#trashHover")) {
            if (selectedIndex == 0 && selectedIndex != tabs.size() - 1) {
                tabs.get(selectedIndex + 1).lazyLoad();
            } else if (selectedIndex > 0) {
                tabs.get(selectedIndex - 1).lazyLoad();
            }
        } else if (event.startsWith("#nextHover") && selectedIndex != tabs.size() - 1) {
            tabs.get(selectedIndex + 1).lazyLoad();
        } else if (event.startsWith("#nextClick") && selectedIndex != tabs.size() - 1) {
            selectTab(selectedIndex + 1);
            if (selectedIndex != tabs.size() - 1) {
                tabs.get(selectedIndex + 1).lazyLoad(); //load the next one up, in case they click this button again
            }
        } else if (event.startsWith("#prevHover") && selectedIndex != 0) {
            tabs.get(selectedIndex - 1).lazyLoad();
        } else if (event.startsWith("#prevClick") && selectedIndex != 0) {
            selectTab(selectedIndex - 1);
            if (selectedIndex != 0) {
                tabs.get(selectedIndex - 1).lazyLoad(); //make sure the previous one is loaded, in case they click this button again
            }
        } else if (event.startsWith("#save ")) {
            //save as template
            String id = event.replace("#save ", "");
            for (int i = 0; i < tabs.size(); i++) {
                Tab tab = tabs.get(i);
                if (tab.id.equals(id)) {
                    saveListeners.update(String.valueOf(i));
                }
            }
        } else if (event.equals("#plusHover")) {
            addHoverListeners.update(null);
        }
        updateTabSelector();
    }

    /**
     * Changes the currently selected index and updates associated variables
     *
     * @param index the index of the selected Tab
     */
    private void setSelectedIndex(int index) {
        if (selectedIndex != index || tabCount != tabs.size()) {
            selectedIndex = index;
            tabCount = tabs.size();
            indexListeners.update(index + "");
        }
    }

    /**
     * @return the index of the currently selected Tab
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Creates an 'li' for the provided Tab so we can make the tab label
     *
     * @param tab we're getting the handle for the provided Tab
     * @return a String that can be ingested by the Tabs javascript component
     */
    private String getTabLi(Tab tab) {
        String closeButton = "";
        if (closable) {
            closeButton = "<span class='ui-icon ui-icon-close ui-closable-tab' onclick=\"" + evtStart + "close " + tab.id + "'});$jq('#" + tab.id + "').remove();$jq('#li" + tab.id + "').remove();$jq('#" + getTabsId() + "').scrollTabs('refresh');\"></span>";
        }
        return "<li id='li" + tab.id + "' style='white-space: nowrap; display: " + (showTabs ? "inline-block" : "none") + "' role='tab'><table><tr><td><a href='#" + tab.id + "' role='presentation'>" + (showTabs ? tab.label.toHtml() : "") + "</a></td><td>" + (closable ? closeButton : "<a class='ui-tabs-anchor'/a>") + "</td></tr></table></li>";
    }

    /**
     * Creates a DIV String that represents the content of the given Tab
     *
     * @param tab the Tab for which to get the content
     * @return a DIV String that represents the content of the given Tab
     */
    private String getTabContent(Tab tab) {
        return "<div id='" + tab.id + "' role='tabpanel'><table><tr><td style='vertical-align:top; padding: 0; margin: 0; width: 100%'>" + tab.layout.toHtml() + "</td><td><div id='height" + tab.id + "'/></td></tr><tr><td><div id='width" + tab.id + "'/></td></tr></table></div>";
    }

    /**
     * Provides the HTML representation of this Component
     *
     * @return the HTML for this Component
     */
    @Override
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public String toHtml() {
        if (!tabs.isEmpty()) {
            if (!showTabs) {
                titleContainer.removeAll();
                titleContainer.add(tabs.get(0).label, titleCount.setText(showNavigation ? "(1 of " + tabs.size() + ")" : ""));
            }
            tabs.get(0).lazyLoad();
        }
        String tabId = getTabsId();
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='" + getId() + "'><div id='" + tabId + "'>\n<ul role='tablist' style='width: 100" + (showTabs ? "px" : "%") + "'>\n"); //start
        for (Tab tab : tabs) {
            sb.append(getTabLi(tab)); //add each tab
        }
        if (closable) { //create "add" and "dropdown" buttons
            if (showTabs) {
                sb.append("<li id='liadd" + tabId + "' role='tab'>"
                        + "<span class='ui-icon ui-icon-plus " + plusMenu.getId() + "'" + (plusOptions.size() <= 1 ? " onclick=\"" + evtStart + "add'});\"" : "") + "></span>" + (plusOptions.size() <= 1 ? "" : plusMenu.toHtml())
                        + "<span class='ui-icon ui-icon-triangle-1-s " + tabMenu.getId() + "'></span>" + tabMenu.toHtml() + "</li>");
            } else {
                sb.append("<span id='liadd" + tabId + "' role='tab'><table><tr>"
                        + "<td class='header" + getId() + "' style='padding:0 7px 0 0; cursor: pointer'><span class='ui-icon ui-icon-disk' onclick=\"var saveTabId=$jq('#" + getTabsId() + " .ui-tabs-panel:visible').attr('id');" + evtStart + "save ' + saveTabId});\"></span></td>"
                        + "<td class='header" + getId() + "' style='padding:0 7px 0 0; cursor: pointer'><span class='ui-icon ui-icon-trash' onclick=\"var removeTabId=$jq('#" + getTabsId() + " .ui-tabs-panel:visible').attr('id');" + evtStart + "close ' + removeTabId});$jq('#' + removeTabId).remove();$jq('#li' + removeTabId).remove();$jq('#" + getTabsId() + "').scrollTabs('refresh');\" onmouseover=\"" + evtStart + "trashHover'});\"></span></td>"
                        + "<td id='plusbutton" + getId() + "' style='padding:0; cursor: pointer'><span class='ui-icon ui-icon-plus " + plusMenu.getId() + "'" + (plusOptions.size() <= 1 ? " onclick=\"" + evtStart + "add'});\"" : "") + " onmouseover=\"" + evtStart + "plusHover'});\"></span>" + (plusOptions.size() <= 1 ? "" : plusMenu.toHtml()) + "</td>"
                        + (showNavigation ? "<td class='header" + getId() + "' style='padding:0 0 0 7px; cursor: pointer'><span style='width: 12px' class='ui-icon ui-icon-triangle-1-w' onclick=\"" + evtStart + "prevClick'});\" onmouseover=\"" + evtStart + "prevHover'});\"></span></td>"
                                + "<td class='header" + getId() + "' style='padding:0; cursor: pointer'><span class='ui-icon ui-icon-triangle-1-e' onclick=\"" + evtStart + "nextClick'});\" onmouseover=\"" + evtStart + "nextHover'});\"></span></td>" : "")
                        + "<td class='header" + getId() + "' style='padding:0 0 0 7px; border: none'>" + titleContainer.toHtml() + "</td>"
                        + (showNavigation ? "<td class='header" + getId() + "' style='padding:0; cursor: pointer'><span class='ui-icon ui-icon-triangle-1-s " + tabMenu.getId() + "'></span>" + tabMenu.toHtml() + "</td>" : "")
                        + "</tr></table></span>");
            }
        }
        sb.append("</ul>\n"); //finish the list
        for (Tab tab : tabs) {
            sb.append(getTabContent(tab));
        }
        if (closable) {
            sb.append("<div id='add" + tabId + "' role='tabpanel'></div>\n");
        }

        String tabsvar = "tabs" + tabId;
        sb.append("</div>\n"
                //Now to make our script, which is pretty complex so let's break it down
                + "<script>\n"
                + "var prevSelection" + getId() + " = '" + (tabs.isEmpty() ? "" : tabs.get(0).id) + "';\n"
                + "var selection" + getId() + " = '" + (tabs.isEmpty() ? "" : tabs.get(0).id) + "';\n"
                + "var maxWidth" + getId() + " = 0;\n"
                + "var maxHeight" + getId() + " = 0;\n"
                + "var scrollEnabled = true;\n"
                + "var " + tabsvar + " = $jq('#" + tabId + "');\n"
                //here's where we actually turn it into scrollTabs
                + "$jq(function () {\n"
                + "    " + tabsvar + ".scrollTabs({\n"
                + "            activate: function(event ,ui){\n"
                + "                prevSelection" + getId() + " = selection" + getId() + ";\n"
                + "                selection" + getId() + " = ui.newPanel[0].id;\n"
                + "                " + evtStart + "selection ' + selection" + getId() + "});\n"
                + "            },\n"
                + "        scrollOptions: {\n"
                + "            enableDebug: false,\n"
                + "            closable: false,\n"
                + "            showFirstLastArrows: true,\n"
                + "            selectTabAfterScroll: false\n"
                + "        },\n"
                + "    });\n"
                //sorting is only enabled if tabs can be closed and are shown, otherwise you don't get to sort anything
                + (closable && showTabs ? ""
                        + tabsvar + ".find('.ui-tabs-nav').eq(0).sortable({\n"
                        + "    axis: 'x',\n"
                        + "    stop: function () {\n"
                        + "        var names = '';\n"
                        + "        $('#" + tabId + " ul li').each(function(i) {\n"
                        + "            names += $(this).attr('id') + '=' + $(this).attr('aria-selected') + ' #T#T ';\n"
                        + "        });\n"
                        + "        " + evtStart + "order ' + names});"
                        + "    }\n"
                        + "});\n" : "")
                //FireFox sets this to -17 for some reason, so let's fix it
                + tabsvar + ".find('.ui-scroll-tabs-view').eq(0).css('margin-bottom', '0px');\n"
                + (minWidth.equals("") ? "" : tabsvar + ".find('.ui-scroll-tabs-view').eq(0).css('min-width', '" + minWidth + "');\n")
                + (tabs.isEmpty() ? "$jq('.header" + getId() + "').hide();\n" : "")
                //end the script
                + "});\n</script></div>"
        );
        return sb.toString();
    }

    /**
     * Sets the minimum width of this Tabs widget
     *
     * @param minWidth the minimum width as a css property string
     * @return this
     */
    public Tabs setMinWidth(String minWidth) {
        this.minWidth = minWidth;
        if (isRendered()) {
            exec("tabs" + getTabsId() + ".find('.ui-scroll-tabs-view').eq(0).css('min-width', '" + minWidth + "');");
        }
        return this;
    }

    /**
     * To be called when this component is no longer needed; disposes the Tabs
     * item and each individual Tab
     */
    @Override
    public void dispose() {
        for (Tab tab : tabs) {
            tab.layout.dispose();
        }
        tabs.clear();
        plusMenu.dispose();
        tabMenu.dispose();
        addListeners.clear();
        orderListeners.clear();
        removeListeners.clear();
        indexListeners.clear();
    }

    /**
     * Tab is an Object that can be added to a Tabs collection
     */
    public static class Tab {

        /**
         * the TabLabel associated with this Tab
         */
        public TabLabel label;
        /**
         * this Tab's content will actually reside inside of this
         */
        public final VBox layout = new VBox();
        /**
         * the layout that is to be displayed when this Tab is selected
         */
        public final Layout content;
        /**
         * flag indicating if content has already been loaded
         */
        private boolean contentLoaded = false;
        /**
         * the identification of this Tab
         */
        public String id;

        /**
         * Constructor
         *
         * @param label the TabLabel of this Tab
         * @param content the content of this Tab
         */
        public Tab(TabLabel label, Layout content) {
            this.label = label;
            this.label.tab = Tab.this;
            this.content = content;
            this.id = "tab" + layout.getId();
            layout.setWidth("100%");
        }

        /**
         * Constructor
         *
         * @param name the String name of this Tab, used to create a TabLabel
         * @param content the content of this Tab
         */
        public Tab(String name, Layout content) {
            this(new TabLabel(name), content);
        }

        /**
         * Lazily loads the tab's content, but only if it isn't already loaded
         */
        protected void lazyLoad() {
            if (!contentLoaded) {
                contentLoaded = true;
                layout.add(content);
            }
        }

    }

    /**
     * TabLabel is a pretty simple Label, but it attempts a lazy load of the Tab
     * when hovered over it
     */
    public static class TabLabel extends Label {

        /**
         * the Tab that is to be labeled
         */
        private Tab tab;

        /**
         * Constructor
         *
         * @param text the label's text
         */
        public TabLabel(String text) {
            super(text);
        }

        /**
         * Any time this happens on a TabLabel, we need to attempt a lazy load
         *
         * @param evt required but not used
         */
        @Override
        public void fireEvent(String evt) {
            tab.lazyLoad();
        }

        /**
         * @return the Label html, but with lazy load on hover
         */
        @Override
        public String toHtml() {
            return super.toHtml().replace(" class=", " onmouseover=\"changed" + layoutId + "({id: '" + getId() + "', value: '#hover'});\" class=");
        }
    }

}
