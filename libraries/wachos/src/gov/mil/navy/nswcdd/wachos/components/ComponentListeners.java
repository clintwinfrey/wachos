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

import java.util.ArrayList;
import java.util.List;

/**
 * ComponentListeners contains a group of listeners and convenience methods to
 * handle them
 */
public class ComponentListeners {

    /**
     * the set of ComponentListeners managed by this object
     */
    private final List<ComponentListener> listeners = new ArrayList<>();

    /**
     * The update method gets called whenever an event occurs
     *
     * @param value the updated value of the thing calling this method
     */
    public void update(String value) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).update(value);
        }
    }

    /**
     * Appends the specified listener to this group of listeners
     *
     * @param listener the listener to add
     */
    public void add(ComponentListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener to this group of listeners
     *
     * @param listener the listener to remove
     */
    public void remove(ComponentListener listener) {
        listeners.remove(listener);
    }

    /**
     * Removes all of the elements from this list
     */
    public void clear() {
        listeners.clear();
    }

}
