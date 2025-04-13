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
package tutorial.gui;

import gov.mil.navy.nswcdd.wachos.components.ListBox;
import gov.mil.navy.nswcdd.wachos.components.SimpleComponent;
import gov.mil.navy.nswcdd.wachos.components.Spacer;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.tools.Theme;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import gov.mil.navy.nswcdd.wachos.tools.WachosGui;
import java.util.ArrayList;
import java.util.List;
import tutorial.ButtonTutorial;
import tutorial.CardLayoutTutorial;
import tutorial.CarouselTutorial;
import tutorial.CheckBoxTutorial;
import tutorial.CodeSnippetTutorial;
import tutorial.ColorPickerTutorial;
import tutorial.ComboBoxTutorial;
import tutorial.ComponentListenerTutorial;
import tutorial.ComponentTutorial;
import tutorial.CustomLayoutTutorial;
import tutorial.DataGridTutorial;
import tutorial.DatePickerTutorial;
import tutorial.DialogTutorial;
import tutorial.DividerTutorial;
import tutorial.DropButtonTutorial;
import tutorial.HBoxTutorial;
import tutorial.ImageButtonTutorial;
import tutorial.ImageDropButtonTutorial;
import tutorial.ImageTutorial;
import tutorial.LabelTutorial;
import tutorial.LinkButtonTutorial;
import tutorial.ParagraphTutorial;
import tutorial.ProgressBarTutorial;
import tutorial.QuillTutorial;
import tutorial.SimpleComponentTutorial;
import tutorial.SplashTutorial;
import tutorial.TableTutorial;
import tutorial.TextAreaTutorial;
import tutorial.TextFieldTutorial;
import tutorial.ThemeTutorial;
import tutorial.ThreejsTutorial;
import tutorial.TreeViewTutorial;
import tutorial.Tutorial;
import tutorial.VBoxTutorial;

public class WachosTutorial implements WachosGui {

    public final String workingDir;

    public WachosTutorial(String workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public Layout create(WSession session) {
        session.title = "MAST 2.17";
        session.icon = "resource/w.png";
        session.theme = Theme.getOrDefault(Theme.CUPERTINO);
        session.setWorkingDirectory(workingDir);
        session.addResourceDirectories(".", "../libraries/wachos/dist/javadoc", "../WachosTutorial");

        ListBox selector = new ListBox().setHeight("100%");
        VBox tutorialContainer = new VBox();
        List<Tutorial> tutorials = new ArrayList<>();

        //add the selector, which selects the selected tutorial selection.
        selector.selectionsChangedListeners.add(action -> {
            tutorialContainer.removeAll();
            List<String> selections = selector.getSelectedItems();
            if (selections.isEmpty()) {
                return;
            }
            for (Tutorial tutorial : tutorials) {
                if (tutorial.title.equals(selections.get(0))) {
                    tutorialContainer.add(
                            new Spacer("20px", "20px"),
                            new VBox(new Label(tutorial.title).setStyle("font-size", "25px").alignCenterH()).setWidth("1000px"), //you can also do .setProperty("css.font-size"
                            new VBox(tutorial.component.alignLeft()).setWidth("1000px").setBorder("Example", true),
                            new VBox(tutorial.snippet).setWidth("1000px").setBorder("Source", true),
                            new VBox(new SimpleComponent("<iframe src='resource/" + tutorial.javadoc + "' height='500' width='1000' title='SimpleComponent Example'></iframe>")).setWidth("1000px").setBorder("JavaDoc", true)
                    );
                }
            }
        });

        tutorials.add(ButtonTutorial.create(session));
        tutorials.add(CardLayoutTutorial.create(session));
        tutorials.add(CarouselTutorial.create(session));
        tutorials.add(CheckBoxTutorial.create(session));
        tutorials.add(CodeSnippetTutorial.create(session));
        tutorials.add(ColorPickerTutorial.create(session));
        tutorials.add(ComboBoxTutorial.create(session));
        tutorials.add(ComponentTutorial.create(session));
        tutorials.add(ComponentListenerTutorial.create(session));
        //ContextMenu
        tutorials.add(CustomLayoutTutorial.create(session));
        tutorials.add(DataGridTutorial.create(session));
        tutorials.add(DatePickerTutorial.create(session));
        tutorials.add(DialogTutorial.create(session));
        tutorials.add(DividerTutorial.create(session));
        tutorials.add(DropButtonTutorial.create(session));
        //FileSelector
        //Grid
        tutorials.add(HBoxTutorial.create(session));
        tutorials.add(ImageTutorial.create(session));
        tutorials.add(ImageButtonTutorial.create(session));
        tutorials.add(ImageDropButtonTutorial.create(session));
        tutorials.add(LabelTutorial.create(session));
        //Layout (talk about how they work and how you can put layouts inside of other layouts and stuff)
        tutorials.add(LinkButtonTutorial.create(session));
        //ListBox
        //ListEditor
        //MenuBar
        tutorials.add(ParagraphTutorial.create(session));
        //PasswordField
        tutorials.add(ProgressBarTutorial.create(session));
        tutorials.add(QuillTutorial.create(session));
        tutorials.add(SimpleComponentTutorial.create(session));
        //Slider
        //Spacer
        tutorials.add(SplashTutorial.create(session));
        tutorials.add(TableTutorial.create(session));
        //Tabs
        tutorials.add(ThemeTutorial.create(session));
        tutorials.add(ThreejsTutorial.create(session));
        tutorials.add(TextAreaTutorial.create(session));
        tutorials.add(TextFieldTutorial.create(session));
        tutorials.add(TreeViewTutorial.create(session));
        tutorials.add(VBoxTutorial.create(session));
        //WCDocker
        //WFileServlet
        //WSession
        //WTimer
        //WTools
        //XYChart

        List<String> tutorialOptions = new ArrayList<>();
        for (Tutorial tutorial : tutorials) {
            tutorialOptions.add(tutorial.title);
        }
        selector.setOptions(tutorialOptions);

        return new HBox(new VBox(selector).setHeight("100%").setPadding(20), tutorialContainer.setOverflow("auto").setWidth("100%")).setHeight("100%");
    }

}
