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
package gov.mil.navy.nswcdd.wachos.tools;

import java.util.Timer;
import java.util.TimerTask;

/**
 * WTimer allows you to execute a task on a given time interval
 */
public class WTimer {

    /**
     * the user session this timer belongs to
     */
    private WSession session;
    /**
     * the thing to do on the delay interval
     */
    private WTimerTask task;
    /**
     * the interval at which execution occurs
     */
    private long delay;
    /**
     * flag indicating if this timer is running
     */
    private boolean running = false;
    /**
     * this is really what's doing the timing
     */
    private Timer timer;

    /**
     * Constructor
     *
     * @param session the user session this belongs to
     * @param delay the interval at which to execute
     */
    public WTimer(WSession session, long delay) {
        this.session = session;
        this.delay = delay;
    }

    /**
     * Sets the task to be done on a timer
     *
     * @param task execute this on a timed interval
     */
    public void setTask(WTimerTask task) {
        this.task = task;
    }

    /**
     * Sets the execution interval
     *
     * @param delay the execution interval
     */
    public void setDelay(long delay) {
        this.delay = delay;
        if (running) {
            stop();
            start();
        }
    }

    /**
     * Starts the timer
     */
    public void start() {
        running = true;
        if (timer != null) {
            return;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (session.isValid()) {
                    try {
                        task.execute();
                    } catch (Exception e) {
                        stop();
                        task = null;
                        session = null;
                    }
                } else {
                    stop();
                    task = null;
                    session = null;
                }
            }
        }, 0, delay);
    }

    /**
     * Stops the timer
     */
    public void stop() {
        running = false;
        if (timer == null) {
            return;
        }
        timer.cancel();
        timer = null;
    }

    /**
     * @return a flag indicating if this timer is currently running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * WTimerTask is a task that is executed in a WTimer
     */
    public static interface WTimerTask {

        /**
         * Called on a delay interval from within the WTimer
         */
        public void execute();
    }

}
