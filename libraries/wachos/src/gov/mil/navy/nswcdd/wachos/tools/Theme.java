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

import gov.mil.navy.nswcdd.wachos.components.MenuItem;
import java.util.Arrays;
import java.util.List;

/**
 * JQuery UI Themes allow the GUI's look and feel to be specified
 */
public class Theme {
    
    /**
     * Theme name for a dark after-hours look.
     */
    public static final String AFTERDARK = "afterdark";
    /**
     * Theme name for a warm afternoon style.
     */
    public static final String AFTERNOON = "afternoon";
    /**
     * Theme name for a relaxed after-work atmosphere.
     */
    public static final String AFTERWORK = "afterwork";
    /**
     * Theme name featuring a clean and elegant design.
     */
    public static final String ARISTO = "aristo";
    /**
     * Theme name inspired by the classic black-tie formal style.
     */
    public static final String BLACK_TIE = "black-tie";
    /**
     * Theme name with a modern and bold appearance.
     */
    public static final String BLITZER = "blitzer";
    /**
     * Theme name with a bright and airy blue sky theme.
     */
    public static final String BLUESKY = "bluesky";
    /**
     * Theme name based on the popular Bootstrap framework.
     */
    public static final String BOOTSTRAP = "bootstrap";
    /**
     * Theme name inspired by the Casablanca movie aesthetic.
     */
    public static final String CASABLANCA = "casablanca";
    /**
     * Theme name reflecting a soft and elegant Cupertino design.
     */
    public static final String CUPERTINO = "cupertino";
    /**
     * Theme name featuring a clean and simple design aesthetic.
     */
    public static final String CRUZE = "cruze";
    /**
     * Theme name with a dark and modern design.
     */
    public static final String DARK_HIVE = "dark-hive";
    /**
     * Theme name with a playful and colorful dot pattern.
     */
    public static final String DOT_LUV = "dot-luv";
    /**
     * Theme name inspired by the rich color of eggplants.
     */
    public static final String EGGPLANT = "eggplant";
    /**
     * Theme name inspired by retro video games.
     */
    public static final String EXCITE_BIKE = "excite-bike";
    /**
     * Theme name with a light and refreshing flicker style.
     */
    public static final String FLICK = "flick";
    /**
     * Theme name featuring a glass-like translucent effect.
     */
    public static final String GLASS_X = "glass-x";
    /**
     * Theme name inspired by a classic home style.
     */
    public static final String HOME = "home";
    /**
     * Theme name featuring vibrant colors and a modern aesthetic.
     */
    public static final String HOT_SNEAKS = "hot-sneaks";
    /**
     * Theme name with a humanistic and friendly design.
     */
    public static final String HUMANITY = "humanity";
    /**
     * Theme name inspired by French elegance.
     */
    public static final String LE_FROG = "le-frog";
    /**
     * Theme name featuring a deep and dark midnight aesthetic.
     */
    public static final String MIDNIGHT = "midnight";
    /**
     * Theme name with a minty fresh and cool color palette.
     */
    public static final String MINT_CHOC = "mint-choc";
    /**
     * Theme name reflecting a cloudy and overcast look.
     */
    public static final String OVERCAST = "overcast";
    /**
     * Theme name with a spicy and vibrant pepper grinder style.
     */
    public static final String PEPPER_GRINDER = "pepper-grinder";
    /**
     * Theme name inspired by the Microsoft Redmond aesthetic.
     */
    public static final String REDMOND = "redmond";
    /**
     * Theme name with a playful and whimsical rocket design.
     */
    public static final String ROCKET = "rocket";
    /**
     * Theme name reflecting a simple and clean design.
     */
    public static final String SAM = "sam";
    /**
     * Theme name with a smooth and modern appearance.
     */
    public static final String SMOOTHNESS = "smoothness";
    /**
     * Theme name inspired by a casual street style.
     */
    public static final String SOUTH_STREET = "south-street";
    /**
     * Theme name featuring a fresh start aesthetic.
     */
    public static final String START = "start";
    /**
     * Theme name with a bright and sunny disposition.
     */
    public static final String SUNNY = "sunny";
    /**
     * Theme name with a stylish and fashionable purse design.
     */
    public static final String SWANKY_PURSE = "swanky-purse";
    /**
     * Theme name inspired by a modern and trendy Tron aesthetic.
     */
    public static final String TRONTASTIC = "trontastic";
    /**
     * Theme name reflecting a dark and sleek user interface.
     */
    public static final String UI_DARKNESS = "ui-darkness";
    /**
     * Theme name reflecting a light and airy user interface.
     */
    public static final String UI_LIGHTNESS = "ui-lightness";
    /**
     * Theme name inspired by the classic Star Wars character.
     */
    public static final String VADER = "vader";

    /**
     * all of the theme names
     */
    public static final List<String> THEMES = Arrays.asList(AFTERDARK, AFTERNOON, AFTERWORK, ARISTO, BLACK_TIE, BLITZER, BLUESKY, BOOTSTRAP, CASABLANCA, CUPERTINO, CRUZE, DARK_HIVE, DOT_LUV,
            EGGPLANT, EXCITE_BIKE, FLICK, GLASS_X, HOME, HOT_SNEAKS, HUMANITY, LE_FROG, MIDNIGHT, MINT_CHOC, OVERCAST, PEPPER_GRINDER, REDMOND, ROCKET, SAM, SMOOTHNESS, SOUTH_STREET,
            START, SUNNY, SWANKY_PURSE, TRONTASTIC, UI_DARKNESS, UI_LIGHTNESS, VADER);

    /**
     * Theme can't be instantiated
     */
    private Theme() {
    }

    /**
     * Changes the GUI's look and feel
     *
     * @param session the user's session
     * @param theme changes to this look and feel
     * @param fontSize the pixel size of the fonts once we change the theme
     * @param fontFamily the font family once we change the theme
     */
    public static void change(WSession session, String theme, String fontSize, String fontFamily) {
        session.exec("$(\"#qtheme\").remove();\n"
                + "qelem = changeTheme(\"./themes/" + theme + ".css\",\"qtheme\");\n"
                + "document.getElementsByTagName(\"head\")[0].appendChild(qelem);\n"
                //update the font size and family
                + "$(\"head\").append('<style type=\"text/css\"></style>');\n"
                + "$(\"head\").children(':last').html('.wfontsize { font-size: " + fontSize + "px }\\n'\n"
                + "+ '.wfontfamily { font-family: " + fontFamily + " }\\n'\n"
                + "+ '.ui-selectlistbox-item,.ui-widget,.ui-widget .ui-widget { font-size: " + fontSize + "px; font-family: " + fontFamily + " }\\n'\n"
                + "+ '.myselect { padding-right:16px; -webkit-appearance: none; -moz-appearance: none; appearance: none; font-size: " + fontSize + "px !important; font-family: " + fontFamily + " !important; }\\n'\n"
                + "+ '.hljs { font-size: " + fontSize + "px }');");
        WTools.setCookie("theme", theme); //remember this for next time
    }

    /**
     * Stores this theme as a cookie for the user
     *
     * @param theme the look and feel to be set as the default
     */
    public static void setDefault(String theme) {
        WTools.setCookie("theme", theme);
    }

    /**
     * Gets the theme via cookie, or provides a default one if theme hasn't been
     * set
     *
     * @param defaultIfNotFound if a default value is not found, then this theme
     * is used instead
     * @return the theme that's currently stored in a cookie, or
     * defaultIfNotFound if a cookie has not been set
     */
    public static String getOrDefault(String defaultIfNotFound) {
        String ret = WTools.getCookie("theme");
        return ret.equals("") ? defaultIfNotFound : ret;
    }

    /**
     * Creates a MenuItem that allows the user to select among some nice looking
     * themes
     *
     * @param session the user's session
     * @param fontSize the pixel size of the fonts once we change the theme
     * @param fontFamily the font family once we change the theme
     * @return a Menu that allows the user to select among some nice looking
     * themes
     */
    public static MenuItem createMenu(WSession session, String fontSize, String fontFamily) {
        MenuItem dark = new MenuItem("Dark");
        for (String theme : new String[]{AFTERDARK, CRUZE, DARK_HIVE, EGGPLANT, MIDNIGHT, TRONTASTIC, UI_DARKNESS, VADER}) {
            dark.addChild(new MenuItem(theme, action -> change(session, theme, fontSize, fontFamily)));
        }
        MenuItem light = new MenuItem("Light");
        for (String theme : new String[]{ARISTO, CUPERTINO, FLICK, OVERCAST, PEPPER_GRINDER, REDMOND, SMOOTHNESS, UI_LIGHTNESS}) {
            light.addChild(new MenuItem(theme, action -> change(session, theme, fontSize, fontFamily)));
        }
        return new MenuItem("Theme").addChild(dark).addChild(light);
    }

}
