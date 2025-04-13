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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;

/**
 * The runnable that will be used for the main listening thread of the NanoHTTPD
 * server. It handles binding to the server socket and accepting incoming client
 * connections.
 */
public class ServerRunnable implements Runnable {

    /**
     * The NanoHTTPD instance associated with this runnable.
     */
    private final NanoHTTPD httpd;

    /**
     * The timeout in milliseconds for client connections.
     */
    private final int timeout;

    /**
     * Exception that occurred during the binding of the server socket.
     */
    private IOException bindException;

    /**
     * Indicates whether the server socket has been successfully bound.
     */
    private boolean hasBinded = false;

    /**
     * Constructs a new ServerRunnable with the specified NanoHTTPD instance and
     * timeout.
     *
     * @param httpd The NanoHTTPD instance to use for handling requests.
     * @param timeout The timeout in milliseconds for client connections.
     */
    public ServerRunnable(NanoHTTPD httpd, int timeout) {
        this.httpd = httpd;
        this.timeout = timeout;
    }

    /**
     * Binds the server socket and enters a loop to accept client connections.
     * Each accepted connection is handled asynchronously.
     */
    @Override
    public void run() {
        try {
            httpd.getMyServerSocket().bind(httpd.hostname != null
                    ? new InetSocketAddress(httpd.hostname, httpd.myPort)
                    : new InetSocketAddress(httpd.myPort));
            hasBinded = true;
        } catch (IOException e) {
            this.bindException = e;
            return;
        }

        do {
            try {
                final Socket finalAccept = httpd.getMyServerSocket().accept();
                if (this.timeout > 0) {
                    finalAccept.setSoTimeout(this.timeout);
                }
                final InputStream inputStream = finalAccept.getInputStream();
                httpd.asyncRunner.exec(httpd.createClientHandler(finalAccept, inputStream));
            } catch (IOException e) {
                NanoHTTPD.LOG.log(Level.FINE, "Communication with the client broken", e);
            }
        } while (!httpd.getMyServerSocket().isClosed());
    }

    /**
     * Gets the exception that occurred during the binding of the server socket,
     * if any.
     *
     * @return The IOException that occurred during binding, or null if none
     * occurred.
     */
    public IOException getBindException() {
        return bindException;
    }

    /**
     * Checks whether the server socket has been successfully bound.
     *
     * @return {@code true} if the socket has been bound, {@code false}
     * otherwise.
     */
    public boolean hasBinded() {
        return hasBinded;
    }
}
