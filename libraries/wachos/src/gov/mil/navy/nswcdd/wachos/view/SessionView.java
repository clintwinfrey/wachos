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
package gov.mil.navy.nswcdd.wachos.view;

import gov.mil.navy.nswcdd.wachos.tools.WSession;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.Serializable;
import org.omnifaces.util.Servlets;

/**
 * SessionView allows the user to upload a file
 */
@Named(value = "sessionBean")
@ViewScoped
public class SessionView implements Serializable {

    /**
     * the file to upload
     */
    private Part file;

    /**
     * Reads the file that was loaded and notifies listeners
     *
     * @param session the session that is associated with the uploaded file
     * @throws IOException if file can't be uploaded
     */
    public void read(WSession session) throws IOException {
        if (file != null) {
            try {
                session.fileLoaded(Servlets.getSubmittedFileName(file), file.getContentType(), file.getInputStream());
            } finally {
                file.getInputStream().close();
            }
        }
    }

    /**
     * @return the uploaded file
     */
    public Part getFile() {
        return file;
    }

    /**
     * Sets the file
     *
     * @param file the uploaded file
     */
    public void setFile(Part file) {
        this.file = file;
    }

    /**
     * Provides the hashcode of the session
     * @param session gets the hashcode of this
     *
     * @return the hashcode of the session
     */
    public String getHashCode(WSession session) {
        return session.hashCode() + "";
    }

}
