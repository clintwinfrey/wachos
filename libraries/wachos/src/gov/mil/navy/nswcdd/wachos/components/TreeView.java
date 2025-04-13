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

import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.ArrayList;
import java.util.List;

/**
 * TreeView is a graphical user interface (GUI) component that displays
 * hierarchical data in a tree structure. The TreeView component allows users to
 * navigate through the hierarchical data by expanding and collapsing nodes.
 */
public class TreeView extends Component {

    /**
     * listen for when a TreeNode has been selected
     */
    public final ComponentListeners selectionListeners = new ComponentListeners();
    /**
     * listen for when TreeNodes are dragged
     */
    public final ComponentListeners dragListeners = new ComponentListeners();
    /**
     * The root node of this tree, which contains all other nodes
     */
    private final TreeNode rootNode = new TreeNode("root");
    /**
     * the ID of the currently selected node
     */
    private String selectedNode = "";

    /**
     * Constructor
     */
    public TreeView() {
        rootNode.treeview = TreeView.this;
    }

    /**
     * Enables socket pushing for this component and all child nodes
     *
     * @param masterId the layout this will ultimately be drawn inside of
     * @param session the user's session
     */
    @Override
    public void init(String masterId, WSession session) {
        super.init(masterId, session);
        rootNode.init(masterId, session);
    }

    /**
     * Fires an event on this Component
     *
     * @param evt the event data
     */
    @Override
    public void fireEvent(String evt) {
        if (!isEnabled()) {
            return;
        }
        if (evt.startsWith("#selected ")) {
            selectedNode = evt.replace("#selected ", "");
            selectionListeners.update(selectedNode);
        } else if (evt.startsWith("#deselected ")) {
            selectedNode = "";
            selectionListeners.update("");
        } else if (evt.startsWith("#dragged ")) {
            String[] strs = evt.split("\\s+");
            TreeNode movedNode = getNode(strs[1]);
            int newIndex = Integer.parseInt(strs[2]);
            TreeNode parent = movedNode.parent;
            parent.children.remove(movedNode);
            parent.children.add(newIndex, movedNode);
            dragListeners.update(evt.replace("#dragged ", ""));
        }
    }

    /**
     * @return the root node, which a user can add to or remove as needed
     */
    public TreeNode getRoot() {
        return rootNode;
    }

    /**
     * Gets the node with the given ID
     *
     * @param id the value of 'getId' for the node we're trying to find
     * @return the TreeNode with the matching ID
     */
    public TreeNode getNode(String id) {
        return getNode(rootNode, id);
    }

    /**
     * Recursively searches for the TreeNode with the given ID
     *
     * @param currentNode the node being searched
     * @param id the ID of the node we're looking for
     * @return the matching node if found; otherwise null
     */
    private TreeNode getNode(TreeNode currentNode, String id) {
        if (currentNode.getId().equals(id)) {
            return currentNode;
        }
        for (int i = 0; i < currentNode.children.size(); i++) {
            TreeNode child = currentNode.children.get(i);
            TreeNode foundById = getNode(child, id);
            if (foundById != null) {
                return foundById;
            }
        }
        return null;
    }

    /**
     * @return all of the Components that exist in the TreeView's nodes
     */
    public List<Component> getNodeComponents() {
        List<Component> components = new ArrayList<>();
        addNodeComponents(rootNode, components);
        return components;
    }

    /**
     * Recursively finds and adds Components found in the TreeNode
     *
     * @param node look through this to find Components
     * @param components add the found Components to this list
     */
    private void addNodeComponents(TreeNode node, List<Component> components) {
        components.add(node.component);
        for (TreeNode child : node.getChildren()) {
            addNodeComponents(child, components);
        }
    }

    /**
     * Gets the currently selected node
     *
     * @return the node that's currently selected
     */
    public TreeNode getSelectedNode() {
        if (selectedNode.equals("")) {
            return null;
        }
        return getNode(selectedNode);
    }

    /**
     * Selects the node in the treeview
     *
     * @param node the node to select
     */
    public void selectNode(TreeNode node) {
        if (node == null || node.treeview == null || !node.treeview.isRendered()) {
            return;
        }
        exec("$jq('#tree" + getId() + "').jstree(true).deselect_all();"
                + "$jq('#tree" + getId() + "').jstree(true).select_node('" + node.getId() + "');");
    }

    /**
     * Filters on the nodes that match the given value
     *
     * @param match must match this String
     */
    public void search(String match) {
        exec("$jq('#tree" + getId() + "').jstree(true).search(\"" + match.replace("\"", "").replace("\n", "") + "\");");
    }

    /**
     * Enables sorting on the children of this node
     *
     * @param sb the script we're adding to
     * @param parent contains nodes that may be sortable, and so the script must
     * account for that
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    private void populateSortablesScript(StringBuilder sb, TreeNode parent) {
        for (TreeNode node : parent.getChildren()) {
            if (node.sortable) {
                sb.append("tfSortableTreeNodes.push('" + node.getId() + "');\n");
            }
            populateSortablesScript(sb, node);
        }
    }

    /**
     * @return the HTML used to represent this Component
     */
    @Override
    public String toHtml() {
        StringBuilder sortablesScript = new StringBuilder();
        populateSortablesScript(sortablesScript, rootNode);

        return "<div id='" + getId() + "'>\n"
                + "<div id='tree" + getId() + "' class='tftree  ui-inputfield ui-corner-all' " + getProperties() + "style='" + getStyle() + "'></div>\n"
                + "<script>\n"
                + sortablesScript.toString()
                + "  $jq('#tree" + getId() + "').on('deselect_node.jstree', function (e, data) {\n"
                + "    changed" + layoutId + "({id: '" + getId() + "', value: '#deselected ' + data.node.id});"
                + "  }).on('select_node.jstree', function (e, data) {\n"
                + "    changed" + layoutId + "({id: '" + getId() + "', value: '#selected ' + data.node.id});"
                + "  }).on('move_node.jstree', function (e, data) {\n"
                + "    changed" + layoutId + "({id: '" + getId() + "', value: '#dragged ' + data.node.id + ' ' + data.position});"
                + "  })\n"
                + "  .jstree({\n"
                + "        'plugins' : ['dnd', 'wholerow', 'search'],\n"
                + "        'core' : {\n"
                + "            'multiple' : false,"
                + "            'themes' : { 'icons': false },"
                + "            'check_callback': function(operation, node, node_parent, node_position, more) {\n"
                + "                if (operation === 'move_node') {\n"
                + "                    return node.parent === node_parent.id && tfSortableTreeNodes.includes(node.id);\n" //this is where you limit whether it can be moved
                + "                }\n"
                + "                return true;\n" //allow all other operations
                + "            },\n"
                + "            'data' : [\n"
                + rootNode.getChildrenStr()
                + "            ]\n"
                + "        }\n"
                + "    });\n"
                + "\n" //handle resizing
                + "    var divElement" + getId() + " = document.getElementById('" + getId() + "').parentElement;\n"
                + "    var resizeObserver" + getId() + " = new ResizeObserver((entries) => {\n"
                + "        if ($jq('#tree" + getId() + "').length) {\n"
                + "            $jq('#tree" + getId() + "').css('height', '0');\n"
                + "            var tableheight = $jq('#" + getId() + "').parent().height() - 10;\n"
                + "            $jq('#tree" + getId() + "').css('height', tableheight).css('width', '250px');\n"
                + "        }\n"
                + "    });\n"
                + "    resizeObserver" + getId() + ".observe(divElement" + getId() + ");\n"
                + "</script>\n"
                + "</div>";
    }

    /**
     * @return a unique ID for this component
     */
    @Override
    public String getId() {
        return "tr" + hashCode();
    }

    /**
     * To be called when this component is no longer needed; disposes rootNode
     */
    @Override
    public void dispose() {
        rootNode.dispose();
    }

    /**
     * The TreeNode class represents a node in a tree structure. It contains
     * data, references to its parent and children nodes, and methods for
     * manipulating the tree structure.
     */
    public static class TreeNode {

        /**
         * the component this belongs to
         */
        TreeView treeview;
        /**
         * the parent node of this node, or null if this node has no parent
         */
        private TreeNode parent;
        /**
         * The list of child nodes of this tree node.
         */
        private final List<TreeNode> children = new ArrayList<>();
        /**
         * the Component that is used to represent this TreeNode; if a String
         * was passed in, then this will be a simple Label
         */
        public final TextComponent component;
        /**
         * this is used to ensure that an identical 'update' doesn't cause
         * unnecessary work
         */
        private String prevUpdate = "";
        /**
         * if true, this node can be dragged/sorted
         */
        public boolean sortable = false;

        /**
         * Constructor
         *
         * @param name the text of the TreeNode; the component in this case will
         * be a Label
         */
        public TreeNode(String name) {
            this.component = new Label(name);
        }

        /**
         * Constructor
         *
         * @param component the content of the TreeNode, where the component is
         * the value passed here.
         *
         * NOTE: if you edit this component directly, you must call update() in
         * order to ensure that the component changes within the TreeView
         * itself.
         */
        public TreeNode(TextComponent component) {
            this.component = component;
        }

        /**
         * Enables socket pushing for this node's component
         *
         * @param masterId the layout this will ultimately be drawn inside of
         * @param session the user's session
         */
        private void init(String masterId, WSession session) {
            component.init(masterId, session);
            for (TreeNode child : children) {
                child.init(masterId, session);
            }
        }

        /**
         * Add the child node
         *
         * @param child the node to add
         * @param sortable flag indicating if this child can be sorted by the
         * user
         */
        public void addChild(TreeNode child, boolean sortable) {
            addChild(children.size(), child, sortable);
        }

        /**
         * Add the child node at the given index
         *
         * @param index the index of the node to remove
         * @param child the node to add
         * @param sortable flag indicating if this child can be sorted by the
         * user
         */
        public void addChild(int index, TreeNode child, boolean sortable) {
            child.init(component.layoutId, component.session);
            child.sortable = sortable;
            children.add(index, child);
            child.parent = this;
            TreeNode node = this;
            while (node != null) {
                if (node.treeview != null) {
                    if (node.treeview.isRendered()) { //we actually have to draw this!  it's already rendered
                        String addSorting = sortable ? "tfSortableTreeNodes.push('" + child.getId() + "');\n" : "";
                        //use null as the parent if the node is supposed to be a root node
                        //for jstree there can be multiple root nodes, but this treeview implementation thinks there is only one
                        //so the first set of children nodes should actually all be root nodes
                        String par = "null";
                        if (node.treeview.getRoot() != this) {
                            par = "'" + this.getId() + "'";
                        }
                        node.treeview.exec(addSorting + "$jq('#tree" + node.treeview.getId() + "').jstree(true).create_node(" + par + ", " + child.toHtml() + ", " + index + ");");
                    }
                    return;
                }
                node = node.parent;
            }
        }

        /**
         * Adds the children to this node
         *
         * @param children children to add to this node
         * @param sortable flag indicating if children can be sorted
         */
        public void addChildren(List<TreeNode> children, boolean sortable) {
            for (int i = 0; i < children.size(); i++) {
                addChild(children.get(i), sortable);
            }
        }

        /**
         * If this is a TextComponent, set the value directly here
         *
         * @param value the node value
         */
        public void setText(String value) {
            if (component instanceof TextComponent && !value.equals(((TextComponent) component).getText())) {
                ((TextComponent) component).setText(value);
                update();
            }
        }

        /**
         * If this is a TextComponent, returns the text of the component;
         * otherwise, returns component.toString
         *
         * @return the display text of the node
         */
        public String getText() {
            if (component instanceof TextComponent) {
                return ((TextComponent) component).getText();
            } else {
                return component.toString();
            }
        }

        /**
         * If you modified the component directly, call this method to ensure
         * the changes are reflected in the tree
         */
        public void update() {
            String update = component.toHtml();
            if (prevUpdate.equals(update)) {
                return;
            }
            prevUpdate = update;
            TreeNode node = this;
            while (node != null) {
                if (node.treeview != null) {
                    if (node.treeview.isRendered()) { //we actually have to draw this!  it's already rendered
                        node.treeview.exec("$jq('#tree" + node.treeview.getId() + "').jstree(true).rename_node('" + this.getId() + "', \"" + update.replace("\"", "\\\"").replace("\n", "\\n") + "\");");
                    }
                    return;
                }
                node = node.parent;
            }
        }

        /**
         * Remove the node at the given index
         *
         * @param index the index of the node to remove
         */
        public void removeChild(int index) {
            removeChild(children.get(index));
        }

        /**
         * Remove the specified node
         *
         * @param child the node to remove
         */
        public void removeChild(TreeNode child) {
            children.remove(child);
            child.dispose();
            TreeNode node = this;
            while (node != null) {
                if (node.treeview != null) {
                    if (node.treeview.isRendered()) { //we actually have to draw this!  it's already rendered
                        node.treeview.exec("$jq('#tree" + node.treeview.getId() + "').jstree(true).delete_node('" + child.getId() + "');");
                    }
                    return;
                }
                node = node.parent;
            }
        }

        /**
         * Removes all children from the node
         */
        public void clearChildren() {
            for (int i = children.size() - 1; i >= 0; i--) {
                removeChild(children.get(i));
            }
        }

        /**
         * Sets the children of this TreeView; if any children exist, they will
         * be removed and replaced with this list
         *
         * @param children the children to use in this view
         */
        @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
        public void setChildren(List<TreeNode> children) {
            WSession session = null;
            StringBuilder sb = new StringBuilder();

            //do the logic to remove existing children
            for (int i = this.children.size() - 1; i >= 0; i--) {//TreeNode child : this.children) {
                TreeNode child = this.children.get(i);
                this.children.remove(child);
                child.dispose();
                TreeNode node = this;
                while (node != null) {
                    if (node.treeview != null) {
                        if (node.treeview.isRendered()) { //we actually have to draw this!  it's already rendered
                            session = node.treeview.session;
                            sb.append("$jq('#tree" + node.treeview.getId() + "').jstree(true).delete_node('" + child.getId() + "');\n");
                        }
                        node = null;
                    } else {
                        node = node.parent;
                    }
                }
            }

            //do the logic to add the list of children
            for (TreeNode child : children) {
                child.init(component.layoutId, component.session);
                this.children.add(child);
                child.parent = this;
                TreeNode node = this;
                while (node != null) {
                    if (node.treeview != null) {
                        if (node.treeview.isRendered()) { //we actually have to draw this!  it's already rendered
                            sb.append("$jq('#tree" + node.treeview.getId() + "').jstree(true).create_node($jq('#" + this.getId() + "')[0], " + child.toHtml() + ", " + (this.children.size() - 1) + ");\n");
                        }
                        node = null;
                    } else {
                        node = node.parent;
                    }
                }
            }

            //execute the code on the client side to remove existing nodes and add the children
            if (session != null) {
                session.exec(sb.toString());
            }
        }

        /**
         * @return the parent that this node belongs to
         */
        public TreeNode getParent() {
            return parent;
        }

        /**
         * @return flag indicating if this node has children
         */
        public boolean hasChildren() {
            return !children.isEmpty();
        }

        /**
         * @return this node's child nodes
         */
        public List<TreeNode> getChildren() {
            return new ArrayList<>(children);
        }

        /**
         * Gets the node at the given index
         *
         * @param index the index of the child to retrieve
         * @return the child node at the given index
         */
        public TreeNode getChild(int index) {
            return children.get(index);
        }

        /**
         * @return a unique ID for this component
         */
        public String getId() {
            return "node" + hashCode();
        }

        /**
         * @return the HTML used to represent this Component
         */
        private String toHtml() {
            return "{" + "'id': '" + getId() + "', 'text': \"" + component.toHtml().replace("\"", "\\\"").replace("\n", "\\n") + "\""
                    + (children.isEmpty() ? "" : ", 'children' : [") + getChildrenStr() + (children.isEmpty() ? "" : "]") + "}";
        }

        /**
         * @return a String representation for a all child nodes
         */
        private String getChildrenStr() {
            StringBuilder childrenStr = new StringBuilder();
            for (int i = 0; i < children.size(); i++) {
                childrenStr.append(children.get(i).toHtml()).append(i == children.size() - 1 ? "" : ", ");
            }
            return childrenStr.toString();
        }

        /**
         * removes references to everything
         */
        public void dispose() {
            for (int i = children.size() - 1; i >= 0; i--) {
                children.get(i).dispose();
            }
            treeview = null;
            parent = null;
            children.clear();
            component.dispose();
        }

    }

}
