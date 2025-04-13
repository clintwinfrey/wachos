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

import gov.mil.navy.nswcdd.wachos.components.Carousel;
import gov.mil.navy.nswcdd.wachos.components.TreeView;
import gov.mil.navy.nswcdd.wachos.components.TreeView.TreeNode;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.TextField;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.Color;
import java.util.Arrays;

public class CarouselTutorial {

    public static Tutorial create(WSession session) {
        TreeView tree = new TreeView(); //this is the component we're trying to demonstrate in this tutorial, but we need it so we can navigate the carousel
        tree.setWidth("200px");
        HBox content = new HBox(); //this is where associated information will be displayed when a node is selected

        //create a Carousel
        TreeNode node1 = new TreeNode("Node 1"); //the parent node for the TreeNodes in carousel1
        Carousel carousel1 = new Carousel(tree, node1, Arrays.asList("Red", "Orange", "Yellow", "Green", "Blue", "Magenta")) {
            @Override
            public void addButtonPressed(String selection) { //this was called, so create a node and associated layout; then, add it as an entry
                TreeNode node = new TreeNode("");
                Color color = Arrays.asList(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA).get(addOptions.indexOf(selection));
                HBox layout = createColor(node, selection, color);
                addEntry(node, layout);
            }
        };

        //let's demonstrate that you can add a TreeNode/Layout pair whenever you want
        TreeNode externallyAdded = new TreeNode("");
        carousel1.addEntry(externallyAdded, createColor(externallyAdded, "Pink", Color.PINK));

        //create another Carousel, demonstrating that you can have multiple Carousels in a tree
        TreeNode node2 = new TreeNode("Node 2");
        Carousel carousel2 = new Carousel(tree, node2, Arrays.asList("Black", "Dark Gray", "Gray", "Light Gray", "White")) {
            @Override
            public void addButtonPressed(String selection) {
                TreeNode node = new TreeNode("");
                Color color = Arrays.asList(Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE).get(addOptions.indexOf(selection));
                HBox layout = createColor(node, selection, color);
                addEntry(node, layout);
            }
        };

        //add the parent nodes that our Carousels are attached to
        tree.getRoot().addChild(node1, false);
        tree.getRoot().addChild(node2, false);
        
        String code = "        TreeView tree = new TreeView(); //this is the component we're trying to demonstrate in this tutorial, but we need it so we can navigate the carousel\n"
                + "        tree.setWidth(\"200px\");\n"
                + "        HBox content = new HBox(); //this is where associated information will be displayed when a node is selected\n"
                + "        \n"
                + "        //create a Carousel\n"
                + "        TreeNode node1 = new TreeNode(\"Node 1\"); //the parent node for the TreeNodes in carousel1\n"
                + "        Carousel carousel1 = new Carousel(tree, node1, Arrays.asList(\"Red\", \"Orange\", \"Yellow\", \"Green\", \"Blue\", \"Magenta\")) {\n"
                + "            @Override\n"
                + "            public void addButtonPressed(String selection) { //this was called, so create a node and associated layout; then, add it as an entry\n"
                + "                TreeNode node = new TreeNode(\"\");\n"
                + "                Color color = Arrays.asList(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA).get(addOptions.indexOf(selection));\n"
                + "                HBox layout = createColor(node, selection, color);\n"
                + "                addEntry(node, layout);\n"
                + "            }\n"
                + "        };\n"
                + "        \n"
                + "        //let's demonstrate that you can add a TreeNode/Layout pair whenever you want\n"
                + "        TreeNode externallyAdded = new TreeNode(\"\");\n"
                + "        carousel1.addEntry(externallyAdded, createColor(externallyAdded, \"Pink\", Color.PINK));\n"
                + "        \n"
                + "        //create another Carousel, demonstrating that you can have multiple Carousels in a tree\n"
                + "        TreeNode node2 = new TreeNode(\"Node 2\");\n"
                + "        Carousel carousel2 = new Carousel(tree, node2, Arrays.asList(\"Black\", \"Dark Gray\", \"Gray\", \"Light Gray\", \"White\")) {\n"
                + "            @Override\n"
                + "            public void addButtonPressed(String selection) {\n"
                + "                TreeNode node = new TreeNode(\"\");\n"
                + "                Color color = Arrays.asList(Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE).get(addOptions.indexOf(selection));\n"
                + "                HBox layout = createColor(node, selection, color);\n"
                + "                addEntry(node, layout);\n"
                + "            }\n"
                + "        };\n"
                + "        \n"
                + "        //add the parent nodes that our Carousels are attached to\n"
                + "        tree.getRoot().addChild(node1, false);\n"
                + "        tree.getRoot().addChild(node2, false);";

        content.add(tree, new VBox(carousel1, carousel2));
        return new Tutorial("Carousel", "gov/mil/navy/nswcdd/wachos/components/Carousel.html",
                content, code);
    }

    private static HBox createColor(TreeNode node, String title, Color color) {
        HBox ret = new HBox();
        node.setText(title);
        ret.titleListeners.add(newTitle -> node.setText(newTitle));
        ret.setTitle(title).setBackground(color).setHeight("50px").setWidth("300px");
        ret.add(new TextField(title, text -> ret.setTitle(text)));
        return ret;
    }

}
