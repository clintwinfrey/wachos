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

import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.ProgressBar;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTimer;

public class ProgressBarTutorial {

    public static Tutorial create(WSession session) {
        ProgressBar progress = new ProgressBar().setWidth("100px"); //the progress bar
        Button startButton = new Button("Start");
        startButton.clickListeners.add(action -> { //a button that increases the progress bar on a timer
            startButton.setEnabled(false); //disable the button until progress is complete
            progress.setValue(0); //start it at 0
            WTimer timer = new WTimer(session, 30); //create a timer that increases the progress bar
            timer.setTask(() -> {
                progress.setValue(progress.getIntValue() + 1); //increase progress by 1
                if (progress.getIntValue() >= 100) { //stop once we reach 100
                    timer.stop(); //stop the timer
                    startButton.setEnabled(true); //re-enable the button
                }
            });
            timer.start(); //start the timer so it increases the slider by 1, every 30 milliseconds
        });

        String code = "        ProgressBar progress = new ProgressBar().setWidth(\"100px\"); //the progress bar\n"
                + "        Button startButton = new Button(\"Start\");\n"
                + "        startButton.clickListeners.add(action -> { //a button that increases the progress bar on a timer\n"
                + "            startButton.setEnabled(false); //disable the button until progress is complete\n"
                + "            progress.setValue(0); //start it at 0\n"
                + "            WTimer timer = new WTimer(session, 30); //create a timer that increases the progress bar\n"
                + "            timer.setTask(() -> {\n"
                + "                progress.setValue(progress.getIntValue() + 1); //increase progress by 1\n"
                + "                if (progress.getIntValue() >= 100) { //stop once we reach 100\n"
                + "                    timer.stop(); //stop the timer\n"
                + "                    startButton.setEnabled(true); //re-enable the button\n"
                + "                }\n"
                + "            });\n"
                + "            timer.start(); //start the timer so it increases the slider by 1, every 30 milliseconds\n"
                + "        });";

        return new Tutorial("ProgressBar", "gov/mil/navy/nswcdd/wachos/components/text/ProgressBar.html", new HBox(startButton, progress), code);
    }
}
