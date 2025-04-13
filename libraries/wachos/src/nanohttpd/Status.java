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

/**
 * Enumeration representing HTTP response status codes and their descriptions.
 * This enum contains standard HTTP status codes as defined by the IETF and
 * related specifications.
 */
public enum Status {

    /**
     * 101 Switching Protocols: The server is switching protocols as requested
     * by the client.
     */
    SWITCH_PROTOCOL(101, "Switching Protocols"),
    /**
     * 200 OK: The request has succeeded.
     */
    OK(200, "OK"),
    /**
     * 201 Created: The request has been fulfilled and resulted in a new
     * resource being created.
     */
    CREATED(201, "Created"),
    /**
     * 202 Accepted: The request has been accepted for processing, but the
     * processing is not complete.
     */
    ACCEPTED(202, "Accepted"),
    /**
     * 204 No Content: The server successfully processed the request and is not
     * returning any content.
     */
    NO_CONTENT(204, "No Content"),
    /**
     * 206 Partial Content: The server is delivering only part of the resource
     * due to a range header sent by the client.
     */
    PARTIAL_CONTENT(206, "Partial Content"),
    /**
     * 207 Multi-Status: The message body contains multiple status codes.
     */
    MULTI_STATUS(207, "Multi-Status"),
    /**
     * 301 Moved Permanently: The requested resource has been assigned a new
     * permanent URI.
     */
    REDIRECT(301, "Moved Permanently"),
    /**
     * 302 Found: The requested resource resides temporarily under a different
     * URI.
     * <p>
     * Note: Many user agents mishandle 302 in ways that violate the RFC1945
     * spec (i.e., redirect a POST to a GET). 303 and 307 were added in RFC2616
     * to address this. You should prefer 303 and 307 unless the calling user
     * agent does not support 303 and 307 functionality.
     * </p>
     */
    @Deprecated
    FOUND(302, "Found"),
    /**
     * 303 See Other: The response to the request can be found under a different
     * URI using the GET method.
     */
    REDIRECT_SEE_OTHER(303, "See Other"),
    /**
     * 304 Not Modified: The resource has not been modified since the last
     * request.
     */
    NOT_MODIFIED(304, "Not Modified"),
    /**
     * 307 Temporary Redirect: The requested resource resides temporarily under
     * a different URI.
     * <p>
     * The client should use the original request method for future requests.
     * </p>
     */
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    /**
     * 400 Bad Request: The server cannot process the request due to a client
     * error.
     */
    BAD_REQUEST(400, "Bad Request"),
    /**
     * 401 Unauthorized: Authentication is required and has failed or has not
     * yet been provided.
     */
    UNAUTHORIZED(401, "Unauthorized"),
    /**
     * 403 Forbidden: The server understands the request, but it refuses to
     * authorize it.
     */
    FORBIDDEN(403, "Forbidden"),
    /**
     * 404 Not Found: The requested resource could not be found.
     */
    NOT_FOUND(404, "Not Found"),
    /**
     * 405 Method Not Allowed: The request method is not supported for the
     * requested resource.
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    /**
     * 406 Not Acceptable: The requested resource is capable of generating only
     * content not acceptable according to the Accept headers sent in the
     * request.
     */
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    /**
     * 408 Request Timeout: The server timed out waiting for the request.
     */
    REQUEST_TIMEOUT(408, "Request Timeout"),
    /**
     * 409 Conflict: The request could not be completed due to a conflict with
     * the current state of the resource.
     */
    CONFLICT(409, "Conflict"),
    /**
     * 410 Gone: The requested resource is no longer available and will not be
     * available again.
     */
    GONE(410, "Gone"),
    /**
     * 411 Length Required: The server refuses to accept the request without a
     * defined Content-Length header.
     */
    LENGTH_REQUIRED(411, "Length Required"),
    /**
     * 412 Precondition Failed: One or more conditions given in the request
     * header fields evaluated to false.
     */
    PRECONDITION_FAILED(412, "Precondition Failed"),
    /**
     * 413 Payload Too Large: The request is larger than the server is willing
     * or able to process.
     */
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    /**
     * 415 Unsupported Media Type: The request entity has a media type which the
     * server or resource does not support.
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    /**
     * 416 Requested Range Not Satisfiable: The server cannot serve the
     * requested range.
     */
    RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    /**
     * 417 Expectation Failed: The server cannot meet the requirements of the
     * Expect request-header field.
     */
    EXPECTATION_FAILED(417, "Expectation Failed"),
    /**
     * 429 Too Many Requests: The user has sent too many requests in a given
     * amount of time.
     */
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    /**
     * 500 Internal Server Error: A generic error message indicating the server
     * encountered an unexpected condition.
     */
    INTERNAL_ERROR(500, "Internal Server Error"),
    /**
     * 501 Not Implemented: The server does not support the functionality
     * required to fulfill the request.
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),
    /**
     * 503 Service Unavailable: The server cannot handle the request due to
     * temporary overloading or maintenance.
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    /**
     * 505 HTTP Version Not Supported: The server does not support the HTTP
     * protocol version that was used in the request.
     */
    UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported");
    /**
     * The numerical status code for the HTTP response.
     */
    private final int requestStatus;
    /**
     * The description associated with the HTTP status code.
     */
    private final String description;

    /**
     * Constructs a Status enum with the specified code and description.
     *
     * @param requestStatus The HTTP status code.
     * @param description A description of the status.
     */
    Status(int requestStatus, String description) {
        this.requestStatus = requestStatus;
        this.description = description;
    }

    /**
     * Looks up a Status by its numerical code.
     *
     * @param requestStatus The HTTP status code to look up.
     * @return The corresponding Status enum, or null if no match is found.
     */
    public static Status lookup(int requestStatus) {
        for (Status status : Status.values()) {
            if (status.getRequestStatus() == requestStatus) {
                return status;
            }
        }
        return null;
    }

    /**
     * Returns a description of the status, including the status code.
     *
     * @return A string representation of the status code and description.
     */
    public String getDescription() {
        return "" + this.requestStatus + " " + this.description;
    }

    /**
     * Returns the numerical status code of this HTTP status.
     *
     * @return The HTTP status code.
     */
    public int getRequestStatus() {
        return this.requestStatus;
    }
}
