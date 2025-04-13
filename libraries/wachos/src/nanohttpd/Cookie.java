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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents an HTTP cookie.
 * <p>
 * This class encapsulates the details of an HTTP cookie, including its name,
 * value, and expiration time. It provides methods to generate the appropriate
 * HTTP header string for the cookie and to calculate expiration dates.
 * </p>
 */
public class Cookie {

    /**
     * Generates an HTTP time string for the given number of days from the
     * current date.
     * <p>
     * This method creates a date string in the format used for HTTP cookies,
     * which includes the day of the week, day of the month, month, year, time,
     * and timezone (GMT).
     * </p>
     *
     * @param days the number of days from now to set the cookie expiration date
     * @return the HTTP time string representing the expiration date
     */
    public static String getHTTPTime(int days) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * The name of the cookie.
     */
    private final String n;
    /**
     * The value of the cookie.
     */
    private final String v;
    /**
     * The expiration date of the cookie in HTTP date format.
     */
    private final String e;

    /**
     * Constructs a new Cookie with the specified name and value, and sets the
     * default expiration to 30 days.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     */
    public Cookie(String name, String value) {
        this(name, value, 30);
    }

    /**
     * Constructs a new Cookie with the specified name, value, and expiration
     * date in days.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param numDays the number of days from now until the cookie expires
     */
    public Cookie(String name, String value, int numDays) {
        this.n = name;
        this.v = value;
        this.e = getHTTPTime(numDays);
    }

    /**
     * Constructs a new Cookie with the specified name, value, and explicit
     * expiration date.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param expires the expiration date of the cookie in HTTP date format
     */
    public Cookie(String name, String value, String expires) {
        this.n = name;
        this.v = value;
        this.e = expires;
    }

    /**
     * Returns the HTTP header string for this cookie.
     * <p>
     * The header string includes the cookie name, value, and expiration date
     * formatted for use in HTTP headers.
     * </p>
     *
     * @return the HTTP header string for the cookie
     */
    public String getHTTPHeader() {
        String fmt = "%s=%s; expires=%s";
        return String.format(fmt, this.n, this.v, this.e);
    }
}
