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
import java.net.ServerSocket;

/**
 * Factory class for creating instances of {@link ServerSocket}.
 * <p>
 * This implementation provides a default way to create a {@link ServerSocket}
 * for TCP connections. It adheres to the {@link IFactoryThrowing} interface,
 * which allows for the creation of objects that may throw an
 * {@link IOException} during instantiation.
 * </p>
 * <p>
 * The 'ServerSocket' created by this factory will be initialized with default
 * settings. To customize the 'ServerSocket' configuration, you would need to
 * use a different factory or extend this class.
 * </p>
 */
public class ServerSocketFactory implements IFactoryThrowing<ServerSocket, IOException> {

    /**
     * Creates a new instance of {@link ServerSocket}.
     * <p>
     * This method initializes the 'ServerSocket' with default settings. If the
     * creation fails, an {@link IOException} will be thrown.
     * </p>
     *
     * @return a newly created {@link ServerSocket} instance
     * @throws IOException if an I/O error occurs when creating the server
     * socket
     */
    @Override
    public ServerSocket create() throws IOException {
        return new ServerSocket();
    }

}
