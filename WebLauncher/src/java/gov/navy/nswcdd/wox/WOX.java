//<editor-fold defaultstate="collapsed" desc="Generated Code - do not modify">
package gov.navy.nswcdd.wox;

import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.tools.WFileServlet;
import gov.mil.navy.nswcdd.wachos.tools.WSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.annotation.WebServlet;
import java.io.Serializable;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;
import tutorial.gui.WachosTutorial;

@ViewScoped
@Named(value = "wachos")
public class WOX implements Serializable {

    /**
     * Allows files to be accessed that are in accessibleFolders
     */
    @WebServlet(name = "FileServlet", urlPatterns = {"/FileServlet"})
    public static class MyFileServlet extends WFileServlet {
    }
    /**
     * the main entry point
     */
    Layout wachosLayout;
    /**
     * contains things that can be accessed throughout the application
     */
    WSession session;
    /**
     * allows for socket communication
     */
    @Inject
    @Push
    private PushContext socketChannel;

    /**
     * initializes the session, layout, push context, and accessible folders
     */
    @PostConstruct
    public void init() {
        session = new WSession(socketChannel, 10);
        wachosLayout = createWebApp(session);
        session.setLayout(wachosLayout);
    }

    /**
     * @return the session as requested by index.xhtml
     */
    public WSession getSession() {
        return session;
    }

    /**
     * @return the Layout as requested by index.xhtml
     */
    public Layout getLayout() {
        return wachosLayout;
    }

    /**
     * @return the title as requested by index.xhtml
     */
    public String getTitle() {
        return session.title;
    }

    /**
     * @return the icon as requested by index.xhtml
     */
    public String getIcon() {
        return session.icon;
    }

    /**
     * @return the theme as requested by index.xhtml
     */
    public String getTheme() {
        return session.theme;
    }

    /**
     * @return the font family as requested by index.xhtml
     */
    public String getFontFamily() {
        return session.fontFamily;
    }

    /**
     * @return the font size as requested by index.xhtml
     */
    public String getFontSize() {
        return session.fontSize;
    }
//</editor-fold>

    public Layout createWebApp(WSession session) {
        //return new WachosTutorial("C:\\OSM\\MAST\\documentation\\WachosTutorial").create(session);
        return new WachosTutorial("C:\\wachos\\WachosTutorial").create(session);
    }

//<editor-fold defaultstate="collapsed" desc="Generated Code - do not modify">
}//</editor-fold>
