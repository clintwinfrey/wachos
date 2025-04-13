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

import gov.mil.navy.nswcdd.wachos.components.text.DatePicker;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import java.util.Date;

public class DatePickerTutorial {

    public static Tutorial create(WSession session) {

        //create a DatePicker with today's date, and toast whenever the date is changed
        DatePicker datePicker = new DatePicker(new Date(), value -> session.postSuccess("Date Changed", "Date is now set to " + value));

        //add another listener to this datePicker so you can see that it knows the current date
        datePicker.valueChangedListeners.add(value -> session.exec("console.log('" + datePicker.getDate() + "');"));

        String code = "        //create a DatePicker with today's date, and toast whenever the date is changed\n"
                + "        DatePicker datePicker = new DatePicker(new Date(), value -> session.postSuccess(\"Date Changed\", \"Date is now set to \" + value));\n"
                + "\n"
                + "        //add another listener to this datePicker so you can see that it knows the current date\n"
                + "        datePicker.valueChangedListeners.add(value -> session.exec(\"console.log('\" + datePicker.getDate() + \"');\"));";

        return new Tutorial("DatePicker", "gov/mil/navy/nswcdd/wachos/components/text/DatePicker.html", datePicker, code);
    }
}
