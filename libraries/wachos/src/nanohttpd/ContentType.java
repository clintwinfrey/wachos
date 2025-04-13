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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the content type of an HTTP request or response.
 * <p>
 * This class parses and stores information related to the 'Content-Type' HTTP
 * header, including the MIME type, character encoding, and boundary information
 * for multipart data.
 * </p>
 */
public class ContentType {

    /**
     * The default character encoding used when none is specified.
     */
    private static final String ASCII_ENCODING = "US-ASCII";
    /**
     * The value indicating a multipart form-data content type.
     */
    private static final String MULTIPART_FORM_DATA_HEADER = "multipart/form-data";
    /**
     * Regular expression for matching MIME type in the content type header.
     */
    private static final String CONTENT_REGEX = "[ |\t]*([^/^ ^;^,]+/[^ ^;^,]+)";
    /**
     * Pattern for matching MIME type in the content type header.
     */
    private static final Pattern MIME_PATTERN = Pattern.compile(CONTENT_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * Regular expression for matching charset in the content type header.
     */
    private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
    /**
     * Pattern for matching charset in the content type header.
     */
    private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * Regular expression for matching boundary in the content type header.
     */
    private static final String BOUNDARY_REGEX = "[ |\t]*(boundary)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
    /**
     * Pattern for matching boundary in the content type header.
     */
    private static final Pattern BOUNDARY_PATTERN = Pattern.compile(BOUNDARY_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * The raw content type header value.
     */
    private final String contentTypeHeader;
    /**
     * The MIME type extracted from the content type header.
     */
    private final String contentType;
    /**
     * The character encoding extracted from the content type header.
     */
    private final String encoding;
    /**
     * The boundary parameter for multipart form-data, if applicable.
     */
    private final String boundary;

    /**
     * Constructs a ContentType instance by parsing the given content type
     * header.
     *
     * @param contentTypeHeader the raw value of the Content-Type header
     */
    public ContentType(String contentTypeHeader) {
        this.contentTypeHeader = contentTypeHeader;
        if (contentTypeHeader != null) {
            contentType = getDetailFromContentHeader(contentTypeHeader, MIME_PATTERN, "", 1);
            encoding = getDetailFromContentHeader(contentTypeHeader, CHARSET_PATTERN, null, 2);
        } else {
            contentType = "";
            encoding = "UTF-8";
        }
        if (MULTIPART_FORM_DATA_HEADER.equalsIgnoreCase(contentType)) {
            boundary = getDetailFromContentHeader(contentTypeHeader, BOUNDARY_PATTERN, null, 2);
        } else {
            boundary = null;
        }
    }

    /**
     * Extracts a specific detail from the content type header using the
     * provided pattern.
     *
     * @param contentTypeHeader the raw value of the Content-Type header
     * @param pattern the pattern to use for extracting the detail
     * @param defaultValue the default value to return if the detail is not
     * found
     * @param group the group number in the pattern to return
     * @return the extracted detail or the default value if not found
     */
    private String getDetailFromContentHeader(String contentTypeHeader, Pattern pattern, String defaultValue, int group) {
        Matcher matcher = pattern.matcher(contentTypeHeader);
        return matcher.find() ? matcher.group(group) : defaultValue;
    }

    /**
     * Returns the raw Content-Type header value.
     *
     * @return the raw Content-Type header value
     */
    public String getContentTypeHeader() {
        return contentTypeHeader;
    }

    /**
     * Returns the MIME type extracted from the Content-Type header.
     *
     * @return the MIME type, or an empty string if not found
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the character encoding extracted from the Content-Type header. If
     * no encoding is specified, returns the default ASCII encoding.
     *
     * @return the character encoding, or {@value #ASCII_ENCODING} if not
     * specified
     */
    public String getEncoding() {
        return encoding == null ? ASCII_ENCODING : encoding;
    }

    /**
     * Returns the boundary parameter for multipart form-data, if applicable.
     *
     * @return the boundary, or {@code null} if not applicable
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Checks if the Content-Type header indicates multipart form-data.
     *
     * @return {@code true} if the Content-Type is "multipart/form-data",
     * {@code false} otherwise
     */
    public boolean isMultipart() {
        return MULTIPART_FORM_DATA_HEADER.equalsIgnoreCase(contentType);
    }

    /**
     * Returns a new ContentType instance with UTF-8 encoding if the current
     * encoding is not specified.
     *
     * @return a new ContentType instance with UTF-8 encoding if encoding was
     * not originally set, otherwise returns the current instance
     */
    public ContentType tryUTF8() {
        if (encoding == null) {
            return new ContentType(this.contentTypeHeader + "; charset=UTF-8");
        }
        return this;
    }
}
