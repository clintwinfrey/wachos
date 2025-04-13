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
 * Represents a factory for creating instances of a specified type, with the
 * capability to throw an exception during the creation process.
 * <p>
 * This interface extends the basic factory concept by allowing the 'create'
 * method to throw a specified type of exception. This can be useful in
 * scenarios where object creation may fail due to various reasons, and you need
 * to handle or propagate those exceptions in a controlled manner.
 * </p>
 *
 * @param <T> The type of object that this factory will create.
 * @param <E> The base type of exceptions that may be thrown during object
 * creation. It must be a subtype of {@link Throwable}.
 */
public interface IFactoryThrowing<T, E extends Throwable> {

    /**
     * Creates a new instance of type {@code T}.
     * <p>
     * This method provides the logic for creating a new object of the specified
     * type, but it may also throw an exception of type {@code E} if the
     * creation process fails or encounters an issue.
     * </p>
     *
     * @return a new instance of type {@code T}
     * @throws E if an error occurs during object creation.
     */
    T create() throws E;
}
