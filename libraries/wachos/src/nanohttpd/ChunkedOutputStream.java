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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream that writes data in chunked transfer encoding format.
 * <p>
 * This class extends {@link FilterOutputStream} and is designed to write data
 * in a chunked manner, which is useful for HTTP streaming where the total size
 * of the content is not known in advance.
 * </p>
 */
public class ChunkedOutputStream extends FilterOutputStream {

    /**
     * Creates a new ChunkedOutputStream that writes to the specified output
     * stream.
     *
     * @param out the underlying output stream to which data will be written
     */
    public ChunkedOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Writes a single byte to the output stream as part of a chunk.
     * <p>
     * This method converts the single byte into a byte array and delegates to
     * the {@link #write(byte[], int, int)} method.
     * </p>
     *
     * @param b the byte to be written
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(int b) throws IOException {
        byte[] data = {
            (byte) b
        };
        write(data, 0, 1);
    }

    /**
     * Writes a byte array to the output stream as part of a chunk.
     * <p>
     * This method delegates to the {@link #write(byte[], int, int)} method with
     * the entire byte array.
     * </p>
     *
     * @param b the byte array to be written
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * Writes a portion of a byte array to the output stream as part of a chunk.
     * <p>
     * This method writes the length of the chunk, followed by the chunk data,
     * and ends with a CRLF sequence. The length is expressed in hexadecimal
     * format.
     * </p>
     *
     * @param b the byte array containing the data to be written
     * @param off the offset within the byte array
     * @param len the number of bytes to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        out.write(String.format("%x\r\n", len).getBytes());
        out.write(b, off, len);
        out.write("\r\n".getBytes());
    }

    /**
     * Ends the chunked transfer encoding by writing a final zero-length chunk.
     * <p>
     * This method writes "0\r\n\r\n" to indicate the end of the chunks.
     * </p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void finish() throws IOException {
        out.write("0\r\n\r\n".getBytes());
    }
}
