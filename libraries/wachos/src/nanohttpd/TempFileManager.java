/**
 * #%L NanoHttpd-Core %% Copyright (C) 2012 - 2016 nanohttpd %% Redistribution
 * and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. #L%
 */
package nanohttpd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Provides the default strategy for managing temporary files within the
 * application.
 * <p>
 * This implementation stores temporary files in the directory specified by the
 * system property <code>java.io.tmpdir</code>. It maintains an internal list of
 * temporary files that are created and cleaned up as needed. Temporary files
 * are deleted when the {@link #clear()} method is invoked, typically at the end
 * of processing a request.
 * </p>
 * <p>
 * The constructor ensures that the temporary directory exists; if it does not,
 * it is created. This class implements the {@link TempFileManager} interface.
 * </p>
 */
public class TempFileManager {

    /**
     * The directory where temporary files are stored. This directory is
     * determined by the system property {@code java.io.tmpdir}. If the
     * directory does not exist, it will be created by the constructor.
     */
    private final File tmpdir;
    /**
     * A list that holds references to the temporary files managed by this
     * instance. This list is used to keep track of all created temporary files
     * so that they can be deleted when {@link #clear()} is called.
     */
    private final List<TempFile> tempFiles;

    /**
     * Constructs a new {@code DefaultTempFileManager} that uses the system's
     * default temporary directory as specified by the {@code java.io.tmpdir}
     * system property. If the directory does not exist, it is created.
     */
    public TempFileManager() {
        this.tmpdir = new File(System.getProperty("java.io.tmpdir"));
        if (!tmpdir.exists()) {
            tmpdir.mkdirs();
        }
        this.tempFiles = new ArrayList<>();
    }

    /**
     * Deletes all temporary files managed by this instance and clears the
     * internal list of temporary files. This method is typically called at the
     * end of request processing to ensure that no temporary files are left
     * over.
     * <p>
     * If an error occurs while deleting a file, a warning is logged, but the
     * error is otherwise ignored to ensure that the cleanup process continues.
     * </p>
     */
    public void clear() {
        for (TempFile file : this.tempFiles) {
            try {
                file.delete();
            } catch (Exception ignored) {
                NanoHTTPD.LOG.log(Level.WARNING, "could not delete file ", ignored);
            }
        }
        this.tempFiles.clear();
    }

    /**
     * Creates a new temporary file with a hint for the filename. The created
     * file is added to the internal list of temporary files and will be managed
     * by this instance.
     *
     * @param filename_hint a hint for the filename of the temporary file; this
     * hint is used for informational purposes and may not be reflected in the
     * actual filename of the created file
     * @return an {@link TempFile} instance representing the newly created
     * temporary file
     * @throws Exception if an error occurs while creating the temporary file
     */
    public TempFile createTempFile(String filename_hint) throws Exception {
        TempFile tempFile = new TempFile(this.tmpdir);
        this.tempFiles.add(tempFile);
        return tempFile;
    }
}
