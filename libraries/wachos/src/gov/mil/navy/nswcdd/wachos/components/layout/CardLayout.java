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
import gov.mil.navy.nswcdd.wachos.tools.WTools;

/**
 * A CardLayout treats each component in the container as a card. Only one card
 * is visible at a time, and the container acts as a stack of cards. The first
 * component added to a CardLayout object is the visible component when the
 * container is first displayed.
 */
public class CardLayout extends HBox {

    /**
     * the index of the currently selected card
     */
    private int index = -1;

    /**
     * Constructor
     *
     * @param components the components to show in this HBox
     */
    public CardLayout(Component... components) {
        super(components);
        if (components.length > 0) {
            CardLayout.this.setIndex(0);
        }
    }

    /**
     * Flips to the card at the given index.
     *
     * @param index the index of the card to show
     */
    public void setIndex(int index) {
        this.index = index;
        for (int i = 0; i < components.size(); i++) {
            components.get(i).setVisible(index == i);
        }
        if (isRendered()) {
            redraw();
            WTools.initToolTips(components.get(index), session);
        }
    }

    /**
     * Flips to the first card.
     */
    public void first() {
        setIndex(0);
    }

    /**
     * Flips to the previous card. If the currently visible card is the first
     * one, this method flips to the last card in the layout.
     */
    public void previous() {
        setIndex(index == 0 ? components.size() - 1 : index - 1);
    }

    /**
     * Flips to the next card. If the currently visible card is the last one,
     * this method flips to the first card in the layout.
     */
    public void next() {
        setIndex(index == components.size() - 1 ? 0 : index + 1);
    }

    /**
     * Flips to the last card.
     */
    public void last() {
        setIndex(components.size() - 1);
    }

    /**
     * Flips to the specified card; if this card does not exist, it is added as
     * the last card.
     *
     * @param content the card to select
     */
    public void setCard(Component content) {
        if (!components.contains(content)) {
            components.add(content);
            content.init(layoutId, session);
        }
        setIndex(components.indexOf(content));
    }

    /**
     * Gets the currently selected card.
     *
     * @return the current card
     */
    public Component getCard() {
        return components.get(index);
    }

    /**
     * Makes sure that only the selected index is visible
     *
     * @return the HTML of this CardLayout
     */
    @Override
    public String toHtml() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).setVisible(i == index);
        }
        return super.toHtml();
    }

}
