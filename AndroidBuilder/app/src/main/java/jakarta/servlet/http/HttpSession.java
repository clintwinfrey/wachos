package jakarta.servlet.http;

import jakarta.servlet.ServletContext;
import java.util.Enumeration;

public interface HttpSession {

        public long getCreationTime();

        public String getId();

        public long getLastAccessedTime();

        public ServletContext getServletContext();

        public void setMaxInactiveInterval(int i);

        public int getMaxInactiveInterval();

        public Object getAttribute(String string);

        public Enumeration<String> getAttributeNames();

        public void setAttribute(String string, Object o);

        public void removeAttribute(String string);

        public void invalidate();

        public boolean isNew();

}