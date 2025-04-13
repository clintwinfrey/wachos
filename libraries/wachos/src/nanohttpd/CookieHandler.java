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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides basic support for handling HTTP cookies.
 * <p>
 * This class offers rudimentary cookie management features such as setting,
 * reading, and deleting cookies. It does not support advanced cookie attributes
 * like 'path', 'secure', or 'httpOnly'. This implementation is simplistic and
 * can be extended to include additional features or improvements.
 * </p>
 * <p>
 * Note: This class is an older implementation and has known limitations and
 * flaws.
 * </p>
 */
public class CookieHandler implements Iterable<String> {

    /**
     * A map to store cookies with their names as keys and values as values.
     */
    private final HashMap<String, String> cookies = new HashMap<>();
    /**
     * A list to keep track of cookies to be sent in the response.
     */
    private final ArrayList<Cookie> queue = new ArrayList<>();

    /**
     * Constructs a CookieHandler by parsing cookies from the provided HTTP
     * headers.
     *
     * @param httpHeaders a map of HTTP headers from which cookies are extracted
     */
    public CookieHandler(Map<String, String> httpHeaders) {
        String raw = httpHeaders.get("cookie");
        if (raw != null) {
            String[] tokens = raw.split(";");
            for (String token : tokens) {
                String[] data = token.trim().split("=");
                if (data.length == 2) {
                    this.cookies.put(data[0], data[1]);
                }
            }
        }
    }

    /**
     * Sets a cookie with an expiration date from a month ago, effectively
     * deleting it on the client side.
     *
     * @param name the name of the cookie to delete
     */
    public void delete(String name) {
        set(name, "-delete-", -30);
    }

    /**
     * Returns an iterator over the names of the cookies.
     * <p>
     * This method allows iteration over the cookie names stored in this
     * handler.
     * </p>
     *
     * @return an iterator over the names of the cookies
     */
    @Override
    public Iterator<String> iterator() {
        return this.cookies.keySet().iterator();
    }

    /**
     * Reads the value of a cookie by its name from the stored cookies.
     *
     * @param name the name of the cookie to read
     * @return the value of the cookie if it exists, or {@code null} if the
     * cookie is not found
     */
    public String read(String name) {
        return this.cookies.get(name);
    }

    /**
     * Adds a cookie to the queue of cookies to be sent in the HTTP response.
     *
     * @param cookie the cookie to add to the queue
     */
    public void set(Cookie cookie) {
        this.queue.add(cookie);
    }

    /**
     * Sets a cookie with a specified name, value, and expiration time.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param expires the number of days until the cookie expires
     */
    public void set(String name, String value, int expires) {
        this.queue.add(new Cookie(name, value, Cookie.getHTTPTime(expires)));
    }

    /**
     * Internal method used by the web server to add all queued cookies to the
     * HTTP response headers.
     *
     * @param response the response object to which the queued cookies will be
     * added
     */
    public void unloadQueue(Response response) {
        for (Cookie cookie : this.queue) {
            response.addCookieHeader(cookie.getHTTPHeader());
        }
    }
}
