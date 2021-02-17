/*	MIT License
	
	Copyright (c) 2020-present b0iizz
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/
package me.b0iizz.advancednbttooltip.tooltip.loader;

/**
 * An exception dedicated to errors while parsing CustomTooltips
 * 
 * @author B0IIZZ
 */
public class TooltipLoaderException extends RuntimeException {

	private static final long serialVersionUID = 5186432346317060766L;

	private static final String DEFAULT_ERROR = "An exception occurred while parsing a CustomTooltip!";

	/**
	 * Constructs a new tooltip loader exception with <i>"An exception occurred
	 * while parsing a CustomTooltip!"</i> as its detail message. The cause is not
	 * initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 */
	public TooltipLoaderException() {
		this(DEFAULT_ERROR);
	}

	/**
	 * Constructs a new tooltip loader exception with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a call
	 * to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for later
	 *                retrieval by the {@link #getMessage()} method.
	 */
	public TooltipLoaderException(String message) {
		this(message, null);
	}

	/**
	 * Constructs a new tooltip loader exception with the specified detail message
	 * and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this runtime exception's detail message.
	 *
	 * @param message the detail message (which is saved for later retrieval by the
	 *                {@link #getMessage()} method).
	 * @param cause   the cause (which is saved for later retrieval by the
	 *                {@link #getCause()} method). (A <code>null</code> value is
	 *                permitted, and indicates that the cause is nonexistent or
	 *                unknown.)
	 */
	public TooltipLoaderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new tooltip loader exception with the detail message of <i>"An
	 * exception occurred while parsing a CustomTooltip!"</i> and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this runtime exception's detail message.
	 *
	 * @param cause the cause (which is saved for later retrieval by the
	 *              {@link #getCause()} method). (A <code>null</code> value is
	 *              permitted, and indicates that the cause is nonexistent or
	 *              unknown.)
	 */
	public TooltipLoaderException(Throwable cause) {
		this(DEFAULT_ERROR, cause);
	}
}
