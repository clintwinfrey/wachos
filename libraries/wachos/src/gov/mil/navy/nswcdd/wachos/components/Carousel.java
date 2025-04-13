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

import gov.mil.navy.nswcdd.wachos.components.TreeView.TreeNode;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.ComboBox;
import gov.mil.navy.nswcdd.wachos.components.text.ImageButton;
import gov.mil.navy.nswcdd.wachos.components.text.ImageDropButton;
import gov.mil.navy.nswcdd.wachos.components.text.TextComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carousel is kind of like a tabbed pane, except the navigation is done through
 * an associated TreeNode or the navigation header. Carousels are dynamic for
 * the user (items can be added or removed) if the 'addOptions' constructor is
 * used.
 */
public class Carousel extends VBox {

    /**
     * listeners for selection, dragging of tree nodes
     */
    ComponentListener dragListener, selectionListener;
    /**
     * the tree that contains the parentNode we're using
     */
    private final TreeView tree;
    /**
     * flag indicating if this Carousel can grow or shrink via + and trash
     * buttons
     */
    private final boolean canGrow;
    /**
     * all TreeNodes are children of this
     */
    private final TreeNode parentNode;
    /**
     * the options that can be selected in order to add items
     */
    public final List<String> addOptions;
    /**
     * a map of the node, layout pairs
     */
    public final Map<TreeNode, Layout> nodeLayoutMap = new LinkedHashMap<>();
    /**
     * the ComboBox through which a user may select a node
     */
    final ComboBox cardSelector = new ComboBox("", Arrays.asList(""));
    /**
     * moves to the previous node/layout pair
     */
    final ImageButton leftArrow;
    /**
     * moves to the next node/layout pair
     */
    final ImageButton rightArrow;
    /**
     * when a node is selected, the associated layout is placed here
     */
    final HBox content = new HBox();
    /**
     * index of the currently selected node/layout
     */
    int index = 0;
    /**
     * if true, the title is being updated so don't try to change the selection
     */
    boolean updatingTitle = false;
    /**
     * true if a TreeNode is in the process of being dragged
     */
    private boolean dragging = false;
    /**
     * the delete button
     */
    final ImageButton trash;
    /**
     * notifies when there is a change to entry order, including when an item is
     * added or deleted
     */
    public final ComponentListeners carouselChangedListeners = new ComponentListeners();

    /**
     * Constructor that cannot grow or shrink
     *
     * @param tree the tree that will control the carousel's navigation
     * @param parentNode we attach carousel nodes to this node
     */
    public Carousel(TreeView tree, TreeNode parentNode) {
        this(tree, parentNode, new ArrayList<>());
    }

    /**
     * Constructor that can grow and shrink via + and trash buttons
     *
     * @param tree the tree that contains the parentNode we're using
     * @param parentNode all TreeNodes are children of this
     * @param addOptions the options that can be selected in order to add items
     */
    public Carousel(TreeView tree, TreeNode parentNode, List<String> addOptions) {
        super();
        this.tree = tree == null ? new TreeView() : tree;
        this.parentNode = parentNode;
        this.addOptions = addOptions;
        setBorder(parentNode.getText(), true);
        HBox navigation = new HBox();

        if (!addOptions.isEmpty()) {
            add(navigation.alignRight());
        }
        add(content.alignRight());

        //there need to be separate HBoxes for navigation, so we can align the trash to the right
        HBox navigationL = new HBox().alignLeft();
        HBox navigationR = new HBox().alignRight();
        navigation.add(navigationL, navigationR);
        navigation.setWidth("100%");

        //figure out if this tree can grow/shrink
        canGrow = !addOptions.isEmpty();
        if (canGrow) { //if so, we'll add a + button so the user can add new items
            navigationL.add(new ImageDropButton("ui-icon-plus", addOptions, toAdd -> addButtonPressed(toAdd)));
        }

        //update cardSelector when treenode order changes
        dragListener = nodeId -> {
            dragging = true;
            TreeNode selectedNode = tree.getSelectedNode();
            updateCardSelector("orderChanged");
            tree.selectNode(selectedNode);
            dragging = false;
        };
        tree.dragListeners.add(dragListener);

        //update content when selection changes
        selectionListener = nodeId -> { //add a listener for whenever a node is selected
            TreeNode selectedNode = tree.getNode(nodeId); //get the node that was just selected

            while (selectedNode != null && !nodeLayoutMap.containsKey(selectedNode)) {
                selectedNode = selectedNode.getParent();
            }
            if (selectedNode == null) {
                return; //if no node is selected, then return because it must have been a deselection or a selection outside of the carousel
            }
            index = parentNode.getChildren().indexOf(selectedNode);
            if (!dragging) {
                Component currentCard = content.getComponents().isEmpty() ? null : content.getComponents().get(0);
                Component newCard = nodeLayoutMap.get(selectedNode);
                if (currentCard != null && newCard.getId().equals(currentCard.getId())) {
                    return; //same card, nothing to update
                }
                if (!content.getComponents().isEmpty()) { //prevent flickering - set the width and height to what's currently in content
                    exec("$('#" + content.getId() + "').width($('#" + content.getComponents().get(0).getId() + "').width());\n"
                            + "$('#" + content.getId() + "').height($('#" + content.getComponents().get(0).getId() + "').height());\n");
                }
                content.removeAll();
                content.add(newCard);
                exec("$('#" + content.getId() + "').width('');\n" //new content has been added; remove old height and width
                        + "$('#" + content.getId() + "').height('');");
                cardSelector.setIndex(index);
            }
        };
        tree.selectionListeners.add(selectionListener);

        //handle navigation via combo selector
        navigationL.add(cardSelector);
        cardSelector.valueChangedListeners.add(value -> {
            if (!updatingTitle && !cardSelector.getOptions().isEmpty()) {
                index = cardSelector.getIndex();
                tree.selectNode(parentNode.getChildren().get(index));
            }
        });

        //handle navigation via left button
        leftArrow = new ImageButton("ui-icon-arrow-1-w", action -> {
            index = Math.max(0, index - 1);
            cardSelector.setIndex(index);
            //TODO!  the card selector appears to be broken now
        });
        navigationL.add(leftArrow);

        //handle navigation via right button
        rightArrow = new ImageButton("ui-icon-arrow-1-e", action -> {
            index = Math.min(nodeLayoutMap.size() - 1, index + 1);
            cardSelector.setIndex(index);
        });
        navigationL.add(rightArrow);

        //add a little space between the delete button and the rest of the stuff
        navigationL.add(new Spacer("20px", "0px"));

        //handle deleting the current item
        trash = new ImageButton("ui-icon-trash", action -> {
            if (!nodeLayoutMap.isEmpty()) {
                TreeNode node = parentNode.getChild(index);
                removeEntry(node);
            }
        });
        navigationR.add(trash);

        //start with everything invisible, but this will change when an item is added
        cardSelector.setVisible(false);
        leftArrow.setVisible(false);
        rightArrow.setVisible(false);
        trash.setVisible(false);
    }

    /**
     * Updates the dropdown selector so it matches the TreeNodes
     */
    private void updateCardSelector(String changeType) {
        List<String> options = new ArrayList<>();
        for (TreeNode node : parentNode.getChildren()) {
            options.add(node.getText().trim());
        }
        cardSelector.setOptions(options);
        boolean navVisible = !options.isEmpty();
        cardSelector.setVisible(navVisible);
        leftArrow.setVisible(navVisible);
        rightArrow.setVisible(navVisible);
        trash.setVisible(navVisible && canGrow);
        index = cardSelector.getIndex();
        if (!updatingTitle) { //don't notify that a node title has changed
            carouselChangedListeners.update(changeType);
        }
    }

    /**
     * Adds an entry to the Carousel
     *
     * @param node the TreeNode to add
     * @param layout the associated Layout to add
     */
    public void addEntry(TreeNode node, Layout layout) {
        node.treeview = tree;
        if (node.component instanceof TextComponent) {
            ((TextComponent) node.component).valueChangedListeners.add(value -> {
                updatingTitle = true;
                updateCardSelector("nodeNameChange");
                cardSelector.setIndex(index);
                updatingTitle = false;
            });
        }
        int insertIndex = nodeLayoutMap.size();
        nodeLayoutMap.put(node, layout);
        parentNode.addChild(insertIndex, node, canGrow);
        updateCardSelector("itemAdded");
        if (content.getComponents().isEmpty()) {
            content.add(layout);
        }
        tree.selectNode(node);
    }

    /**
     * Removes an entry from the Carousel
     *
     * @param node the TreeNode to remove
     */
    public void removeEntry(TreeNode node) {
        nodeLayoutMap.remove(node);
        parentNode.removeChild(node);
        if (parentNode.getChildren().isEmpty()) {
            content.removeAll();
            index = 0;
        } else {
            index = Math.max(0, index - 1);
            selectionListener.update(parentNode.getChildren().get(index).getId());
        }
        updateCardSelector("itemDeleted");
    }

    /**
     * Removes all entries from the Carousel
     */
    public void clearEntries() {
        while (!nodeLayoutMap.isEmpty()) {
            TreeNode node = parentNode.getChild(0);
            nodeLayoutMap.remove(node);
            parentNode.removeChild(node);
        }
        content.removeAll();
        index = 0;
        updateCardSelector("itemsCleared");
    }

    /**
     * @return flag indicating if this carousel has no entries
     */
    public boolean isEmpty() {
        return parentNode.getChildren().isEmpty();
    }

    /**
     * @return the TreeNodes that navigate to GUIs in this Carousel
     */
    public List<TreeNode> getTreeNodes() {
        return parentNode.getChildren();
    }

    public boolean containsLayout(Layout layout) {
        return nodeLayoutMap.values().contains(layout);
    }

    /**
     * Provides the layout that matches the given node
     *
     * @param treeNode the match for the GUI we're looking for
     * @return the GUI we're looking for
     */
    public Layout getLayout(TreeNode treeNode) {
        while (treeNode != null) {
            Layout ret = nodeLayoutMap.get(treeNode);
            if (ret != null) {
                return ret;
            }
            treeNode = treeNode.getParent();
        }
        return null;
    }

    /**
     * When a selection from addOptions is selected, this gets called so you can
     * create and add a TreeNode/Layout pair
     *
     * @param selection the String that was selected via the + button
     */
    public void addButtonPressed(String selection) {
    }

    @Override
    public void dispose() {
        tree.dragListeners.remove(dragListener);
        tree.selectionListeners.remove(selectionListener);
        TreeNode parent = parentNode.getParent();
        if (parent != null) {
            parent.removeChild(parentNode);
        }
        for (TreeNode tn : nodeLayoutMap.keySet()) {
            tn.dispose();
        }
        for (Layout l : nodeLayoutMap.values()) {
            l.dispose();
        }
        cardSelector.valueChangedListeners.clear();
        carouselChangedListeners.clear();
        super.dispose();
    }

}
