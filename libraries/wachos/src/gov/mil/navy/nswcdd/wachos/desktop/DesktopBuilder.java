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

import gov.mil.navy.nswcdd.wachos.desktop.responder.HtmlResponder;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

/**
 * DesktopBuilder creates a layout for a WACHOS desktop application
 */
public class DesktopBuilder {

    /**
     * if in web mode, some things behave differently such as file selection
     */
    public static enum Mode {
        /**
         * is run through a browser
         */
        WEB,
        /**
         * is run as a standalone application
         */
        DESKTOP,
        /**
         * is run in android
         */
        ANDROID
    }

    /**
     * the default mode, which must be changed if we are in desktop mode
     */
    public static Mode MODE = Mode.WEB;
    /**
     * the Java Swing application, if in desktop mode
     */
    public static JFrame JFRAME;

    /**
     * When receiving a message from the client, an event may be fired;
     * alternately, something may be printed to the console
     *
     * @param layout receives the message from the client
     * @param message the thing that was received
     * @param error if true, this will be printed using System.err
     */
    public static void receiveMessage(Layout layout, String message, boolean error) {
        if (layout != null && message.startsWith("#WAJAX#")) {
            String[] strs = message.substring(7).split("#WACHOSBREAK#");
            layout.fireEvent(strs[0], strs.length > 1 ? strs[1] : "");
        } else if (message.contains("Blocked script execution in 'about:blank'") || message.contains("Canvas2D: Multiple readback operations using getImageData are faster with the willReadFrequently")) {
            //do nothing, these are garbage messages
        } else if (error) {
            System.err.println("Console: " + message);
        } else if (!message.contains("exported wcDocker")) {
            System.out.println("Console: " + message);
        }
    }

    /**
     * Creates the WACHOS layout for the desktop application
     *
     * @param server serves the layout for consumption in the embedded browser
     * @param layoutMaker the thing that makes the layout
     * @param session the user's session
     * @return the generated layout
     */
    public static Layout createLayout(NanoServer server, LayoutMaker layoutMaker, WSession session) {
        Layout layout = layoutMaker.createLayout(session);
        layout.init(layout.getId(), session);
        StringBuilder sb = new StringBuilder();
        WTools.createToolTipsScript(layout, sb);
        layout.exec("$('#tooltipInit" + layout.getId() + "').remove();", 5000); //in five seconds, remove the tooltipInit script from the dom because it'll have been run already; this is for tidying
        String htmlContent = (layout.toHtml() + "<script id='tooltipInit" + layout.getId() + "'>" + sb.toString() + "</script>").replace("#LAYOUT_ID#", layout.getId());
        server.add(new HtmlResponder("wachos" + layout.getId(), HTML.replace("SESSIONHASHCODE", session.hashCode() + "").replace("WACHOS_THEME", session.theme)
                .replace("WACHOS_FONTSIZE", session.fontSize).replace("WACHOS_FONT", session.fontFamily).replace("#REPLACE_WITH_COMPONENT", htmlContent).replace("#JCEF_CALLBACK",
                "<script>var changed" + layout.getId() + "=function(o){ console.log('#WAJAX#' + o.id + '#WACHOSBREAK#' + o.value); };</script>")));
        return layout;
    }

    /**
     * Creates a desktop-based HttpSession object
     *
     * @return an HttpSession for desktop use
     */
    public static HttpSession createDesktopHttpSession() {
        return new HttpSession() {
            private final Map<String, Object> attributes = new HashMap<>();

            @Override
            public long getCreationTime() {
                return 1;
            }

            @Override
            public String getId() {
                return "Desktop";
            }

            @Override
            public long getLastAccessedTime() {
                return 1;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public void setMaxInactiveInterval(int i) {
            }

            @Override
            public int getMaxInactiveInterval() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Object getAttribute(String string) {
                return attributes.get(string);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public void setAttribute(String string, Object o) {
                attributes.put(string, o);
            }

            @Override
            public void removeAttribute(String string) {
                attributes.remove(string);
            }

            @Override
            public void invalidate() {
            }

            @Override
            public boolean isNew() {
                return false;
            }
        };
    }

    /**
     * HTML is the content of a downloaded WACHOS page, with the layout div
     * replaced by #REPLACE_WITH_COMPONENT
     */
    private static final String HTML = ("<!--?xml version='1.0' encoding='UTF-8' ?-->\n"
            + "<!DOCTYPE html>\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head id=\"j_idt2:j_idt3\">\n"
            + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n"
            + "            <link type=\"text/css\" rel=\"stylesheet\" href=\"themes/WACHOS_THEME.css\" id=\"qtheme\">\n"
            + "            <script type=\"text/javascript\" src=\"wjs/jquery.js\"></script>\n"
            + "            <script type=\"text/javascript\" src=\"wjs/jquery-plugins.js\"></script>\n"
            + "            <script type=\"text/javascript\" src=\"wjs/core.js\"></script>\n"
            + "            <link type=\"text/css\" rel=\"stylesheet\" href=\"wjs/components.css\">\n"
            + "            <script type=\"text/javascript\" src=\"wjs/components.js\"></script>\n"
            + "            <script type=\"text/javascript\">if (window.PrimeFaces){PrimeFaces.settings.locale = 'en_US'; PrimeFaces.settings.projectStage = 'Development'; }</script>\n"
            + "            <script src=\"wjs/jquery.min.js\"></script>\n"
            + "            <script>var $jq = jQuery.noConflict();</script>\n"
            + "            <script src=\"wjs/jquery-ui.min.js\"></script>\n"
            + "            <script src=\"wjs/jquery.mousewheel.min.js\"></script>\n"
            + "            <script src=\"wjs/jquery.ui.scrolltabs.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wjs/jquery.qtip.min.css\">\n"
            + "            <script src=\"wjs/jquery.qtip.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wjs/jquery.contextMenu.css\">\n"
            + "            <script src=\"wjs/jquery.contextMenu.min.js\"></script>\n"
            + "            <script src=\"wjs/chart.min.js\"></script>\n"
            + "            <script src=\"wjs/svg-pan-zoom-container.js\"></script><style>[data-zoom-on-wheel]{overflow:scroll}[data-zoom-on-wheel]>:first-child{width:100%;height:100%;vertical-align:middle;transform-origin:0 0}</style>\n"
            + "            <script src=\"wjs/jstree.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wjs/jstree.min.css\">\n"
            + "            <script src=\"wjs/Sortable.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wjs/highlight.min.css\">\n"
            + "            <script src=\"wjs/highlight.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" href=\"wjs/katex.min.css\">\n"
            + "            <script src=\"wjs/katex.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" href=\"wjs/quill.snow.css\">\n"
            + "            <script src=\"wjs/quill.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wjs/slickgrid.min.css\">\n"
            + "            <script src=\"wjs/slickgrid.min.js\"></script>\n"
            + "\n"
            + "            <style type=\"text/css\">\n"
            + "                .wfontsize { font-size: WACHOS_FONTSIZEpx }\n"
            + "                .wfontfamily { font-family: WACHOS_FONT }\n"
            + "                .ui-selectlistbox-item,.ui-widget,.ui-widget .ui-widget { font-size: WACHOS_FONTSIZEpx; font-family: WACHOS_FONT }\n"
            + "                .ui-scroll-tabs-view { z-index: 1; overflow: hidden; }\n"
            + "                .ui-scroll-tabs-view .ui-widget-header { border: none; background: transparent; }\n"
            + "                .ui-scroll-tabs-header { position: relative; overflow: hidden; }\n"
            + "                .ui-scroll-tabs-header .stNavMain { position: absolute; top: 0; z-index: 2; height: 100%; opacity: 0; transition: left .5s, right .5s, opacity .8s; transition-timing-function: swing; }\n"
            + "                .ui-scroll-tabs-header .stNavMain button { height: 100%; padding: 0; }\n"
            + "                .ui-scroll-tabs-header .stNavMainLeft { left: -250px; }\n"
            + "                .ui-scroll-tabs-header .stNavMainLeft.stNavVisible { left: 0; opacity: 1; }\n"
            + "                .ui-scroll-tabs-header .stNavMainRight { right: -250px; }\n"
            + "                .ui-scroll-tabs-header .stNavMainRight.stNavVisible { right: 0; opacity: 1; }\n"
            + "                .ui-scroll-tabs-header ul.ui-tabs-nav { position: relative; white-space: nowrap; }\n"
            + "                .ui-scroll-tabs-header ul.ui-tabs-nav li { display: inline-block; float: none; }\n"
            + "                .ui-scroll-tabs-header ul.ui-tabs-nav li.stHasCloseBtn a { padding-right: 2px; }\n"
            + "                .ui-scroll-tabs-header ul.ui-tabs-nav li span.stCloseBtn { float: left; padding: 0px 0px; border: none; cursor: pointer; }\n"
            + "                .ui-tabs .ui-tabs-nav li { margin: 0px 1px; }\n"
            + "                .ui-tabs .ui-tabs-nav li a { padding: 0 0 0 3px; margin: 0; }\n"
            + "                .ui-tabs .ui-tabs-nav li .ui-icon { margin: 0; }\n"
            + "                .ui-tabs .ui-tabs-panel { padding: 3px; }\n"
            + "                .ui-datepicker { z-index: 9999 !important; }\n"
            + "                .wcSplitterBar { border: 1px solid #777 !important; background-color: #777 !important; z-index: 0 !important }\n"
            + "                .wcFrameButton { z-index: 0 !important; }\n"
            + "                .wcPanelBackground { overflow: hidden !important }\n"
            + "                .tftable { border-collapse: collapse; }\n"
            + "                .tftable > tbody > tr > td, .tftable > tbody > tr > th { border: 1px solid #8c8c8c; padding: 3px; white-space: nowrap; }\n"
            + "                .tfgrid { border-collapse: collapse; }\n"
            + "                .tfgrid > tbody > tr > td { border: 0px; padding: 2px; white-space: nowrap; }\n"
            + "                .myselect { padding-right:16px; -webkit-appearance: none; -moz-appearance: none; appearance: none; font-size: WACHOS_FONTSIZEpx !important; font-family: WACHOS_FONT !important; }\n"
            + "                select::-ms-expand { display:none; }\n"
            + "                .qtip { max-width: 600px !important; }\n"
            + "                .tftree { overflow:auto; border:1px solid silver; min-height:100px; }\n"
            + "                .ui-widget.context-menu-hover { color: white; background: gray; }\n"
            + "                .ui-slider .ui-slider-handle { z-index: 0; }\n"
            + "                .ui-slider .ui-slider-range { z-index: 0; }\n"
            + "                .tfsplash .ui-dialog-titlebar { display:none }\n"
            + "                input[type=\"color\"] { -webkit-appearance: none;	border: none; }\n"
            + "                input[type=\"color\"]::-webkit-color-swatch-wrapper { padding: 0; }\n"
            + "                input[type=\"color\"]::-webkit-color-swatch { border: none; }\n"
            + "                .hljs { font-size: WACHOS_FONTSIZEpx }\n"
            + "                .ql-container { min-height: 10rem; height: 100%; flex: 1; display: flex; flex-direction: column; }\n"
            + "                .ql-editor { height: 100%; flex: 1; overflow-y: auto; width: 100%; }\n"
            + "                .ql-formats { margin-right: 5px !important }\n"
            + "                ::-webkit-scrollbar-track-piece  { background-color: #CECECE; }\n"
            + "                ::-webkit-scrollbar-track { box-shadow: inset 0 0 5px grey !important; border-radius: 50px !important; }\n"
            + "                ::-webkit-scrollbar-thumb { border-radius: 50px !important; }\n"
            + "            </style>\n"
            + "            <script>\n"
            + "                //\n"
            + "                const tfSortableTreeNodes = [];\n"
            + "\n"
            + "                //set Chart defaults\n"
            + "                Chart.defaults.animation = false;\n"
            + "                Chart.defaults.plugins.legend.display = false;\n"
            + "                Chart.defaults.backgroundColor = 'white';\n"
            + "                Chart.defaults.maintainAspectRatio = false;\n"
            + "\n"
            + "                //keep track of where the mouse is\n"
            + "                var tfMouseEvent;\n"
            + "                $(document).mousemove(function (e) {\n"
            + "                    tfMouseEvent = e;\n"
            + "                }).mouseover();\n"
            + "\n"
            + "                //make dialogs draggable to off the screen\n"
            + "                if (!$jq.ui.dialog.prototype._makeDraggableBase) {\n"
            + "                    $jq.ui.dialog.prototype._makeDraggableBase = $jq.ui.dialog.prototype._makeDraggable;\n"
            + "                    $jq.ui.dialog.prototype._makeDraggable = function () {\n"
            + "                        this._makeDraggableBase();\n"
            + "                        this.uiDialog.draggable(\"option\", \"containment\", false);\n"
            + "                    };\n"
            + "                }\n"
            + "\n"
            + "                //throttle events that you don't want called too frequently\n"
            + "                var tfThrottling = false;\n"
            + "                function tfThrottle(func, limit) {\n"
            + "                    if (!tfThrottling) {\n"
            + "                        tfThrottling = true;\n"
            + "                        setTimeout(function () {\n"
            + "                            func();\n"
            + "                            tfThrottling = false;\n"
            + "                        }, limit);\n"
            + "                    }\n"
            + "                }\n"
            + "\n"
            + "                //manage text file downloading\n"
            + "                var tfDownloadFileName = \"file.txt\";\n"
            + "                var tfDownloadContent = \"The content of the file to be downloaded\";\n"
            + "\n"
            + "                function tfDownload(mime_type) {\n"
            + "                    mime_type = mime_type || \"text/plain\";\n"
            + "                    var blob = new Blob([tfDownloadContent], {type: mime_type});\n"
            + "                    var dlink = document.createElement('a');\n"
            + "                    dlink.download = tfDownloadFileName;\n"
            + "                    dlink.href = window.URL.createObjectURL(blob);\n"
            + "                    dlink.onclick = function (e) {\n"
            + "                        // revokeObjectURL needs a delay to work properly\n"
            + "                        var that = this;\n"
            + "                        setTimeout(function () {\n"
            + "                            window.URL.revokeObjectURL(that.href);\n"
            + "                        }, 1500);\n"
            + "                    };\n"
            + "\n"
            + "                    dlink.click();\n"
            + "                    dlink.remove();\n"
            + "                }\n"
            + "                \n"
            + "                function changeTheme(filename,id) {\n"
            + "                    var elem=document.createElement(\"link\");\n"
            + "                    elem.id=id;\n"
            + "                    elem.rel=\"stylesheet\";\n"
            + "                    elem.type=\"text/css\";\n"
            + "                    elem.href=filename;\n"
            + "                    return elem;\n"
            + "                }\n"
            + "            </script>\n"
            + "            <script type=\"text/javascript\">if (window.PrimeFaces){PrimeFaces.settings.locale = 'en_US'; PrimeFaces.settings.projectStage = 'Development'; }</script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wcdocker/font-awesome.min.css\">\n"
            + "            <script src=\"wjs/FileSaver.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wcdocker/wcDocker.min.css\">\n"
            + "            <script src=\"wcdocker/wcDocker.min.js\"></script>\n"
            + "            <link rel=\"stylesheet\" type=\"text/css\" href=\"wjs/toastr.min.css\">\n"
            + "            <script src=\"wjs/toastr.min.js\"></script><style>\n"
            + "            .toast {\n"
            + "                opacity: 1 !important;\n"
            + "                font-family: WACHOS_FONT;\n"
            + "            }\n"
            + "        </style></head>\n"
            + "        \n"
            + "        <body style=\"padding:0px; margin:0px; border: 0px\" class=\"ui-widget-content\"><div id=\"dialogsContainerSESSIONHASHCODE\"></div><input type=\"hidden\" id=\"j_idt2:j_idt6\">\n"
            + "<form id=\"j_idt2:j_idt7\" name=\"j_idt2:j_idt7\" method=\"post\" action=\"/HelloWachos/faces/index.xhtml;jsessionid=so64Wf1914UUFPYqz0TmlWAKsErEpghyAskTsJvl.dg0220wkv8880\" enctype=\"multipart/form-data\">\n"
            + "<input type=\"hidden\" name=\"j_idt2:j_idt7\" value=\"j_idt2:j_idt7\">\n"
            + "<input id=\"j_idt2:j_idt7:fileLoader\" type=\"file\" name=\"j_idt2:j_idt7:fileLoader\" class=\"fileLoader\" style=\"display:none\" onchange=\"mojarra.ab(this,event,'valueChange',0,'@form')\"><input type=\"hidden\" name=\"jakarta.faces.ViewState\" id=\"j_id1:jakarta.faces.ViewState:0\" value=\"-8784956415861981948:-1811891635474972722\" autocomplete=\"off\">\n"
            + "</form>\n"
            + "        <script>\n"
            + "            toastr.options.positionClass = 'toast-bottom-right';\n"
            + "            toastr.options.progressBar = false;\n"
            + "            toastr.options.preventDuplicates = true;\n"
            + "        </script>\n"
            + "<form id=\"j_idt10\" name=\"j_idt10\" method=\"post\" action=\"/HelloWachos/faces/index.xhtml;jsessionid=so64Wf1914UUFPYqz0TmlWAKsErEpghyAskTsJvl.dg0220wkv8880\" enctype=\"application/x-www-form-urlencoded\" style=\"height:100%; overflow: auto\">\n"
            + "<input type=\"hidden\" name=\"j_idt10\" value=\"j_idt10\">\n"
            + "<span id=\"j_idt10:j_idt11:j_idt12\"><script>var changedlayout1347486668=function(o){var o=(typeof o==='object')&&o?o:{};mojarra.ab('j_idt10:j_idt11:j_idt12',null,'action',0,0,{'params':o})}</script></span><span id=\"j_idt10:j_idt11:layout1347486668\" style=\"display:block; height:100%\"><div id=\"layout1347486668\" style=\"height: 100%; width: 100%; padding: 0px; box-sizing: border-box\"><table id=\"tablelayout1347486668\" class=\"tfgrid\" style=\"\"><tbody><tr><td style=\"padding:0 0  0 0;\"><span id=\"vc1161076130\" class=\"ui-widget\" style=\"\" onclick=\"changedlayout1347486668({id: 'vc1161076130', value: '#buttonclick'});\">Hello, World!</span></td></tr></tbody></table></div></span><input type=\"hidden\" name=\"jakarta.faces.ViewState\" id=\"j_id1:jakarta.faces.ViewState:1\" value=\"-8784956415861981948:-1811891635474972722\" autocomplete=\"off\">\n"
            + "</form>\n"
            + "<div id=\"textarea_simulator\" style=\"position: absolute; top: 0px; left: 0px; visibility: hidden;\"></div></body></html>")
            .replaceFirst("<div id=\"layout.*div>", "#REPLACE_WITH_COMPONENT")
            .replaceFirst("<script>var changedlayout.*script>", "#JCEF_CALLBACK");
}
