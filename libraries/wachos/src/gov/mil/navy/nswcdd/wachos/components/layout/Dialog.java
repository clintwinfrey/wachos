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

import gov.mil.navy.nswcdd.wachos.components.Component;
import gov.mil.navy.nswcdd.wachos.components.ComponentListeners;
import gov.mil.navy.nswcdd.wachos.components.Tabs;
import gov.mil.navy.nswcdd.wachos.components.Tabs.Tab;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;

/**
 * Creates a JQuery dialog on the client side
 */
public class Dialog {

    /**
     * the user's session
     */
    WSession session;
    /**
     * the dialog's title
     */
    String title;
    /**
     * this is the content of the dialog, where you can add/remove Components
     */
    protected final VBox content = new VBox();
    /**
     * the Layout that created this Dialog; can ONLY be created by a Layout
     */
    private final Layout creator;
    /**
     * keep track of whether this Dialog has actually been added to the DOM
     */
    private boolean addedToDom = false;
    /**
     * the ID of the master layout
     */
    private String layoutId;
    /**
     * flag indicating if this Dialog is currently opened
     */
    private boolean opened = false;
    /**
     * listens for when this Dialog closes
     */
    public final ComponentListeners closeListeners = new ComponentListeners();
    /**
     * defines whether this dialog blocks input to some other top-level windows
     * in the application, except for windows created with the dialog box as
     * their owner
     */
    private boolean modal = false;

    /**
     * Constructor
     *
     * @param title text displayed at the top of the Dialog
     * @param session the user's session
     * @param creator the layout from which this Dialog was created
     */
    protected Dialog(String title, WSession session, Layout creator) {
        this.session = session;
        this.title = title;
        this.creator = creator;
        creator.dialogs.add(Dialog.this);
        content.setWidth("100%");
    }

    /**
     * Sets whether this dialog is modal (blocks input to some other top-level
     * windows in the application, except for windows created with the dialog
     * box as their owner)
     *
     * @param modal true if it blocks acess to other GUI components
     * @return this
     */
    public Dialog setModal(boolean modal) {
        this.modal = modal;
        return this;
    }

    /**
     * @return the content of this dialog
     */
    public VBox getContent() {
        return content;
    }

    /**
     * Initializes this component with the ID of the master layout and session
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    public void init(String masterId, WSession session) {
        if (session != null) {
            this.session = session;
        }
        if (!masterId.equals("#LAYOUT_ID#")) { //this isn't a valid master ID
            layoutId = masterId;
        }
    }

    /**
     * Opens the Dialog
     */
    public void open() {
        open(false);
    }

    /**
     * Opens the Dialog at the user's mouse location
     */
    public void openAtMouse() {
        open(true);
    }

    /**
     * Opens the Dialog
     *
     * @param openAtMouse if true, opens at the user's mouse location; otherwise
     * opens at center of browser frame
     */
    private void open(boolean openAtMouse) {
        if (opened) {
            return; //nothing to do
        }

        if (!addedToDom) {
            content.init(layoutId, session);
            String dialogStr = "<div id='" + getId() + "'><div id='dialog" + getId() + "' title='" + title + "'>\n"
                    + content.toHtml().replace("\\\\", "\\").replace("\\", "\\\\")
                    + "</div>\n"
                    + "<script>\n"
                    + "  $jq(function() {\n"
                    + "    $jq('#dialog" + getId() + "').dialog({"
                    + "      autoOpen: false,\n"
                    + "      resizable: false,\n"
                    + "      autoResize: true,\n"
                    + (modal ? "      modal: true,\n" : "")
                    + (this instanceof Splash ? "      dialogClass: 'tfsplash',\n" : "")
                    + "    });\n"
                    + (this instanceof Splash ? "    $('#dialog" + getId() + "').removeClass();\n" : "")
                    + "  });\n"
                    + "</script>\n"
                    + "</div>";
            session.exec("var appendix = \"" + dialogStr.replace("#LAYOUT_ID#", layoutId).replace("\"", "\\\"").replace("\n", "\\n") + "\";\n"
                    + "$('#dialogsContainer" + session.hashCode() + "').append(appendix);");
            WTools.initToolTips(content, session);
        }
        String dlg = "dialog" + hashCode();
        session.exec("var " + dlg + " = $jq('#dialog" + getId() + "');\n"
                + "" + dlg + ".dialog('open');\n" //open it
                + "" + dlg + ".css('min-height', '');\n" //remove predefined "min-height" and "width"
                + "" + dlg + ".parent().css('width', '');\n"
                + "" + dlg + ".css('overflow-x', 'hidden');\n"
                + "" + dlg + ".css('overflow-y', 'auto');\n"
                + "" + dlg + ".css('max-height', $(window).height() * .9);"
                + (addedToDom ? "" : "" + dlg + ".prev().html(" + dlg + ".prev().html().replace('<button', '<span').replace('</button>', '</span>'));\n"
                        + "var btn = " + dlg + ".prev().find('.ui-button').eq(0);\n"
                        + "btn.html(btn.html().replace(\"ui-icon-closethick\\\"\", \"ui-icon-closethick\\\" onclick=\\\"changed" + layoutId + "({id: '" + getId() + "', value: '#closing'});\\\"\").replace('Close', ''));")
                + dlg + ".parent().position({ my: 'center', at: 'center', of: " + (openAtMouse ? "tfMouseEvent" : "window") + " });");
        addedToDom = true;
        opened = true;
    }

    /**
     * Resets the position of the dialog to the center of the frame
     */
    public void resetPosition() {
        String dlg = "dialog" + hashCode();
        session.exec("var " + dlg + " = $jq('#dialog" + getId() + "');\n"
                + "" + dlg + ".parent().position({ my: 'center', at: 'center', of: window});");
    }

    /**
     * Closes the Dialog; closes children; does NOT dispose of this Dialog; call
     * dispose instead if you don't intend for this to ever be opened again
     */
    public void close() {
        if (opened) {
            opened = false;
            closeListeners.update("closed");
            session.exec("$jq('#dialog" + getId() + "').dialog('close');");
            closeChildren(content);
        }
    }

    /**
     * Recursively closes all Dialogs that are children of the provided Layout
     *
     * @param layout the layout whose children are to be closed
     */
    private void closeChildren(Layout<?> layout) {
        for (Dialog dialog : layout.dialogs) {
            dialog.close();
        }
        for (Component component : layout.getComponents()) {
            if (component instanceof Tabs) {
                for (Tab tab : ((Tabs) component).tabs) {
                    closeChildren(tab.layout);
                }
            } else if (component instanceof Layout) {
                closeChildren((Layout) component);
            }
        }
    }

    /**
     * @return this object's unique identification
     */
    public String getId() {
        return "dlg" + hashCode();
    }

    /**
     * To be called when this component is no longer needed; removes from the
     * DOM and disposes the Tabs item and each individual Tab
     */
    public void dispose() {
        if (addedToDom) {
            if (opened) {
                close();
            }
            session.exec("$('#" + getId() + "').remove();"); //remove this element from the DOM
            addedToDom = false;
        }
        content.dispose();
        creator.removeDialog(this); //only completely remove a Dialog if it has been disposed; otherwise, it might be reopened
    }

}
