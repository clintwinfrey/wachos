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

import gov.mil.navy.nswcdd.wachos.components.Spacer;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.CheckBox;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.components.text.Quill;
import gov.mil.navy.nswcdd.wachos.tools.WSession;

public class QuillTutorial {

    public static Tutorial create(WSession session) {

        //create an instance of the Quill widget
        Quill quill = new Quill();

        //create the layout
        VBox layout = new VBox();

        //add an HBox to our layout; this HBox has a checkbox and a button to toggle editing and populate content
        CheckBox cb = new CheckBox(true);
        cb.valueChangedListeners.add(action -> quill.setEnabled(cb.isSelected()));
        layout.add(new HBox(cb, new Label("Enable Editing"), new Spacer("20px", "0px"),
                new Button("Homerfy", action -> quill.setText("{\"ops\":[{\"attributes\":{\"size\":\"large\",\""
                + "bold\":true},\"insert\":\"Homer Simpson\"},{\"insert\":\"\\n\"},{\"attributes\":{\"alt\":\"homer si"
                + "mpson icon from www.iconarchive.com\"},\"insert\":{\"image\":\"data:image/png;base64,iVBORw"
                + "0KGgoAAAANSUhEUgAAAFwAAABcCAMAAADUMSJqAAABYlBMVEX////+2B/UsH7Qni3d1tP/3B//2h7OnC3SoC3/3h/7+/zWoi339/f/4R/g2db/5SDn5+jAwMHv8PH61CGYmJqvr7H1zSPf3+CgoaWkpKNwcHTY2NjitCnIlyXqvSjXoyh2dnbMzM8AAAB4c23Gn3KofxrtxCV8cWCEhIW1uL+OjY19foNsbHWDhY1OUVhdX2R8a1SWgjW3pBfGrQynlyGAdUBBQkxLRTpoTg/aqiXw1BiCfXiFYw2Lah/t2FCDfFM/P0CBYR/64mNJNQD/63iEgXDOx4FgVUOBfmH/5E/x5Ib/8oaWkXL/4jjr3orixA6figBxakKAcABlWgadk1B3ZTebdBpzXCWiggB/chq2hhOeey9aTiG4mh1ZRBuLbTZkXlZBMhC9tXeYfhmThxcsIhHaz1FiVja8s02lmkKwoldpUDzev5aijnVMPyONbVROQwChmWusi2Q+MybOxGwgFQ7g7LQSAAAInUlEQVRoge2Z6V/a2BrHE4GQkASTkJiwhHAGoyFMhaIdMUodHRVpS+NSYcDlUmvLvTO2Ve7M/3/PSaCExQXLvLmf/t5AkXzz8Ow5xbAf+qEf+v+QwAmhfwDL8IoSNRVJEfmps7msIpsxMSuEOEWZMjsUi2O8IcjizxkePMtMF84rmLCUyz8vLC8vr7z4Zbq2Z+XMatG3FglAEZS1kgbTY4di6xt6iZ4jcFezLzelqcG5X3Va30oG8J6I7f1pJQ2T/k3zBdUI3hexsytPBy7VVNLnS+BeVffE6cDLiB1MEl44dIwwDTa/QfugFgIDplcupxLTZ8jwEXj1VXYKbCGt+Rz4gFtw/PXuFHoYtzgWTuy8mUIldeEooESACri3gO+qb3e574Z33RJMwLqHrcWmCJwK2PBNdS/63fDQOu3C2YPDKC/tHlmz9vGuJK033k0hX8QmyhaSPHF9zKUPUk4oudwUWgCoOaaf9OLHLXUrX4hNQBF4flyIhDqEk3q/3CV++M2AZFGMD37CACCd1H5vjPt6C1aRdhLHBNE1OW46L2Y0Pq6/SMVmcyMKPHkqpE9XzmpahH0+JnmjOqlpbQaL/+TeWjbugYO6Ri+cX5z+S2S6n3CtBEtZDXWOtTZH6i4UL9EX6gn0WPfrwGQ4IEErxFFLhJyu28t1ei15bnYvN3XYUCm7qB+cHw9fwJjgRH9fqvXHspQz39xebka51Ch7fevDVeO4uYazsw3HiSB9fKMvQPjH07XCQWtoCjCxn/OlWlFN91osd/jpZaVSeb2aG4oblKGvBQJW4zTC2sfFMvSifGLNso3Sgl1MUFSAfZliBr4vZMXV39RSrZl2U0nOoyLFCbZ9ODKM5BMNDlqCOlouFIOJxBGPKcsU7BVH+vF5ACcIAn8xeA2T5XIfNXVLUzefZUDm2TubxSF97vfGv0dcnjlNLNsEHrDPtoLJSMDaxxbVOUi1bkoRgqh+sqj/DF1jCJmSBot0jbUKKyu2vaBC6fVW+3AErugJ+zhAEDttPRmgbOs5Vm4WrQDOXn2A7L/OrOofQ9cAIXPhg3CamrVXGu0/HeU7M9efU8NOh3C28Hx7++0XLULZZ/Y7bF392LACbOHm5au9th0YgWPSi6sLnfTR7MqXr515VzNI12keiF4nis0EThXa+cOLBLXcuKlsYrxeKtaSyeLBcf3GIvDq5VBpgCPLZtd8Pu3qT5fZ13V5//NqP/7KBtwRqPMDFvbNwnLD/iRiXF4rFYsXtSRlNyw4CF4NjRhRLb3/oPno2uowe2Z+/vp6sWd6SNkgYdePNKCFlF0o2JVbgPYSLajCgOH2UcQZvbsDdKVZq+kkqb4fYc/MdOZnWj22WKLRMLQKEXdUVS6dhdXQaZQNERsmJVTg1b6XLrSbPp9K176Owjt/z3zuTgzGhJ05iFY+Z0sgqq/3TKeTCK0mvKk2h3cHZPXVopcOWkUY0K3OKPzrT/Mpt3AZaCCcVp7NY7PXuYQY+lN/pyJ2BhdN4YQO1jujXoFZ4+7qXLlJexeE6luPeQxa2ciBxWHg+aHtC56NwDud62vXCJBXaR/ZX4OJnVvvfAV1Z8B7dsE3iqf55lXfqOXzX1qLTtpKdWRa325ie08c6NwKinWyDye2L8X+qrmo0qWRgHaiDPqGYGxoZJCc69td2VsfbH6hlOM1z4Jf3UtxfTjZ/DIE7xgOgWuheMFs6Ovt4nC/DLXgChFc8Pjdk5IQTtfKg2wn00JSW0Vs3LPsvR6z6MXbcLUiPXQ88M3xEO6jtzz0+Tz0tiABswR7WnAt4o3W2OWa30ILypzHBqLyR0o0BBRQkiTp83anh/4vNI5JGUvv1YH0RpdcPhvD7gaV9NJx1q7/muKxQ5oOquqCfXTztfM1f3b0134IThGJ4zKtEp0cMGfPYMbC4aB26N41n7JuQEr5Ajtbu7y4v7m/eoSW0Z28KMbMsN8f9psblicLKm/Td+2+bsoMesZOh+RYDPjDYecrgnLFwlGTV3gefgjpDDi0e3QCVubde7WwiOi0J6+IgoQx/hwf9rtXZQWxQOHbm7yJSdGwX4Y+iH+jV4fb9aBktLv5+g/LOIVmPCPFwn7HlXIKPpbaxPZtTMK4WNi9Iahbjh1wFNx/6CCXXXqPvewUOGNGXQ46bpHbVOU2B3Mo1nMBeME67NuHNnbQRnStaztV724rS+5rDBU8uKoeSnBbU761zswBixOvbx8+zgBlEtJJpy6og54tioSFQtATzj+iNzxmAsyziK7DDBoevnfb7nQLwt79tqvGFFMUs65tDIBpngOS8e0a+fnOpfkINizVNhp7sF/MvugHX+RlKE+BSCnJ05/MW3N88YwoXkZVTUcOPMHnh58LhKyXBpYeh0b0PMx3+oP3h8ZHojWwnnMTPK1zcGbXBrqEMLIwx1PeO03yaGeki1n5gUfkWH9UiRMdvxg5098r+buk9PI0ZEqTnTJkpTDqevelQPfJS4AtbCI0vEREpvv99x0IoYcQTjGkJ5wwxGMiND0cvvMXx1O8lE2Bp51HhYBhRiWeB3ecxIGlX/jvOOkKyeCXmGHc5VJhsiQZES9y4Xusk+6YmI+SDKdE+N6bG/f99V4xBt8dQXdKerJnFOkBw6GMJ57p8GL4/kRHEp7mdpB6qEYdxZ/idsZAi8kjqs98wgmjIYUfB+eMiQ9HM2hd8/sfc93wodaDErII/WCuOOInTUcx6hj+qIbH5CZjMznH8AcqqCdzMr+ITjQfC58sG7mcy35UPKEmORzFeDdV/N5JIXAyAHwGCnZ4bvAnxSbp6wrfh4cEAUipVrl9Vt8qnZ6e1k5LW/WzdjktApnp3tyY4H92mCxwvcKF4pKRL27UmloiuTAXISgotnEemFtIrDU3imUz42AnyXSY5GEkv5Fr6yqpJS2KnUVi0Qv7d8NyXqlIUtXL6HxRGQ//HxjIOPIOd0fmAAA"
                + "AAElFTkSuQmCC\"}},{\"insert\":\"\\nThis is a list about Homer Simpson.  He is known for the"
                + " following:\\nGood at nuclear power plants\"},{\"attributes\":{\"list\":\"bullet\"},\"inser"
                + "t\":\"\\n\"},{\"insert\":\"Married to Marge\"},{\"attributes\":{\"list\":\"bullet\"},\"inse"
                + "rt\":\"\\n\"},{\"insert\":\"Has three children\"},{\"attributes\":{\"list\":\"bullet\"},\"i"
                + "nsert\":\"\\n\"}]}"))));

        //add quill to the layout, with height=500px and width=1000px
        layout.add(quill.setHeight("500px").setWidth("1000px"));

        String code = "        //create an instance of the Quill widget\n"
                + "        Quill quill = new Quill();\n"
                + "        \n"
                + "        //create the layout\n"
                + "        VBox layout = new VBox();\n"
                + "        \n"
                + "        //add an HBox to our layout; this HBox has a checkbox and a button to toggle editing and populate content\n"
                + "        CheckBox cb = new CheckBox(true);\n"
                + "        cb.valueChangedListeners.add(action -> quill.setEnabled(cb.isSelected()));\n"
                + "        layout.add(new HBox(cb, new Label(\"Enable Editing\"), new Spacer(\"20px\", \"0px\"),\n"
                + "                new Button(\"Homerfy\", action -> quill.setText(\"[Quill-compatible content on Homer]\"))));\n"
                + "\n"
                + "        //add quill to the layout, with height=500px and width=1000px\n"
                + "        layout.add(quill.setHeight(\"500px\").setWidth(\"1000px\"));";

        return new Tutorial("Quill", "gov/mil/navy/nswcdd/wachos/components/Quill.html",
                layout, code);
    }
}
