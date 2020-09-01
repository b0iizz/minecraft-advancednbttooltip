/*	MIT License
	
	Copyright (c) 2020 b0iizz
	
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
package me.b0iizz.advancednbttooltip;

import java.util.Arrays;

/**
 * A utility class used for methods which do not have any other place and have some kind of general relevance.
 * 
 * @author B0IIZZ
 */
public class ModUtils {
	
	/**
	 * Shortcut for creating a new array and then casting all its children to a specific type.
	 * <br>
	 * <br><b>Example:</b><br>
	 * <code>
	 * 	<br>Object[] array = new Object[] {new Foo("bar"), new Foo("baz")};
	 * 	<br>Foo[] newArray = castArray(array, Foo[].class);
	 * 	<br>newArray[0].specificFooMethod();
	 * </code>
	 * @param <T> : The class the array will consist of.
	 * @param array : The Array to be transformed into an array of the other class.
	 * @param newType : The class the array will be transformed into.
	 * @return The transformed array
	 */
	
	public static <T> T[] castArray(Object[] array, Class<T[]> newType) {
		return Arrays.copyOf(array, array.length, newType);
	}
	
}
