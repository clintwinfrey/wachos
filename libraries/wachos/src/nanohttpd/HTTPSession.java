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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import javax.net.ssl.SSLException;
import nanohttpd.NanoHTTPD.ResponseException;

/**
 * Represents an HTTP session in NanoHTTPD. This class handles the reading of
 * HTTP request headers, decoding of form data, and managing cookies. It also
 * provides methods for processing HTTP requests and generating responses.
 */
public class HTTPSession {

    /**
     * Constant representing the key for POST data in the session parameters.
     */
    public static final String POST_DATA = "postData";
    /**
     * Buffer size for reading request data.
     */
    private static final int REQUEST_BUFFER_LEN = 512;
    /**
     * Limit for the memory store size.
     */
    private static final int MEMORY_STORE_LIMIT = 1024;
    /**
     * Buffer size for reading data from the input stream.
     */
    public static final int BUFSIZE = 8192;
    /**
     * Maximum size for HTTP headers.
     */
    public static final int MAX_HEADER_SIZE = 1024;
    /**
     * The NanoHTTPD server instance handling this session.
     */
    private final NanoHTTPD httpd;
    /**
     * Manager for temporary files during the session.
     */
    private final TempFileManager tempFileManager;
    /**
     * Output stream for sending responses.
     */
    private final OutputStream outputStream;
    /**
     * Input stream for reading requests.
     */
    private final BufferedInputStream inputStream;
    /**
     * Byte index where the header splits from the body.
     */
    private int splitbyte;
    /**
     * Length of the request data read so far.
     */
    private int rlen;
    /**
     * URI of the request.
     */
    private String uri;
    /**
     * HTTP method of the request (GET, POST, etc.).
     */
    private Method method;
    /**
     * Parameters of the request, including query and form parameters.
     */
    private Map<String, List<String>> parms;
    /**
     * HTTP headers of the request.
     */
    private Map<String, String> headers;
    /**
     * Handler for managing cookies in the request.
     */
    private CookieHandler cookies;
    /**
     * Query parameter string from the request URI.
     */
    private String queryParameterString;
    /**
     * IP address of the remote client making the request.
     */
    private String remoteIp;
    /**
     * Protocol version of the request (e.g., HTTP/1.1).
     */
    private String protocolVersion;

    /**
     * Constructs an HTTPSession with the specified parameters.
     *
     * @param httpd the NanoHTTPD server instance handling this session
     * @param tempFileManager the manager for temporary files
     * @param inputStream the input stream to read request data
     * @param outputStream the output stream to send responses
     */
    public HTTPSession(NanoHTTPD httpd, TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
        this.httpd = httpd;
        this.tempFileManager = tempFileManager;
        this.inputStream = new BufferedInputStream(inputStream, HTTPSession.BUFSIZE);
        this.outputStream = outputStream;
    }

    /**
     * Constructs an HTTPSession with the specified parameters and remote IP
     * address.
     *
     * @param httpd the NanoHTTPD server instance handling this session
     * @param tempFileManager the manager for temporary files
     * @param inputStream the input stream to read request data
     * @param outputStream the output stream to send responses
     * @param inetAddress the IP address of the remote client
     */
    public HTTPSession(NanoHTTPD httpd, TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
        this.httpd = httpd;
        this.tempFileManager = tempFileManager;
        this.inputStream = new BufferedInputStream(inputStream, HTTPSession.BUFSIZE);
        this.outputStream = outputStream;
        this.remoteIp = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "127.0.0.1" : inetAddress.getHostAddress().toString();
        this.headers = new HashMap<String, String>();
    }

    /**
     * Decodes the HTTP headers from the input reader and populates the provided
     * maps with the data.
     *
     * @param in the BufferedReader for reading headers
     * @param pre map to store HTTP method and URI
     * @param parms map to store request parameters
     * @param headers map to store request headers
     * @throws ResponseException if an error occurs while reading or parsing the
     * headers
     */
    private void decodeHeader(BufferedReader in, Map<String, String> pre, Map<String, List<String>> parms, Map<String, String> headers) throws ResponseException {
        try {
            // Read the request line
            String inLine = in.readLine();
            if (inLine == null) {
                return; // End of stream, no request received
            }

            StringTokenizer st = new StringTokenizer(inLine);
            if (!st.hasMoreTokens()) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
            }

            pre.put("method", st.nextToken());

            if (!st.hasMoreTokens()) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
            }

            String uri = st.nextToken();

            // Decode parameters from the URI
            int qmi = uri.indexOf('?');
            if (qmi >= 0) {
                decodeParms(uri.substring(qmi + 1), parms);
                uri = NanoHTTPD.decodePercent(uri.substring(0, qmi));
            } else {
                uri = NanoHTTPD.decodePercent(uri);
            }

            // Determine protocol version
            if (st.hasMoreTokens()) {
                protocolVersion = st.nextToken();
            } else {
                protocolVersion = "HTTP/1.1";
                NanoHTTPD.LOG.log(Level.FINE, "No protocol version specified; assuming HTTP/1.1.");
            }

            // Read headers
            String line = in.readLine();
            while (line != null && !line.trim().isEmpty()) {
                int p = line.indexOf(':');
                if (p >= 0) {
                    headers.put(line.substring(0, p).trim().toLowerCase(Locale.US), line.substring(p + 1).trim());
                }
                line = in.readLine();
            }

            pre.put("uri", uri);
        } catch (IOException ioe) {
            throw new ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage(), ioe);
        }
    }

    /**
     * Decodes multipart form data and populates the provided maps with the
     * parameters and files.
     *
     * @param contentType the content type of the multipart form data
     * @param fbuf the ByteBuffer containing the form data
     * @param parms map to store parameters
     * @param files map to store file paths
     * @throws ResponseException if an error occurs while decoding the multipart
     * data
     */
    private void decodeMultipartFormData(ContentType contentType, ByteBuffer fbuf, Map<String, List<String>> parms, Map<String, String> files) throws ResponseException {
        int pcount = 0;
        try {
            int[] boundaryIdxs = getBoundaryPositions(fbuf, contentType.getBoundary().getBytes());
            if (boundaryIdxs.length < 2) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings.");
            }

            byte[] partHeaderBuff = new byte[MAX_HEADER_SIZE];
            for (int boundaryIdx = 0; boundaryIdx < boundaryIdxs.length - 1; boundaryIdx++) {
                fbuf.position(boundaryIdxs[boundaryIdx]);
                int len = (fbuf.remaining() < MAX_HEADER_SIZE) ? fbuf.remaining() : MAX_HEADER_SIZE;
                fbuf.get(partHeaderBuff, 0, len);
                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(partHeaderBuff, 0, len), Charset.forName(contentType.getEncoding())), len);

                int headerLines = 0;
                // First line is boundary string
                String mpline = in.readLine();
                headerLines++;
                if (mpline == null || !mpline.contains(contentType.getBoundary())) {
                    throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary.");
                }

                String partName = null, fileName = null, partContentType = null;
                // Parse the rest of the header lines
                mpline = in.readLine();
                headerLines++;
                while (mpline != null && mpline.trim().length() > 0) {
                    Matcher matcher = NanoHTTPD.CONTENT_DISPOSITION_PATTERN.matcher(mpline);
                    if (matcher.matches()) {
                        String attributeString = matcher.group(2);
                        matcher = NanoHTTPD.CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(attributeString);
                        while (matcher.find()) {
                            String key = matcher.group(1);
                            if ("name".equalsIgnoreCase(key)) {
                                partName = matcher.group(2);
                            } else if ("filename".equalsIgnoreCase(key)) {
                                fileName = matcher.group(2);
                                // Add support for multiple files uploaded using the same field ID
                                if (!fileName.isEmpty()) {
                                    if (pcount > 0) {
                                        partName = partName + String.valueOf(pcount++);
                                    } else {
                                        pcount++;
                                    }
                                }
                            }
                        }
                    }
                    matcher = NanoHTTPD.CONTENT_TYPE_PATTERN.matcher(mpline);
                    if (matcher.matches()) {
                        partContentType = matcher.group(2).trim();
                    }
                    mpline = in.readLine();
                    headerLines++;
                }

                // Process headers to calculate length
                int partHeaderLength = 0;
                while (headerLines-- > 0) {
                    partHeaderLength = skipOverNewLine(partHeaderBuff, partHeaderLength);
                }
                // Read the part data
                if (partHeaderLength >= len - 4) {
                    throw new ResponseException(Status.INTERNAL_ERROR, "Multipart header size exceeds MAX_HEADER_SIZE.");
                }
                int partDataStart = boundaryIdxs[boundaryIdx] + partHeaderLength;
                int partDataEnd = boundaryIdxs[boundaryIdx + 1] - 4;

                fbuf.position(partDataStart);

                List<String> values = parms.get(partName);
                if (values == null) {
                    values = new ArrayList<String>();
                    parms.put(partName, values);
                }

                if (partContentType == null) {
                    // Read the part into a string
                    byte[] data_bytes = new byte[partDataEnd - partDataStart];
                    fbuf.get(data_bytes);
                    values.add(new String(data_bytes, contentType.getEncoding()));
                } else {
                    // Read it into a file
                    String path = saveTmpFile(fbuf, partDataStart, partDataEnd - partDataStart, fileName);
                    if (!files.containsKey(partName)) {
                        files.put(partName, path);
                    } else {
                        int count = 2;
                        while (files.containsKey(partName + count)) {
                            count++;
                        }
                        files.put(partName + count, path);
                    }
                    values.add(fileName);
                }
            }
        } catch (ResponseException re) {
            throw re; // Rethrow known response exception
        } catch (Exception e) {
            throw new ResponseException(Status.INTERNAL_ERROR, e.toString());
        }
    }

    /**
     * Skips over newline characters in the header buffer.
     *
     * @param partHeaderBuff the buffer containing header data
     * @param index the starting index to skip from
     * @return the new index position after skipping the newline characters
     */
    private int skipOverNewLine(byte[] partHeaderBuff, int index) {
        while (partHeaderBuff[index] != '\n') {
            index++;
        }
        return ++index;
    }

    /**
     * Decodes query parameters in percent-encoded URI format and adds them to
     * the provided map.
     *
     * @param parms the percent-encoded URI parameters
     * @param p the map to store decoded parameters
     */
    private void decodeParms(String parms, Map<String, List<String>> p) {
        if (parms == null) {
            this.queryParameterString = "";
            return;
        }

        this.queryParameterString = parms;
        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            String key = null;
            String value = null;

            if (sep >= 0) {
                key = NanoHTTPD.decodePercent(e.substring(0, sep)).trim();
                value = NanoHTTPD.decodePercent(e.substring(sep + 1));
            } else {
                key = NanoHTTPD.decodePercent(e).trim();
                value = "";
            }

            List<String> values = p.get(key);
            if (values == null) {
                values = new ArrayList<String>();
                p.put(key, values);
            }

            values.add(value);
        }
    }

    /**
     * Executes the HTTP session by processing the request, handling the
     * response, and managing any exceptions. It reads the request header,
     * decodes it, handles the HTTP method, processes the request body, and
     * sends the response. This method also handles various types of exceptions,
     * including 'SocketException', 'SocketTimeoutException', 'SSLException',
     * and 'IOException'.
     *
     * @throws IOException if an I/O error occurs during request processing or
     * response handling
     */
    public void execute() throws IOException {
        Response r = null;
        try {
            // Read the first BUFSIZE bytes; the full header should fit in here.
            byte[] buf = new byte[HTTPSession.BUFSIZE];
            this.splitbyte = 0;
            this.rlen = 0;

            int read = -1;
            this.inputStream.mark(HTTPSession.BUFSIZE);
            try {
                read = this.inputStream.read(buf, 0, HTTPSession.BUFSIZE);
            } catch (SSLException e) {
                throw e;
            } catch (IOException e) {
                NanoHTTPD.safeClose(this.inputStream);
                NanoHTTPD.safeClose(this.outputStream);
                throw new SocketException("NanoHttpd Shutdown");
            }
            if (read == -1) {
                // Socket was closed
                NanoHTTPD.safeClose(this.inputStream);
                NanoHTTPD.safeClose(this.outputStream);
                throw new SocketException("NanoHttpd Shutdown");
            }
            while (read > 0) {
                this.rlen += read;
                this.splitbyte = findHeaderEnd(buf, this.rlen);
                if (this.splitbyte > 0) {
                    break;
                }
                read = this.inputStream.read(buf, this.rlen, HTTPSession.BUFSIZE - this.rlen);
            }

            if (this.splitbyte < this.rlen) {
                this.inputStream.reset();
                this.inputStream.skip(this.splitbyte);
            }

            this.parms = new HashMap<>();
            if (null == this.headers) {
                this.headers = new HashMap<String, String>();
            } else {
                this.headers.clear();
            }

            // Create a BufferedReader for parsing the header
            BufferedReader hin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, this.rlen)));

            // Decode the header into parameters and headers
            Map<String, String> pre = new HashMap<>();
            decodeHeader(hin, pre, this.parms, this.headers);

            if (this.remoteIp != null) {
                this.headers.put("remote-addr", this.remoteIp);
                this.headers.put("http-client-ip", this.remoteIp);
            }

            this.method = Method.lookup(pre.get("method"));
            if (this.method == null) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Syntax error. HTTP verb " + pre.get("method") + " unhandled.");
            }

            this.uri = pre.get("uri");

            this.cookies = new CookieHandler(this.headers);

            String connection = this.headers.get("connection");
            boolean keepAlive = "HTTP/1.1".equals(protocolVersion) && (connection == null || !connection.matches("(?i).*close.*"));

            // Handle the request and generate the response
            r = httpd.handle(this);
            if (r == null) {
                throw new ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
            } else {
                String acceptEncoding = this.headers.get("accept-encoding");
                this.cookies.unloadQueue(r);
                r.setRequestMethod(this.method);
                if (acceptEncoding == null || !acceptEncoding.contains("gzip")) {
                    r.setUseGzip(false);
                }
                r.setKeepAlive(keepAlive);
                r.send(this.outputStream);
            }
            if (!keepAlive || r.isCloseConnection()) {
                throw new SocketException("NanoHttpd Shutdown");
            }
        } catch (SocketException e) {
            throw e;
        } catch (SocketTimeoutException ste) {
            throw ste;
        } catch (SSLException ssle) {
            Response resp = Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SSL PROTOCOL FAILURE: " + ssle.getMessage());
            resp.send(this.outputStream);
            NanoHTTPD.safeClose(this.outputStream);
        } catch (IOException ioe) {
            Response resp = Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            resp.send(this.outputStream);
            NanoHTTPD.safeClose(this.outputStream);
        } catch (ResponseException re) {
            Response resp = Response.newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
            resp.send(this.outputStream);
            NanoHTTPD.safeClose(this.outputStream);
        } finally {
            NanoHTTPD.safeClose(r);
            this.tempFileManager.clear();
        }
    }

    /**
     * Finds the byte index separating the header from the body in the given
     * buffer. The separation is identified by the presence of two sequential
     * newlines.
     *
     * @param buf the byte buffer containing the request data
     * @param rlen the length of the buffer data read so far
     * @return the index of the byte where the header ends and the body begins
     */
    private int findHeaderEnd(final byte[] buf, int rlen) {
        int splitbyte = 0;
        while (splitbyte + 1 < rlen) {
            if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && splitbyte + 3 < rlen && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
                return splitbyte + 4;
            }
            if (buf[splitbyte] == '\n' && buf[splitbyte + 1] == '\n') {
                return splitbyte + 2;
            }
            splitbyte++;
        }
        return 0;
    }

    /**
     * Finds the positions of multipart boundaries in the given ByteBuffer. The
     * boundaries are used to separate different parts of a multipart request.
     *
     * @param b the ByteBuffer containing the multipart data
     * @param boundary the boundary string used to separate parts
     * @return an array of byte positions where the boundaries start
     */
    private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
        int[] res = new int[0];
        if (b.remaining() < boundary.length) {
            return res;
        }

        int search_window_pos = 0;
        byte[] search_window = new byte[4 * 1024 + boundary.length];

        int first_fill = (b.remaining() < search_window.length) ? b.remaining() : search_window.length;
        b.get(search_window, 0, first_fill);
        int new_bytes = first_fill - boundary.length;

        do {
            // Search the search_window for boundary matches
            for (int j = 0; j < new_bytes; j++) {
                for (int i = 0; i < boundary.length; i++) {
                    if (search_window[j + i] != boundary[i]) {
                        break;
                    }
                    if (i == boundary.length - 1) {
                        // Match found, add it to results
                        int[] new_res = new int[res.length + 1];
                        System.arraycopy(res, 0, new_res, 0, res.length);
                        new_res[res.length] = search_window_pos + j;
                        res = new_res;
                    }
                }
            }
            search_window_pos += new_bytes;

            // Copy the end of the buffer to the start of the search window
            System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0, boundary.length);

            // Refill search_window
            new_bytes = search_window.length - boundary.length;
            new_bytes = (b.remaining() < new_bytes) ? b.remaining() : new_bytes;
            b.get(search_window, boundary.length, new_bytes);
        } while (new_bytes > 0);
        return res;
    }

    /**
     * Retrieves the cookie handler for this session.
     *
     * @return the CookieHandler instance managing cookies for this session
     */
    public CookieHandler getCookies() {
        return this.cookies;
    }

    /**
     * Retrieves the HTTP headers for this session.
     *
     * @return a map containing the headers of the request
     */
    public final Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Retrieves the input stream for reading the request data.
     *
     * @return the InputStream for reading request data
     */
    public final InputStream getInputStream() {
        return this.inputStream;
    }

    /**
     * Retrieves the HTTP method used in the request (e.g., GET, POST).
     *
     * @return the Method of the request
     */
    public final Method getMethod() {
        return this.method;
    }

    /**
     * Retrieves the request parameters, including all values associated with
     * each parameter.
     *
     * @return a map containing all parameters and their values
     */
    public final Map<String, List<String>> getParameters() {
        return this.parms;
    }

    /**
     * Retrieves the query parameter string from the request URI.
     *
     * @return the query parameter string
     */
    public String getQueryParameterString() {
        return this.queryParameterString;
    }

    /**
     * Retrieves a temporary file bucket for storing large request bodies.
     *
     * @return a RandomAccessFile for temporary storage
     * @throws Error if an error occurs while creating the temporary file
     */
    private RandomAccessFile getTmpBucket() {
        try {
            TempFile tempFile = this.tempFileManager.createTempFile(null);
            return new RandomAccessFile(tempFile.getName(), "rw");
        } catch (Exception e) {
            throw new Error(e); // we won't recover, so throw an error
        }
    }

    /**
     * Retrieves the URI from the request.
     *
     * @return the URI of the request
     */
    public final String getUri() {
        return this.uri;
    }

    /**
     * Deduces the size of the request body in bytes. This is determined either
     * from the "content-length" header or by reading the remaining bytes after
     * the header.
     *
     * @return the size of the request body in bytes
     */
    public long getBodySize() {
        if (this.headers.containsKey("content-length")) {
            return Long.parseLong(this.headers.get("content-length"));
        } else if (this.splitbyte < this.rlen) {
            return this.rlen - this.splitbyte;
        }
        return 0;
    }

    /**
     * Parses the body of the request, handling file uploads and form data.
     * Stores file paths and form parameters in the provided 'files' map.
     *
     * @param files a map to store file paths associated with form field names
     * @throws IOException if an I/O error occurs while reading the request body
     * @throws ResponseException if there is an error in parsing the request
     * body
     */
    public void parseBody(Map<String, String> files) throws IOException, ResponseException {
        RandomAccessFile randomAccessFile = null;
        try {
            long size = getBodySize();
            ByteArrayOutputStream baos = null;
            DataOutput requestDataOutput = null;

            // Store the request in memory or a file, depending on size
            if (size < MEMORY_STORE_LIMIT) {
                baos = new ByteArrayOutputStream();
                requestDataOutput = new DataOutputStream(baos);
            } else {
                randomAccessFile = getTmpBucket();
                requestDataOutput = randomAccessFile;
            }

            // Read all the body and write it to requestDataOutput
            byte[] buf = new byte[REQUEST_BUFFER_LEN];
            while (this.rlen >= 0 && size > 0) {
                this.rlen = this.inputStream.read(buf, 0, (int) Math.min(size, REQUEST_BUFFER_LEN));
                size -= this.rlen;
                if (this.rlen > 0) {
                    requestDataOutput.write(buf, 0, this.rlen);
                }
            }

            ByteBuffer fbuf = null;
            if (baos != null) {
                fbuf = ByteBuffer.wrap(baos.toByteArray(), 0, baos.size());
            } else {
                fbuf = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
                randomAccessFile.seek(0);
            }

            // Handle POST and PUT requests
            if (Method.POST.equals(this.method)) {
                ContentType contentType = new ContentType(this.headers.get("content-type"));
                if (contentType.isMultipart()) {
                    String boundary = contentType.getBoundary();
                    if (boundary == null) {
                        throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                    }
                    decodeMultipartFormData(contentType, fbuf, this.parms, files);
                } else {
                    byte[] postBytes = new byte[fbuf.remaining()];
                    fbuf.get(postBytes);
                    String postLine = new String(postBytes, contentType.getEncoding()).trim();
                    // Handle application/x-www-form-urlencoded
                    if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType.getContentType())) {
                        decodeParms(postLine, this.parms);
                    } else if (postLine.length() != 0) {
                        // Special case for raw POST data => create a special files entry "postData" with raw content data
                        files.put(POST_DATA, postLine);
                    }
                }
            } else if (Method.PUT.equals(this.method)) {
                files.put("content", saveTmpFile(fbuf, 0, fbuf.limit(), null));
            }
        } finally {
            NanoHTTPD.safeClose(randomAccessFile);
        }
    }

    /**
     * Saves a portion of the ByteBuffer to a temporary file and returns the
     * path to the file.
     *
     * @param b the ByteBuffer containing the data to be saved
     * @param offset the offset within the buffer where the data starts
     * @param len the length of the data to be saved
     * @param filename_hint a hint for the filename (can be null)
     * @return the full path to the saved temporary file
     */
    private String saveTmpFile(ByteBuffer b, int offset, int len, String filename_hint) {
        String path = "";
        if (len > 0) {
            FileOutputStream fileOutputStream = null;
            try {
                TempFile tempFile = this.tempFileManager.createTempFile(filename_hint);
                ByteBuffer src = b.duplicate();
                fileOutputStream = new FileOutputStream(tempFile.getName());
                FileChannel dest = fileOutputStream.getChannel();
                src.position(offset).limit(offset + len);
                dest.write(src.slice());
                path = tempFile.getName();
            } catch (Exception e) {
                throw new Error(e); // we won't recover, so throw an error
            } finally {
                NanoHTTPD.safeClose(fileOutputStream);
            }
        }
        return path;
    }

    /**
     * Retrieves the remote IP address of the client making the request.
     *
     * @return the remote IP address of the client
     */
    public String getRemoteIpAddress() {
        return this.remoteIp;
    }

}
