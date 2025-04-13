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
package tutorial;

import gov.mil.navy.nswcdd.wachos.components.TreeView;
import gov.mil.navy.nswcdd.wachos.components.TreeView.TreeNode;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.Quill;
import gov.mil.navy.nswcdd.wachos.components.text.TextField;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.HashMap;
import java.util.Map;

public class TreeViewTutorial {

    HBox layout = new HBox(); //this is the layout we're going to be populating for the tutorial
    TreeNode selectedNode; //this is the node that is currently selected

    public TreeViewTutorial() {
        Map<TreeNode, String> nodeContentMap = new HashMap<>(); //keep a map of the nodes and their associated content
        TreeView tree = new TreeView(); //this is the component we're trying to demonstrate in this tutorial
        VBox content = new VBox(); //this is where associated information will be displayed when a node is selected
        tree.selectionListeners.add(nodeId -> { //add a listener for whenever a node is selected
            content.removeAll(); //clear everything out from the previous selection
            selectedNode = tree.getNode(nodeId); //get the node that was just selected
            if (selectedNode == null) {
                return; //if no node is selected, then return because it must have been a deselection
            }

            //now start adding content; first line allows the user to rename a node
            content.add(new HBox(new Label("Name"), new TextField(selectedNode.getText(), text -> selectedNode.setText(text))).alignCenterH());
            String quillContent = nodeContentMap.get(selectedNode); //get the content we're going to display in the selected node
            if (quillContent == null) { //hasn't been created yet?
                quillContent = ""; //it's empty string
                nodeContentMap.put(selectedNode, quillContent); //map the selected node to an empty string
            }
            content.add(new Quill(quillContent, text -> nodeContentMap.put(selectedNode, text))); //add a Quill, and update the map when text changes
            content.add(new Button("Delete", action -> { //add a button to delete the selected node
                nodeContentMap.remove(selectedNode); //node is being removed, so remove it from the map
                selectedNode.getParent().removeChild(selectedNode); //remove the node from the tree
                selectedNode = tree.getRoot(); //the old node can't be selected anymore, since we are deleting it
                content.removeAll(); //nothing will be selected after deleting, so clear this out
            }).alignRight()); //put the button to the right
        });

        Button addNode = new Button("Add", action -> { //this is how we add a new node to the tree
            if (selectedNode == null) {
                selectedNode = tree.getRoot(); //nothing selected, add it to root
            }
            selectedNode.addChild(new TreeNode("New Node"), true); //add it to the currently selected node, and mark it as sortable
        });
        layout.alignTop().add(new VBox(addNode, tree.setHeight("300px").setWidth("200px")).alignLeft(), content); //add everything to master layout
    }

    public static Tutorial create(WSession session) {

        String code = "        Map<TreeNode, String> nodeContentMap = new HashMap<>(); //keep a map of the nodes and their associated content\n"
                + "        TreeView tree = new TreeView(); //this is the component we're trying to demonstrate in this tutorial\n"
                + "        VBox content = new VBox(); //this is where associated information will be displayed when a node is selected\n"
                + "        tree.selectionListeners.add(nodeId -> { //add a listener for whenever a node is selected\n"
                + "            content.removeAll(); //clear everything out from the previous selection\n"
                + "            selectedNode = tree.getNode(nodeId); //get the node that was just selected\n"
                + "            if (selectedNode == null) {\n"
                + "                return; //if no node is selected, then return because it must have been a deselection\n"
                + "            }\n"
                + "\n"
                + "            //now start adding content; first line allows the user to rename a node\n"
                + "            content.add(new HBox(new Label(\"Name\"), new TextField(selectedNode.getText(), text -> selectedNode.setText(text))).alignCenterH());\n"
                + "            String quillContent = nodeContentMap.get(selectedNode); //get the content we're going to display in the selected node\n"
                + "            if (quillContent == null) { //hasn't been created yet?\n"
                + "                quillContent = \"\"; //it's empty string\n"
                + "                nodeContentMap.put(selectedNode, quillContent); //map the selected node to an empty string\n"
                + "            }\n"
                + "            content.add(new Quill(quillContent, text -> nodeContentMap.put(selectedNode, text))); //add a Quill, and update the map when text changes\n"
                + "            content.add(new Button(\"Delete\", action -> { //add a button to delete the selected node\n"
                + "                nodeContentMap.remove(selectedNode); //node is being removed, so remove it from the map\n"
                + "                selectedNode.getParent().removeChild(selectedNode); //remove the node from the tree\n"
                + "                selectedNode = tree.getRoot(); //the old node can't be selected anymore, since we are deleting it\n"
                + "                content.removeAll(); //nothing will be selected after deleting, so clear this out\n"
                + "            }).alignRight()); //put the button to the right\n"
                + "        });\n"
                + "\n"
                + "        Button addNode = new Button(\"Add\", action -> { //this is how we add a new node to the tree\n"
                + "            if (selectedNode == null) {\n"
                + "                selectedNode = tree.getRoot(); //nothing selected, add it to root\n"
                + "            }\n"
                + "            selectedNode.addChild(new TreeNode(\"New Node\"), true); //add it to the currently selected node, and mark it as sortable\n"
                + "        });\n"
                + "        layout.alignTop().add(new VBox(addNode, tree.setHeight(\"300px\").setWidth(\"200px\")).alignLeft(), content); //add everything to master layout";

        return new Tutorial("TreeView", "gov/mil/navy/nswcdd/wachos/components/TreeView.html",
                new TreeViewTutorial().layout, code);
    }

}
