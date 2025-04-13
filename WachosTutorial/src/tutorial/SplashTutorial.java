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

import gov.mil.navy.nswcdd.wachos.components.layout.Splash;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.ProgressBar;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WTimer;

public class SplashTutorial {

    public static Tutorial create(WSession session) {

        VBox splashCreator = new VBox(); //this will contain the 'Create Splash' button
        splashCreator.add(new Button("Create Splash", action -> {
            ProgressBar progress = new ProgressBar().setWidth("100px"); //the splash content; can be any Component
            WTimer timer = new WTimer(session, 30); //create a timer that increases the progress bar
            Splash splash = splashCreator.createSplash(progress); //create a splash
            timer.setTask(() -> {
                progress.setValue(progress.getIntValue() + 1); //increase progress by 1
                if (progress.getIntValue() >= 100) { //if we're at 100,
                    splash.dispose(); //then throw the splash away (we're done splashing)
                }
            });
            timer.start(); //start the splash timer so it increases the slider by 1, every 30 milliseconds
        }));

        String code = "        VBox splashCreator = new VBox(); //this will contain the 'Create Splash' button\n"
                + "        splashCreator.add(new Button(\"Create Splash\", action -> {\n"
                + "            ProgressBar progress = new ProgressBar().setWidth(\"100px\"); //the splash content; can be any Component\n"
                + "            WTimer timer = new WTimer(session, 30); //create a timer that increases the progress bar\n"
                + "            Splash splash = splashCreator.createSplash(progress); //create a splash\n"
                + "            timer.setTask(() -> {\n"
                + "                progress.setValue(progress.getIntValue() + 1); //increase progress by 1\n"
                + "                if (progress.getIntValue() >= 100) { //if we're at 100,\n"
                + "                    splash.dispose(); //then throw the splash away (we're done splashing)\n"
                + "                }\n"
                + "            });\n"
                + "            timer.start(); //start the splash timer so it increases the slider by 1, every 30 milliseconds\n"
                + "        }));";

        return new Tutorial("Splash", "gov/mil/navy/nswcdd/wachos/components/layout/Splash.html", splashCreator, code);
    }
}
