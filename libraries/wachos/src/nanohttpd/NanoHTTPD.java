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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * NanoHTTPD is a lightweight and embeddable HTTP server. It is designed to be
 * simple and minimal, making it easy to integrate into your own applications.
 * It provides basic functionalities for handling HTTP requests and serving both
 * static and dynamic content.
 *
 * <strong>Copyright:</strong>
 * <ul>
 * <li>2012-2013 by Paul S. Hawke</li>
 * <li>2001, 2005-2013 by Jarno Elonen</li>
 * <li>2010 by Konstantinos Togias</li>
 * </ul>
 *
 * <strong>Features and Limitations:</strong>
 * <ul>
 * <li>Single Java file implementation for ease of use</li>
 * <li>Compatible with Java 5</li>
 * <li>Open-source under Modified BSD License</li>
 * <li>No built-in configuration files, logging, or authorization (implement as
 * needed)</li>
 * <li>Supports parameter parsing for GET and POST methods (rudimentary PUT
 * support in version 1.25)</li>
 * <li>Handles both dynamic content and file serving</li>
 * <li>File upload support (available since version 1.2, 2010)</li>
 * <li>Partial content support for streaming</li>
 * <li>ETag support for cache validation</li>
 * <li>No caching of responses</li>
 * <li>No limitations on bandwidth, request time, or simultaneous
 * connections</li>
 * <li>Default implementation serves files and displays all HTTP parameters and
 * headers</li>
 * <li>File server features directory listing and supports index.html and
 * index.htm</li>
 * <li>301 redirection for directories missing a trailing '/'</li>
 * <li>Handles very large files without significant memory overhead</li>
 * <li>Built-in support for common MIME types</li>
 * <li>All header names are normalized to lower case for consistency across
 * browsers</li>
 * </ul>
 *
 * <strong>Usage Instructions:</strong>
 * <ul>
 * <li>Subclass NanoHTTPD and implement the <code>serve()</code> method to embed
 * it in your application.</li>
 * </ul>
 *
 * For the distribution license, refer to the "LICENSE.md" file (Modified BSD
 * License).
 */
public abstract class NanoHTTPD {

    /**
     * Regular expression used to match the Content-Disposition HTTP header. It
     * captures both the header name and its associated value.
     */
    public static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";

    /**
     * Pattern for matching the Content-Disposition header in a case-insensitive
     * manner.
     */
    public static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(CONTENT_DISPOSITION_REGEX, Pattern.CASE_INSENSITIVE);

    /**
     * Regular expression for parsing the Content-Type HTTP header. It captures
     * both the header name and its associated value.
     */
    public static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";

    /**
     * Pattern for matching the Content-Type header in a case-insensitive
     * manner.
     */
    public static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE);

    /**
     * Regular expression for parsing attributes in the Content-Disposition
     * header. It captures attribute names and their corresponding values.
     */
    public static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";

    /**
     * Pattern for matching attributes in the Content-Disposition header.
     */
    public static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile(CONTENT_DISPOSITION_ATTRIBUTE_REGEX);

    /**
     * Exception thrown to indicate an error related to HTTP responses.
     */
    public static final class ResponseException extends Exception {

        private static final long serialVersionUID = 6569838532917408380L;

        /**
         * the status of the response
         */
        private final Status status;

        /**
         * Constructs a new ResponseException with the specified HTTP status and
         * message.
         *
         * @param status the HTTP status associated with the exception
         * @param message the detail message explaining the exception
         */
        public ResponseException(Status status, String message) {
            super(message);
            this.status = status;
        }

        /**
         * Constructs a new ResponseException with the specified HTTP status,
         * message, and cause.
         *
         * @param status the HTTP status associated with the exception
         * @param message the detail message explaining the exception
         * @param e the cause of the exception
         */
        public ResponseException(Status status, String message, Exception e) {
            super(message, e);
            this.status = status;
        }

        /**
         * Retrieves the HTTP status associated with this exception.
         *
         * @return the HTTP status
         */
        public Status getStatus() {
            return this.status;
        }
    }

    /**
     * Maximum time to wait on {@link Socket#getInputStream()}.read() (in
     * milliseconds). This is required as Keep-Alive HTTP connections would
     * otherwise block the socket reading thread indefinitely.
     */
    public static final int SOCKET_READ_TIMEOUT = 5000;

    /**
     * Common MIME type for plain text content.
     */
    public static final String MIME_PLAINTEXT = "text/plain";

    /**
     * Common MIME type for HTML content.
     */
    public static final String MIME_HTML = "text/html";

    /**
     * Pseudo-parameter used to store the actual query string in the parameters
     * map for later re-processing.
     */
    private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";

    /**
     * Logger for logging messages from the NanoHTTPD server.
     */
    public static final Logger LOG = Logger.getLogger(NanoHTTPD.class.getName());

    /**
     * Hashtable mapping file name extensions to their corresponding MIME types.
     */
    protected static Map<String, String> MIME_TYPES;

    /**
     * Returns a map of common MIME types based on file extensions. Initializes
     * the map if it has not been created yet.
     *
     * @return a map of file extensions to MIME types
     */
    public static Map<String, String> mimeTypes() {
        if (MIME_TYPES == null) {
            MIME_TYPES = new HashMap<>();
            // Add common MIME types
            MIME_TYPES.put("css", "text/css");
            MIME_TYPES.put("htm", "text/html");
            MIME_TYPES.put("html", "text/html");
            MIME_TYPES.put("xml", "text/xml");
            MIME_TYPES.put("java", "text/x-java-source, text/java");
            MIME_TYPES.put("md", "text/plain");
            MIME_TYPES.put("txt", "text/plain");
            MIME_TYPES.put("asc", "text/plain");
            MIME_TYPES.put("gif", "image/gif");
            MIME_TYPES.put("glb", "model/gltf-binary");
            MIME_TYPES.put("jpg", "image/jpeg");
            MIME_TYPES.put("jpeg", "image/jpeg");
            MIME_TYPES.put("png", "image/png");
            MIME_TYPES.put("svg", "image/svg+xml");
            MIME_TYPES.put("mp3", "audio/mpeg");
            MIME_TYPES.put("m3u", "audio/mpeg-url");
            MIME_TYPES.put("mp4", "video/mp4");
            MIME_TYPES.put("ogv", "video/ogg");
            MIME_TYPES.put("flv", "video/x-flv");
            MIME_TYPES.put("mov", "video/quicktime");
            MIME_TYPES.put("swf", "application/x-shockwave-flash");
            MIME_TYPES.put("js", "application/javascript");
            MIME_TYPES.put("pdf", "application/pdf");
            MIME_TYPES.put("doc", "application/msword");
            MIME_TYPES.put("ogg", "application/x-ogg");
            MIME_TYPES.put("zip", "application/octet-stream");
            MIME_TYPES.put("exe", "application/octet-stream");
            MIME_TYPES.put("class", "application/octet-stream");
            MIME_TYPES.put("m3u8", "application/vnd.apple.mpegurl");
            MIME_TYPES.put("ts", "video/mp2t");
        }
        return MIME_TYPES;
    }

    /**
     * Creates an SSLSocketFactory for HTTPS.
     *
     * <p>
     * Pass a loaded KeyStore and an array of loaded KeyManagers. These objects
     * must be properly initialized by the caller.
     * </p>
     *
     * @param loadedKeyStore the KeyStore containing the server's certificate
     * @param keyManagers the array of KeyManagers for SSL configuration
     * @return an SSLServerSocketFactory configured with the specified KeyStore
     * and KeyManagers
     * @throws IOException if an error occurs while creating the socket factory
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManager[] keyManagers) throws IOException {
        SSLServerSocketFactory res = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(loadedKeyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
            res = ctx.getServerSocketFactory();
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
        return res;
    }

    /**
     * Creates an SSLSocketFactory for HTTPS.
     *
     * <p>
     * Pass a loaded KeyStore and a loaded KeyManagerFactory. These objects must
     * be properly initialized by the caller.
     * </p>
     *
     * @param loadedKeyStore the KeyStore containing the server's certificate
     * @param loadedKeyFactory the KeyManagerFactory to use for SSL
     * configuration
     * @return an SSLServerSocketFactory configured with the specified KeyStore
     * and KeyManagerFactory
     * @throws IOException if an error occurs while creating the socket factory
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManagerFactory loadedKeyFactory) throws IOException {
        try {
            return makeSSLSocketFactory(loadedKeyStore, loadedKeyFactory.getKeyManagers());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Creates an SSLSocketFactory for HTTPS.
     *
     * <p>
     * Pass a KeyStore resource containing your certificate and its passphrase.
     * </p>
     *
     * @param keyAndTrustStoreClasspathPath the classpath path to the KeyStore
     * resource
     * @param passphrase the passphrase for the KeyStore
     * @return an SSLServerSocketFactory configured with the specified KeyStore
     * @throws IOException if an error occurs while loading the KeyStore or
     * creating the socket factory
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase) throws IOException {
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream keystoreStream = NanoHTTPD.class.getResourceAsStream(keyAndTrustStoreClasspathPath);

            if (keystoreStream == null) {
                throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
            }

            keystore.load(keystoreStream, passphrase);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            return makeSSLSocketFactory(keystore, keyManagerFactory);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Gets the MIME type associated with the specified file name extension.
     *
     * @param fileName the name of the file, including its extension
     * @return the corresponding MIME type, or "application/octet-stream" if the
     * extension is not recognized
     */
    public static String getMimeTypeForFile(String fileName) {
        int dot = fileName.lastIndexOf('.');
        String mime = null;
        if (dot >= 0) {
            mime = mimeTypes().get(fileName.substring(dot + 1).toLowerCase());
        }
        return mime == null ? "application/octet-stream" : mime;
    }

    /**
     * Safely closes a Closeable resource, logging any IOException that occurs.
     *
     * @param closeable the resource to close, which may be a Closeable, Socket,
     * or ServerSocket
     */
    public static final void safeClose(Object closeable) {
        try {
            if (closeable != null) {
                if (closeable instanceof Closeable) {
                    ((Closeable) closeable).close();
                } else if (closeable instanceof Socket) {
                    ((Socket) closeable).close();
                } else if (closeable instanceof ServerSocket) {
                    ((ServerSocket) closeable).close();
                } else {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (IOException e) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not close", e);
        }
    }

    /**
     * The hostname the server listens on.
     */
    public final String hostname;

    /**
     * The port number the server listens on.
     */
    public final int myPort;

    /**
     * The server socket for accepting incoming connections.
     */
    private volatile ServerSocket myServerSocket;

    /**
     * Retrieves the current server socket.
     *
     * @return the server socket
     */
    public ServerSocket getMyServerSocket() {
        return myServerSocket;
    }

    /**
     * Factory for creating server sockets.
     */
    private IFactoryThrowing<ServerSocket, IOException> serverSocketFactory = new ServerSocketFactory();

    /**
     * The thread handling incoming connections.
     */
    private Thread myThread;

    /**
     * The handler for processing HTTP sessions.
     */
    private IHandler<HTTPSession, Response> httpHandler;

    /**
     * List of interceptors to process HTTP sessions before the main handler.
     */
    protected List<IHandler<HTTPSession, Response>> interceptors = new ArrayList<>(4);

    /**
     * Strategy for asynchronously executing requests.
     */
    protected AsyncRunner asyncRunner;

    /**
     * Constructs an HTTP server on the specified port.
     *
     * @param port the port number to listen on
     */
    public NanoHTTPD(int port) {
        this(null, port);
    }

    /**
     * Constructs an HTTP server on the specified hostname and port.
     *
     * @param hostname the hostname the server listens on (or null for all
     * interfaces)
     * @param port the port number to listen on
     */
    public NanoHTTPD(String hostname, int port) {
        this.hostname = hostname;
        this.myPort = port;
        NanoHTTPD.this.setAsyncRunner(new AsyncRunner());

        // Default handler that redirects to deprecated serve()
        this.httpHandler = (HTTPSession input) -> NanoHTTPD.this.serve(input);
    }

    /**
     * Sets a custom HTTP handler.
     *
     * @param handler the handler to process HTTP sessions
     */
    public void setHTTPHandler(IHandler<HTTPSession, Response> handler) {
        this.httpHandler = handler;
    }

    /**
     * Adds an interceptor for processing HTTP sessions.
     *
     * @param interceptor the interceptor to add
     */
    public void addHTTPInterceptor(IHandler<HTTPSession, Response> interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Forcibly closes all open connections.
     */
    public synchronized void closeAllConnections() {
        stop();
    }

    /**
     * Creates an instance of the client handler. Subclasses can return a
     * subclass of the ClientHandler.
     *
     * @param finalAccept the socket the client is connected to
     * @param inputStream the input stream for the connection
     * @return the client handler
     */
    protected ClientHandler createClientHandler(final Socket finalAccept, final InputStream inputStream) {
        return new ClientHandler(this, inputStream, finalAccept);
    }

    /**
     * Instantiates the server runnable. Can be overridden by subclasses to
     * provide a custom ServerRunnable.
     *
     * @param timeout the socket timeout to use
     * @return the server runnable
     */
    protected ServerRunnable createServerRunnable(final int timeout) {
        return new ServerRunnable(this, timeout);
    }

    /**
     * Decodes parameters from a URL. Handles cases where a single parameter
     * name might appear multiple times, returning lists of values.
     *
     * @param parms original parameters passed to the serve() method
     * @return a map of parameter names to lists of values
     */
    protected static Map<String, List<String>> decodeParameters(Map<String, String> parms) {
        return decodeParameters(parms.get(NanoHTTPD.QUERY_STRING_PARAMETER));
    }

    /**
     * Decodes parameters from a query string. Handles cases where a single
     * parameter name might appear multiple times, returning lists of values.
     *
     * @param queryString the query string pulled from the URL
     * @return a map of parameter names to lists of values
     */
    protected static Map<String, List<String>> decodeParameters(String queryString) {
        Map<String, List<String>> parms = new HashMap<>();
        if (queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                String propertyName = sep >= 0 ? decodePercent(e.substring(0, sep)).trim() : decodePercent(e).trim();
                if (!parms.containsKey(propertyName)) {
                    parms.put(propertyName, new ArrayList<>());
                }
                String propertyValue = sep >= 0 ? decodePercent(e.substring(sep + 1)) : null;
                if (propertyValue != null) {
                    parms.get(propertyName).add(propertyValue);
                }
            }
        }
        return parms;
    }

    /**
     * Decodes percent-encoded strings.
     *
     * @param str the percent-encoded string
     * @return the decoded string, or null if decoding fails
     */
    public static String decodePercent(String str) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
            NanoHTTPD.LOG.log(Level.WARNING, "Encoding not supported, ignored", ignored);
        }
        return decoded;
    }

    /**
     * Retrieves the port number on which the server is currently listening.
     *
     * @return the listening port, or -1 if the server is not running
     */
    public final int getListeningPort() {
        return this.myServerSocket == null ? -1 : this.myServerSocket.getLocalPort();
    }

    /**
     * Checks if the server is alive and accepting connections.
     *
     * @return true if the server is running, false otherwise
     */
    public final boolean isAlive() {
        return wasStarted() && !this.myServerSocket.isClosed() && this.myThread.isAlive();
    }

    /**
     * Retrieves the server socket factory used to create server sockets.
     *
     * @return the server socket factory
     */
    public IFactoryThrowing<ServerSocket, IOException> getServerSocketFactory() {
        return serverSocketFactory;
    }

    /**
     * Sets a custom server socket factory.
     *
     * @param serverSocketFactory the new server socket factory
     */
    public void setServerSocketFactory(IFactoryThrowing<ServerSocket, IOException> serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    /**
     * Retrieves the hostname the server is listening on.
     *
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Configures the server to use HTTPS.
     *
     * @param sslServerSocketFactory the SSLServerSocketFactory for secure
     * connections
     * @param sslProtocols an array of SSL protocols to use
     */
    public void makeSecure(SSLServerSocketFactory sslServerSocketFactory, String[] sslProtocols) {
        this.serverSocketFactory = new SecureServerSocketFactory(sslServerSocketFactory, sslProtocols);
    }

    /**
     * Handles incoming HTTP sessions. This is the main method that delegates
     * requests to the appropriate handlers. It ensures that every request
     * receives a response.
     *
     * @param session the incoming HTTP session
     * @return a response to the incoming session
     */
    public Response handle(HTTPSession session) {
        for (IHandler<HTTPSession, Response> interceptor : interceptors) {
            Response response = interceptor.handle(session);
            if (response != null) {
                return response;
            }
        }
        return httpHandler.handle(session);
    }

    /**
     * Handles HTTP sessions. Override this method to customize the server's
     * response to requests. By default, it returns a 404 "Not Found" response.
     *
     * @param session the HTTP session
     * @return HTTP response, see class Response for details
     */
    protected Response serve(HTTPSession session) {
        return Response.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found");
    }

    /**
     * Sets a custom strategy for asynchronously executing requests.
     *
     * @param asyncRunner the new async runner strategy
     */
    public void setAsyncRunner(AsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }

    /**
     * Starts the server.
     *
     * @throws IOException if the server socket cannot be created (e.g., if the
     * port is already in use)
     */
    public void start() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT);
    }

    /**
     * Starts the server with a specified timeout and in daemon mode.
     *
     * @param timeout the timeout for socket connections
     * @throws IOException if the server socket cannot be created (e.g., if the
     * port is already in use)
     */
    public void start(final int timeout) throws IOException {
        start(timeout, true);
    }

    /**
     * Starts the server with a specified timeout and daemon setting.
     *
     * @param timeout the timeout for socket connections
     * @param daemon whether the thread should run as a daemon
     * @throws IOException if the server socket cannot be created (e.g., if the
     * port is already in use)
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void start(final int timeout, boolean daemon) throws IOException {
        this.myServerSocket = this.getServerSocketFactory().create();
        this.myServerSocket.setReuseAddress(true);

        ServerRunnable serverRunnable = createServerRunnable(timeout);
        this.myThread = new Thread(serverRunnable);
        this.myThread.setDaemon(daemon);
        this.myThread.setName("NanoHttpd Main Listener");
        this.myThread.start();
        while (!serverRunnable.hasBinded() && serverRunnable.getBindException() == null) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                // Handle potential interruptions during sleep
            }
        }
        if (serverRunnable.getBindException() != null) {
            throw serverRunnable.getBindException();
        }
    }

    /**
     * Stops the server and closes all open connections.
     */
    public void stop() {
        try {
            safeClose(this.myServerSocket);
            this.asyncRunner.closeAll();
            if (this.myThread != null) {
                this.myThread.join();
            }
        } catch (InterruptedException e) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not stop all connections", e);
        }
    }

    /**
     * Checks if the server has been started.
     *
     * @return true if the server is running, false otherwise
     */
    public final boolean wasStarted() {
        return this.myServerSocket != null && this.myThread != null;
    }
}
