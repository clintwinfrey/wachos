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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * Handles client connections for the NanoHTTPD server.
 * <p>
 * This class implements the {@link Runnable} interface and is responsible for
 * managing a single client connection, processing HTTP requests, and sending
 * responses. It runs in its own thread and ensures proper cleanup of resources
 * when the connection is closed or when an error occurs.
 * </p>
 */
public class ClientHandler implements Runnable {

    /**
     * The NanoHTTPD server instance handling this client.
     */
    private final NanoHTTPD httpd;
    /**
     * The input stream associated with the client connection.
     */
    private final InputStream inputStream;
    /**
     * The socket representing the client connection.
     */
    private final Socket acceptSocket;

    /**
     * Constructs a new ClientHandler.
     *
     * @param httpd the NanoHTTPD server instance managing this handler
     * @param inputStream the input stream for reading data from the client
     * @param acceptSocket the socket for the client connection
     */
    public ClientHandler(NanoHTTPD httpd, InputStream inputStream, Socket acceptSocket) {
        this.httpd = httpd;
        this.inputStream = inputStream;
        this.acceptSocket = acceptSocket;
    }

    /**
     * Closes the input stream and socket associated with this handler.
     * <p>
     * This method is used to release resources when they are no longer needed
     * or when an error occurs. It calls
     * {@link NanoHTTPD#safeClose(OutputStream)} and
     * {@link NanoHTTPD#safeClose(Socket)} to ensure that the resources are
     * properly closed and cleaned up.
     * </p>
     */
    public void close() {
        NanoHTTPD.safeClose(this.inputStream);
        NanoHTTPD.safeClose(this.acceptSocket);
    }

    /**
     * Executes the client handling process.
     * <p>
     * This method is run in a separate thread and handles the client requests
     * by creating an {@link HTTPSession} to process incoming requests and send
     * responses. It continues to process requests until the client connection
     * is closed. If an exception occurs, it logs the error if it is not a
     * specific type of expected exception.
     * </p>
     */
    @Override
    public void run() {
        OutputStream outputStream = null;
        try {
            outputStream = this.acceptSocket.getOutputStream();
            TempFileManager tempFileManager = new TempFileManager();
            HTTPSession session = new HTTPSession(httpd, tempFileManager, this.inputStream, outputStream, this.acceptSocket.getInetAddress());
            while (!this.acceptSocket.isClosed()) {
                session.execute();
            }
        } catch (IOException e) {
            // When the socket is closed by the client, we throw our own SocketException to break the "keep alive" loop above. If
            // the exception was anything other than the expected SocketException OR a SocketTimeoutException, print the stacktrace
            if (!(e instanceof SocketException && "NanoHttpd Shutdown".equals(e.getMessage())) && !(e instanceof SocketTimeoutException)) {
                NanoHTTPD.LOG.log(Level.SEVERE, "Communication with the client broken, or an bug in the handler code", e);
            }
        } finally {
            NanoHTTPD.safeClose(outputStream);
            NanoHTTPD.safeClose(this.inputStream);
            NanoHTTPD.safeClose(this.acceptSocket);
            httpd.asyncRunner.closed(this);
        }
    }
}
