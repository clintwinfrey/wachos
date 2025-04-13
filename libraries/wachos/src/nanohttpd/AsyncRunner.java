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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of the asynchronous runner for handling HTTP requests
 * in NanoHTTPD.
 * <p>
 * This class uses a simple threading strategy where a new thread is created for
 * each incoming request. Each thread is set as a <i>daemon</i> and is named
 * based on the request number. This naming convention is useful for profiling
 * and debugging purposes.
 * </p>
 * <p>
 * Threads are managed and tracked in a synchronized list to ensure thread
 * safety. The class provides methods to start, close, and remove client
 * handlers from the running list.
 * </p>
 */
public class AsyncRunner {

    /**
     * Counter to keep track of the number of requests processed.
     */
    protected long requestCount;
    /**
     * A synchronized list of currently running client handlers.
     */
    private final List<ClientHandler> running = Collections.synchronizedList(new ArrayList<>());

    /**
     * Returns a list of currently running client handlers.
     * <p>
     * The list is thread-safe, providing a snapshot of active client handlers
     * at the time of the call.
     * </p>
     *
     * @return a list of currently running client handlers
     */
    public List<ClientHandler> getRunning() {
        return running;
    }

    /**
     * Closes all currently running client handlers.
     * <p>
     * This method iterates over a copy of the list of running client handlers
     * to avoid concurrency issues. Each client handler is closed to stop its
     * associated thread.
     * </p>
     */
    public void closeAll() {
        // copy of the list for concurrency
        for (ClientHandler clientHandler : new ArrayList<>(this.running)) {
            clientHandler.close();
        }
    }

    /**
     * Removes a client handler from the list of running client handlers.
     * <p>
     * This method is called when a client handler has completed its task and
     * should be removed from the list.
     * </p>
     *
     * @param clientHandler the client handler to be removed
     */
    public void closed(ClientHandler clientHandler) {
        this.running.remove(clientHandler);
    }

    /**
     * Executes a client handler by creating a new thread for it and starting
     * the thread.
     * <p>
     * The request count is incremented, the client handler is added to the list
     * of running handlers, and a new thread is created and started to handle
     * the client request.
     * </p>
     *
     * @param clientHandler the client handler to be executed
     */
    public void exec(ClientHandler clientHandler) {
        ++this.requestCount;
        this.running.add(clientHandler);
        createThread(clientHandler).start();
    }

    /**
     * Creates a new thread for the given client handler.
     * <p>
     * The thread is configured as a daemon and is given a name that includes
     * the current request count. This naming helps in profiling and debugging.
     * </p>
     *
     * @param clientHandler the client handler to be run in the new thread
     * @return the newly created thread
     */
    protected Thread createThread(ClientHandler clientHandler) {
        Thread t = new Thread(clientHandler);
        t.setDaemon(true);
        t.setName("NanoHttpd Request Processor (#" + this.requestCount + ")");
        return t;
    }
}
