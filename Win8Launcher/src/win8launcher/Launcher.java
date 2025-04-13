package win8launcher;

import gov.mil.navy.nswcdd.wachos.desktop.SwingGui;
import tutorial.gui.WachosTutorial;

public class Launcher {
    public static void main(String args[]) {
        SwingGui.create(new WachosTutorial("."));
    }
    
}
