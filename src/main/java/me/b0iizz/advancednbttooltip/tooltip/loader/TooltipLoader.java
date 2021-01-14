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

import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.require;

import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.tooltip.CustomTooltip;
import me.b0iizz.advancednbttooltip.tooltip.api.AbstractCustomTooltip;
import me.b0iizz.advancednbttooltip.tooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.tooltip.api.TooltipFactory;

/**
 * The JSON loader for {@link CustomTooltip CustomTooltips}
 * 
 * @author B0IIZZ
 */
public class TooltipLoader implements Loader<AbstractCustomTooltip> {

	/**
	 * The instance of this class as there is no need for multiple instances of this
	 * class
	 */
	public static final TooltipLoader INSTANCE = new TooltipLoader();

	// Error messages
	private static final String GENERAL_ERROR = "Exception while parsing CustomTooltip from json";
	private static final String GENERAL_TEXT_ERROR = "Could not load root TooltipFactory (text) object";
	private static final String GENERAL_CONDITION_ERROR = "Could not load root condition";

	/**
	 * @return All possible error messages that can be caused by this utility class
	 */
	public static String[] getAllErrorMessages() {
		return new String[] { GENERAL_ERROR, GENERAL_TEXT_ERROR, GENERAL_CONDITION_ERROR};
	}
	
	@Override
	public AbstractCustomTooltip load(JsonObject object) {
		try {
			return loadUnsafe(object);
		} catch (Exception e) {
			throw new TooltipLoaderException(GENERAL_ERROR, e);
		}
	}

	private CustomTooltip loadUnsafe(JsonObject object) {
		String id = require(object, "id", String.class);

		TooltipFactory factory;
		try {
			factory = TooltipFactory.LOADER.load(require(object, "text", JsonObject.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(GENERAL_TEXT_ERROR, t);
		}

		TooltipCondition condition;
		try {
			condition = TooltipCondition.LOADER.load(require(object, "condition", JsonObject.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(GENERAL_CONDITION_ERROR, t);
		}

		return new CustomTooltip(id, factory).addCondition(condition);
	}

	private TooltipLoader() {
	}

}
