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
 * Enum representing the HTTP request methods.
 *
 * This enum includes common HTTP methods and provides functionality to decode a
 * string representation of a method back to its enum value.
 */
public enum Method {

    /**
     * Request data from a specified resource.
     */
    GET,
    /**
     * Update a resource with the provided data.
     */
    PUT,
    /**
     * Submit data to be processed to a specified resource.
     */
    POST,
    /**
     * Remove the specified resource.
     */
    DELETE,
    /**
     * Same as GET, but returns only headers.
     */
    HEAD,
    /**
     * Describe the communication options for the target resource.
     */
    OPTIONS,
    /**
     * Perform a message loop-back test along the path to the target resource.
     */
    TRACE,
    /**
     * Establish a tunnel to the server identified by the target resource.
     */
    CONNECT,
    /**
     * Apply partial modifications to a resource.
     */
    PATCH,
    /**
     * Retrieve properties of a resource.
     */
    PROPFIND,
    /**
     * Change and delete multiple properties on a resource.
     */
    PROPPATCH,
    /**
     * Create a new collection (directory).
     */
    MKCOL,
    /**
     * Move a resource from one location to another.
     */
    MOVE,
    /**
     * Create a copy of a resource at a new location.
     */
    COPY,
    /**
     * Lock a resource to prevent other clients from modifying it.
     */
    LOCK,
    /**
     * Unlock a resource to allow modifications.
     */
    UNLOCK,
    /**
     * Notify a subscriber of an event.
     */
    NOTIFY,
    /**
     * Subscribe to receive notifications of changes.
     */
    SUBSCRIBE;

    /**
     * Looks up a Method enum value based on a string representation.
     *
     * @param method The string representation of the HTTP method.
     * @return The corresponding Method enum value, or null if the method is
     * invalid.
     */
    public static Method lookup(String method) {
        if (method == null) {
            return null;
        }

        try {
            return valueOf(method);
        } catch (IllegalArgumentException e) {
            // TODO: Log it?
            return null;
        }
    }
}
