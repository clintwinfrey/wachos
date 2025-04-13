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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

/**
 * Represents an HTTP response in the NanoHTTPD framework.
 *
 * This class encapsulates the data and metadata needed to send an HTTP response
 * back to a client, including the status code, MIME type, headers, and body
 * content.
 */
public class Response implements Closeable {

    /**
     * HTTP status code after processing, e.g. "200 OK", Status.OK
     */
    private Status status;

    /**
     * MIME type of the response content, e.g. "text/html"
     */
    private String mimeType;

    /**
     * InputStream containing the data of the response, may be null.
     */
    private InputStream data;

    private final long contentLength;

    /**
     * Headers for the HTTP response. Use {@link #addHeader(String, String)} to
     * add lines. The lowercase map is automatically kept up to date.
     */
    @SuppressWarnings("serial")
    private final Map<String, String> header = new HashMap<String, String>() {

        @Override
        public String put(String key, String value) {
            lowerCaseHeader.put(key == null ? key : key.toLowerCase(), value);
            return super.put(key, value);
        }
    };

    /**
     * A copy of the header map with all keys in lowercase for faster searching.
     */
    private final Map<String, String> lowerCaseHeader = new HashMap<>();

    /**
     * The request method that spawned this response.
     */
    private Method requestMethod;

    /**
     * Flag indicating whether to use chunked transfer encoding.
     */
    private boolean chunkedTransfer;

    /**
     * Flag indicating whether to keep the connection alive after sending the
     * response.
     */
    private boolean keepAlive;

    /**
     * List of cookie headers to be sent with the response.
     */
    private final List<String> cookieHeaders;

    /**
     * Gzip usage configuration.
     */
    private GzipUsage gzipUsage = GzipUsage.DEFAULT;

    /**
     * Enum for defining gzip usage behavior.
     */
    private static enum GzipUsage {
        DEFAULT,
        ALWAYS,
        NEVER;
    }

    /**
     * Creates a response with the specified parameters. This constructor will
     * create a fixed-length response if totalBytes >= 0; otherwise, it will
     * create a chunked response.
     *
     * @param status The HTTP status of the response.
     * @param mimeType The MIME type of the response content.
     * @param data An InputStream containing the response data.
     * @param totalBytes The total number of bytes in the response body, or -1
     * for chunked transfer.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Response(Status status, String mimeType, InputStream data, long totalBytes) {
        this.status = status;
        this.mimeType = mimeType;
        if (data == null) {
            this.data = new ByteArrayInputStream(new byte[0]);
            this.contentLength = 0L;
        } else {
            this.data = data;
            this.contentLength = totalBytes;
        }
        this.chunkedTransfer = this.contentLength < 0;
        this.keepAlive = true;
        this.cookieHeaders = new ArrayList<>(10);
    }

    @Override
    public void close() throws IOException {
        if (this.data != null) {
            this.data.close();
        }
    }

    /**
     * Adds a cookie header to the list. This should not be called manually; it
     * is an internal utility.
     *
     * @param cookie The cookie header to add.
     */
    public void addCookieHeader(String cookie) {
        cookieHeaders.add(cookie);
    }

    /**
     * Retrieves all unloaded cookie headers. This method should not be called
     * manually; it is intended for internal testing purposes.
     *
     * @return A list of cookie headers.
     */
    public List<String> getCookieHeaders() {
        return cookieHeaders;
    }

    /**
     * Adds a given line to the HTTP response headers.
     *
     * @param name The name of the header.
     * @param value The value of the header.
     */
    public void addHeader(String name, String value) {
        this.header.put(name, value);
    }

    /**
     * Indicates whether to close the connection after the response has been
     * sent.
     *
     * @param close {@code true} to hint connection closing, {@code false} to
     * allow the client to close it.
     */
    public void closeConnection(boolean close) {
        if (close) {
            this.header.put("connection", "close");
        } else {
            this.header.remove("connection");
        }
    }

    /**
     * @return {@code true} if the connection is to be closed after this
     * response has been sent.
     */
    public boolean isCloseConnection() {
        return "close".equals(getHeader("connection"));
    }

    /**
     * Retrieves the InputStream containing the response data.
     *
     * @return The InputStream with the response data.
     */
    public InputStream getData() {
        return this.data;
    }

    /**
     * Retrieves the value of the specified header.
     *
     * @param name The name of the header to retrieve.
     * @return The value of the specified header, or {@code null} if not
     * present.
     */
    public String getHeader(String name) {
        return this.lowerCaseHeader.get(name.toLowerCase());
    }

    /**
     * Retrieves the MIME type of the response.
     *
     * @return The MIME type of the response.
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Retrieves the HTTP request method that generated this response.
     *
     * @return The request method.
     */
    public Method getRequestMethod() {
        return this.requestMethod;
    }

    /**
     * Retrieves the HTTP status of the response.
     *
     * @return The HTTP status.
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Sets the keep-alive status for the connection.
     *
     * @param useKeepAlive {@code true} to enable keep-alive, {@code false} to
     * disable it.
     */
    public void setKeepAlive(boolean useKeepAlive) {
        this.keepAlive = useKeepAlive;
    }

    /**
     * Sends the response to the specified OutputStream.
     *
     * @param outputStream The OutputStream to send the response to.
     */
    public void send(OutputStream outputStream) {
        SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            if (this.status == null) {
                throw new Error("sendResponse(): Status can't be null.");
            }
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, new ContentType(this.mimeType).getEncoding())), false);
            pw.append("HTTP/1.1 ").append(this.status.getDescription()).append(" \r\n");
            if (this.mimeType != null) {
                printHeader(pw, "Content-Type", this.mimeType);
            }
            if (getHeader("date") == null) {
                printHeader(pw, "Date", gmtFrmt.format(new Date()));
            }
            for (Entry<String, String> entry : this.header.entrySet()) {
                printHeader(pw, entry.getKey(), entry.getValue());
            }
            for (String cookieHeader : this.cookieHeaders) {
                printHeader(pw, "Set-Cookie", cookieHeader);
            }
            if (getHeader("connection") == null) {
                printHeader(pw, "Connection", (this.keepAlive ? "keep-alive" : "close"));
            }
            if (getHeader("content-length") != null) {
                setUseGzip(false);
            }
            if (useGzipWhenAccepted()) {
                printHeader(pw, "Content-Encoding", "gzip");
                setChunkedTransfer(true);
            }
            long pending = this.data != null ? this.contentLength : 0;
            if (this.requestMethod != Method.HEAD && this.chunkedTransfer) {
                printHeader(pw, "Transfer-Encoding", "chunked");
            } else if (!useGzipWhenAccepted()) {
                pending = sendContentLengthHeaderIfNotAlreadyPresent(pw, pending);
            }
            pw.append("\r\n");
            pw.flush();
            sendBodyWithCorrectTransferAndEncoding(outputStream, pending);
            outputStream.flush();
            NanoHTTPD.safeClose(this.data);
        } catch (IOException ioe) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not send response to the client", ioe);
        }
    }

    /**
     * Prints a header line to the specified PrintWriter.
     *
     * @param pw The PrintWriter to print to.
     * @param key The header name.
     * @param value The header value.
     */
    @SuppressWarnings("static-method")
    protected void printHeader(PrintWriter pw, String key, String value) {
        pw.append(key).append(": ").append(value).append("\r\n");
    }

    /**
     * Sends the Content-Length header if not already present.
     *
     * @param pw The PrintWriter to send headers through.
     * @param defaultSize The default size to use if the header is not present.
     * @return The actual content length to use.
     */
    protected long sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, long defaultSize) {
        String contentLengthString = getHeader("content-length");
        long size = defaultSize;
        if (contentLengthString != null) {
            try {
                size = Long.parseLong(contentLengthString);
            } catch (NumberFormatException ex) {
                NanoHTTPD.LOG.log(Level.SEVERE, "content-length was not a number: {0}", contentLengthString);
            }
        } else {
            pw.print("Content-Length: " + size + "\r\n");
        }
        return size;
    }

    /**
     * Sends the response body to the specified OutputStream with the correct
     * transfer and encoding.
     *
     * @param outputStream The OutputStream to write the body to.
     * @param pending The number of bytes to send, or -1 to send all.
     * @throws IOException If an error occurs while sending the body.
     */
    private void sendBodyWithCorrectTransferAndEncoding(OutputStream outputStream, long pending) throws IOException {
        if (this.requestMethod != Method.HEAD && this.chunkedTransfer) {
            ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(outputStream);
            sendBodyWithCorrectEncoding(chunkedOutputStream, -1);
            try {
                chunkedOutputStream.finish();
            } catch (IOException e) {
                if (this.data != null) {
                    this.data.close();
                }
            }
        } else {
            sendBodyWithCorrectEncoding(outputStream, pending);
        }
    }

    /**
     * Sends the body of the response to the specified OutputStream with the
     * correct encoding.
     *
     * @param outputStream The OutputStream to write the body to.
     * @param pending The number of bytes to send, or -1 to send all.
     * @throws IOException If an error occurs while sending the body.
     */
    private void sendBodyWithCorrectEncoding(OutputStream outputStream, long pending) throws IOException {
        if (useGzipWhenAccepted()) {
            GZIPOutputStream gzipOutputStream = null;
            try {
                gzipOutputStream = new GZIPOutputStream(outputStream);
            } catch (IOException e) {
                if (this.data != null) {
                    this.data.close();
                }
            }
            if (gzipOutputStream != null) {
                sendBody(gzipOutputStream, -1);
                gzipOutputStream.finish();
            }
        } else {
            sendBody(outputStream, pending);
        }
    }

    /**
     * Sends the body data to the specified OutputStream. The pending parameter
     * limits the maximum number of bytes sent unless it is -1, in which case
     * everything is sent.
     *
     * @param outputStream The OutputStream to send data to.
     * @param pending -1 to send everything, otherwise sets a max limit on the
     * number of bytes sent.
     * @throws IOException If an error occurs while sending the data.
     */
    private void sendBody(OutputStream outputStream, long pending) throws IOException {
        long BUFFER_SIZE = 16 * 1024;
        byte[] buff = new byte[(int) BUFFER_SIZE];
        boolean sendEverything = pending == -1;
        while (pending > 0 || sendEverything) {
            long bytesToRead = sendEverything ? BUFFER_SIZE : Math.min(pending, BUFFER_SIZE);
            int read = this.data.read(buff, 0, (int) bytesToRead);
            if (read <= 0) {
                break;
            }
            try {
                outputStream.write(buff, 0, read);
            } catch (IOException e) {
                if (this.data != null) {
                    this.data.close();
                }
            }
            if (!sendEverything) {
                pending -= read;
            }
        }
    }

    /**
     * Sets whether to use chunked transfer encoding for the response.
     *
     * @param chunkedTransfer {@code true} to use chunked transfer,
     * {@code false} otherwise.
     */
    public void setChunkedTransfer(boolean chunkedTransfer) {
        this.chunkedTransfer = chunkedTransfer;
    }

    /**
     * Sets the InputStream containing the response data.
     *
     * @param data The InputStream with the response data.
     */
    public void setData(InputStream data) {
        this.data = data;
    }

    /**
     * Sets the MIME type of the response.
     *
     * @param mimeType The MIME type to set.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Sets the request method that generated this response.
     *
     * @param requestMethod The request method to set.
     */
    public void setRequestMethod(Method requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Sets the HTTP status for this response.
     *
     * @param status The HTTP status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Creates a response with unknown length (using HTTP 1.1 chunking).
     *
     * @param status The HTTP status of the response.
     * @param mimeType The MIME type of the response content.
     * @param data An InputStream containing the response data.
     * @return A new Response instance.
     */
    public static Response newChunkedResponse(Status status, String mimeType, InputStream data) {
        return new Response(status, mimeType, data, -1);
    }

    /**
     * Creates a fixed-length response with the provided byte array.
     *
     * @param status The HTTP status of the response.
     * @param mimeType The MIME type of the response content.
     * @param data The byte array containing the response data.
     * @return A new Response instance.
     */
    public static Response newFixedLengthResponse(Status status, String mimeType, byte[] data) {
        return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(data), data.length);
    }

    /**
     * Creates a response with known length.
     *
     * @param status The HTTP status of the response.
     * @param mimeType The MIME type of the response content.
     * @param data An InputStream containing the response data.
     * @param totalBytes The total number of bytes in the response body.
     * @return A new Response instance.
     */
    public static Response newFixedLengthResponse(Status status, String mimeType, InputStream data, long totalBytes) {
        return new Response(status, mimeType, data, totalBytes);
    }

    /**
     * Creates a text response with known length.
     *
     * @param status The HTTP status of the response.
     * @param mimeType The MIME type of the response content.
     * @param txt The text to send in the response body.
     * @return A new Response instance.
     */
    public static Response newFixedLengthResponse(Status status, String mimeType, String txt) {
        ContentType contentType = new ContentType(mimeType);
        if (txt == null) {
            return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(new byte[0]), 0);
        } else {
            byte[] bytes;
            try {
                CharsetEncoder newEncoder = Charset.forName(contentType.getEncoding()).newEncoder();
                if (!newEncoder.canEncode(txt)) {
                    contentType = contentType.tryUTF8();
                }
                bytes = txt.getBytes(contentType.getEncoding());
            } catch (UnsupportedEncodingException e) {
                NanoHTTPD.LOG.log(Level.SEVERE, "encoding problem, responding nothing", e);
                bytes = new byte[0];
            }
            return newFixedLengthResponse(status, contentType.getContentTypeHeader(), new ByteArrayInputStream(bytes), bytes.length);
        }
    }

    /**
     * Create a text response with known length.
     *
     * @param msg the text of the response
     * @return a text response with known length
     */
    public static Response newFixedLengthResponse(String msg) {
        return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, msg);
    }

    /**
     * Sets whether to use gzip
     *
     * @param useGzip flag indicating if gzip should be used
     * @return this
     */
    public Response setUseGzip(boolean useGzip) {
        gzipUsage = useGzip ? GzipUsage.ALWAYS : GzipUsage.NEVER;
        return this;
    }

    /**
     * @return whether Gzip should be used when accepted
     */
    public boolean useGzipWhenAccepted() {
        if (gzipUsage == GzipUsage.DEFAULT) { //if a Gzip usage has been enforced, use it. 
            return getMimeType() != null && (getMimeType().toLowerCase().contains("text/") || getMimeType().toLowerCase().contains("/json"));
        } else { //else decide whether or not to use Gzip.
            return gzipUsage == GzipUsage.ALWAYS;
        }
    }
}
