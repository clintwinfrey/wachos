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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Default implementation of a temporary file handler.
 * <p>
 * This class provides the default strategy for creating and managing temporary
 * files in the specified directory. Temporary files are created using
 * {@code File.createTempFile()} and are managed through an {@link OutputStream}
 * for writing data.
 * </p>
 * <p>
 * The temporary file is automatically created in the directory specified by the
 * {@code tempdir} parameter in the constructor. The file is named with a prefix
 * of "NanoHTTPD-" and no suffix. It is the responsibility of the user to delete
 * the file when it is no longer needed.
 * </p>
 */
public class TempFile {

    /**
     * The temporary file created by this instance.
     */
    private final File file;
    /**
     * The output stream for writing data to the temporary file.
     */
    private final OutputStream fstream;

    /**
     * Constructs a {@code DefaultTempFile} instance.
     * <p>
     * Creates a new temporary file in the specified directory with a prefix of
     * "NanoHTTPD-" and no suffix. An {@link OutputStream} is also created for
     * writing data to the temporary file.
     * </p>
     *
     * @param tempdir the directory in which the temporary file will be created
     * @throws IOException if an I/O error occurs while creating the temporary
     * file
     */
    public TempFile(File tempdir) throws IOException {
        this.file = File.createTempFile("NanoHTTPD-", "", tempdir);
        this.fstream = new FileOutputStream(this.file);
    }

    /**
     * Deletes the temporary file and closes the associated
     * {@link OutputStream}.
     * <p>
     * If the file cannot be deleted, an exception is thrown with a message
     * indicating the failure. This method ensures that resources are properly
     * released by closing the output stream before attempting to delete the
     * file.
     * </p>
     *
     * @throws Exception if an error occurs while deleting the file or closing
     * the stream
     */
    public void delete() throws Exception {
        NanoHTTPD.safeClose(this.fstream);
        if (!this.file.delete()) {
            throw new Exception("could not delete temporary file: " + this.file.getAbsolutePath());
        }
    }

    /**
     * Returns the absolute path of the temporary file.
     * <p>
     * This method provides the full path to the temporary file created by this
     * instance, which can be useful for debugging or logging purposes.
     * </p>
     *
     * @return the absolute path of the temporary file
     */
    public String getName() {
        return this.file.getAbsolutePath();
    }

    /**
     * Returns the output stream associated with the temporary file.
     * <p>
     * This method provides access to the {@link OutputStream} for writing data
     * to the temporary file. The stream should be properly closed after use.
     * </p>
     *
     * @return the output stream for writing to the temporary file
     * @throws Exception if an error occurs while opening the output stream
     */
    public OutputStream open() throws Exception {
        return this.fstream;
    }
}
