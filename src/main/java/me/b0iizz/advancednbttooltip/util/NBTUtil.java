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
package me.b0iizz.advancednbttooltip.util;

import java.math.BigDecimal;

import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * A utility class to cast {@link Tag Tags} into their respective Java
 * representation
 * 
 * @author B0IIZZ
 */
public final class NBTUtil {

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a number or zero when the tag is not a number
	 */
	public static Number number(Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			return ((AbstractNumberTag) tag).getNumber();
		} else
			return 0;
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as an int or zero when the tag is not a number
	 */
	public static int asInt(Tag tag) {
		return number(tag).intValue();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a long or zero when the tag is not a number
	 */
	public static long asLong(Tag tag) {
		return number(tag).longValue();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a short or zero when the tag is not a number
	 */
	public static short asShort(Tag tag) {
		return number(tag).shortValue();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a double or zero when the tag is not a number
	 */
	public static double asDouble(Tag tag) {
		return number(tag).doubleValue();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a float or zero when the tag is not a number
	 */
	public static float asFloat(Tag tag) {
		return number(tag).floatValue();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a byte or zero when the tag is not a number
	 */
	public static byte asByte(Tag tag) {
		return number(tag).byteValue();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a String
	 */
	public static String asString(Tag tag) {
		return tag.asString();
	}

	/**
	 * @param tag the given {@link Tag}
	 * @return the given tag as a {@link CompoundTag} or an empty tag when the teg
	 *         is not an instance of {@link CompoundTag}
	 */
	public static CompoundTag asCompound(Tag tag) {
		return tag instanceof CompoundTag ? (CompoundTag) tag : new CompoundTag();
	}

	/**
	 * Returns whether a tag is equal to a String
	 * 
	 * @param tag   a {@link Tag}
	 * @param value a {@link String}
	 * @return true hen the string and the tag are equal
	 */
	public static boolean isEqualTo(Tag tag, String value) {
		if (tag == null && (value == null || value.isEmpty()))
			return true;
		if (tag == null || (value == null || value.isEmpty()))
			return false;
		if (tag instanceof AbstractNumberTag) {
			try {
				BigDecimal a = new BigDecimal(tag.toString());
				BigDecimal b = new BigDecimal(value.replaceAll("\"", ""));
				return a.equals(b);
			} catch (Exception e) {
				return false;
			}
		} else
			return ("\"" + tag.asString() + "\"").equals(value);
	}
}
