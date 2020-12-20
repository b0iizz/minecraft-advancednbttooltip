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
package me.b0iizz.advancednbttooltip.tooltip.loader;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A small utility class for json parsing for custom tooltips
 * 
 * @author B0IIZZ
 */
class JSONUtil {

	public static final Optional<JsonElement> optional(JsonObject obj, String name) {
		return obj.has(name) ? Optional.of(obj.get(name)) : Optional.empty();
	}

	public static final JsonElement require(JsonObject obj, String name) {
		return optional(obj, name)
				.orElseThrow(() -> new TooltipLoaderException("Could not find required field: " + name));
	}

	public static final boolean castBoolean(Optional<JsonElement> optional, boolean defaultt) {
		if (!optional.isPresent())
			return defaultt;
		try {
			return optional.get().getAsBoolean();
		} catch (Throwable t) {
			return defaultt;
		}
	}
	
	public static final String castString(Optional<JsonElement> optional, String defaultt) {
		if (!optional.isPresent())
			return defaultt;
		try {
			return optional.get().getAsString();
		} catch (Throwable t) {
			return defaultt;
		}
	}

}
