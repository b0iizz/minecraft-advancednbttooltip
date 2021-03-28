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
package me.b0iizz.advancednbttooltip.misc.loader;

import static me.b0iizz.advancednbttooltip.misc.loader.JSONUtil.require;
import static me.b0iizz.advancednbttooltip.misc.loader.JSONUtil.suggest;

import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.api.impl.BuiltInFactory;
import me.b0iizz.advancednbttooltip.util.NBTPath;
import net.minecraft.util.Formatting;

/**
 * The JSON loader for {@link TooltipFactory TooltipFactories}
 * 
 * @author B0IIZZ
 */
public class TooltipFactoryLoader implements Loader<TooltipFactory> {

	/**
	 * The instance of this class as there is no need for multiple instances of this
	 * class
	 */
	public static final TooltipFactoryLoader INSTANCE = new TooltipFactoryLoader();

	// Error messages
	private static final String GENERAL_ERROR = "Exception while parsing TooltipFactory from json";
	private static final String GENERAL_UNKNOWN_ID = "Unknown TooltipFactory id %s";
	private static final String GENERAL_PARSING_ERROR = "Error while parsing %s: %s";

	private static final String TEXT_PARSING_ERROR = "Could not load required field text";
	private static final String TEXT_ARRAY_PARSING_ERROR = "Could not load text %s";

	private static final String ARGUMENTPROVIDER_PARSING_ERROR = "Could not load argument_provider";

	private static final String CONDITIONAL_SUCCESS_PARSING_ERROR = "Could not load success case text";
	private static final String CONDITIONAL_FAIL_PARSING_ERROR = "Could not load fail case text";
	private static final String CONDITION_PARSING_ERROR = "Could not load condition";

	/**
	 * @return All possible error messages that can be caused by this class
	 */
	public static String[] getAllErrorMessages() {
		return new String[] { GENERAL_ERROR, GENERAL_UNKNOWN_ID, GENERAL_PARSING_ERROR, TEXT_PARSING_ERROR,
				TEXT_ARRAY_PARSING_ERROR, ARGUMENTPROVIDER_PARSING_ERROR, CONDITIONAL_SUCCESS_PARSING_ERROR,
				CONDITIONAL_FAIL_PARSING_ERROR, CONDITION_PARSING_ERROR };
	}

	@Override
	public TooltipFactory load(JsonElement object) {
		try {
			return loadUnsafe(object);
		} catch (Exception e) {
			throw new TooltipLoaderException(GENERAL_ERROR, e);
		}
	}

	private TooltipFactory loadUnsafe(JsonElement element) {
		if (!element.isJsonObject()) {
			return parseImplicit(element);
		}

		JsonObject object = element.getAsJsonObject();

		String id = require(object, "id", String.class);

		try {
			switch (id) {
			case "literal":
				return parseLiteral(object);
			case "formatted":
				return parseFormatted(object);
			case "translated":
				return parseTranslated(object);
			case "nbt":
				return parseNBT(object);
			case "nbt_size":
				return parseNBTSize(object);
			case "conditional":
				return parseConditional(object);
			case "multiple":
				return parseMultiple(object);
			case "mix":
				return parseMix(object);
			case "effect":
				return parseEffect(object);
			case "limit":
				return parseLimit(object);
			case "limit_lines":
				return parseLimitLines(object);
			case "builtin_suspicious_stew":
				return BuiltInFactory.BUILTIN_SUSPICIOUS_STEW.create();
			case "builtin_signs":
				return BuiltInFactory.BUILTIN_SIGNS.create();
			case "builtin_hideflags":
				return BuiltInFactory.BUILTIN_HIDEFLAGS.create();
			case "empty":
				return TooltipFactory.EMPTY;
			default:
				throw new TooltipLoaderException(String.format(GENERAL_UNKNOWN_ID, id));
			}
		} catch (Throwable t) {
			throw new TooltipLoaderException(String.format(GENERAL_PARSING_ERROR, id, t.getMessage()), t);
		}
	}

	private TooltipFactory parseImplicit(JsonElement element) {
		return BuiltInFactory.LITERAL.create(element.getAsString());
	}

	private TooltipFactory parseLiteral(JsonObject object) {
		String text;
		text = require(object, "text", String.class);

		return BuiltInFactory.LITERAL.create(text);
	}

	private TooltipFactory parseFormatted(JsonObject object) {
		TooltipFactory factory;
		try {
			factory = TooltipFactory.LOADER.load(require(object, "text", JsonElement.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(TEXT_PARSING_ERROR, t);
		}

		ArrayList<Formatting> formattings = new ArrayList<>();

		boolean bold = suggest(object, "bold", Boolean.class).orElse(false);
		boolean italic = suggest(object, "italic", Boolean.class).orElse(false);
		boolean strikethrough = suggest(object, "strikethrough", Boolean.class).orElse(false);
		boolean underline = suggest(object, "underline", Boolean.class).orElse(false);
		boolean obfuscated = suggest(object, "obfuscated", Boolean.class).orElse(false);

		String colorName = suggest(object, "color", String.class).orElse("");

		if (!colorName.isEmpty() && Formatting.byName(colorName) != null && Formatting.byName(colorName).isColor())
			formattings.add(Formatting.byName(colorName));
		if (bold)
			formattings.add(Formatting.BOLD);
		if (italic)
			formattings.add(Formatting.ITALIC);
		if (strikethrough)
			formattings.add(Formatting.STRIKETHROUGH);
		if (underline)
			formattings.add(Formatting.UNDERLINE);
		if (obfuscated)
			formattings.add(Formatting.OBFUSCATED);

		final Formatting[] allFormattings = formattings.toArray(new Formatting[0]);
		return BuiltInFactory.FORMATTED.create(factory, (Object) allFormattings);
	}

	private TooltipFactory parseTranslated(JsonObject object) {
		String key = require(object, "key", String.class);

		Optional<JsonElement> argsProvider = suggest(object, "argument_provider", JsonElement.class);
		if (argsProvider.isPresent()) {
			TooltipFactory provider;

			try {
				provider = TooltipFactory.LOADER.load(argsProvider.get());
			} catch (Throwable t) {
				throw new TooltipLoaderException(ARGUMENTPROVIDER_PARSING_ERROR, t);
			}

			return BuiltInFactory.TRANSLATED.create(key, provider);
		} else {
			return BuiltInFactory.TRANSLATED.create(key);
		}

	}

	private TooltipFactory parseNBT(JsonObject object) {
		String path = require(object, "tag", String.class);

		final NBTPath nbtpath = new NBTPath(path);

		int flags = suggest(object, "flags", int.class).orElse(0);

		boolean colored = suggest(object, "colored", boolean.class).orElse(false);

		return BuiltInFactory.NBT.create(nbtpath, flags, colored);
	}

	private TooltipFactory parseNBTSize(JsonObject object) {
		String path = require(object, "tag", String.class);

		final NBTPath nbtpath = new NBTPath(path);

		return BuiltInFactory.NBT_SIZE.create(nbtpath);
	}

	private TooltipFactory parseConditional(JsonObject object) {
		TooltipFactory trueFactory;
		try {
			trueFactory = TooltipFactory.LOADER.load(require(object, "success", JsonElement.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(CONDITIONAL_SUCCESS_PARSING_ERROR, t);
		}

		TooltipFactory falseFactory;
		try {
			falseFactory = TooltipFactory.LOADER.load(require(object, "fail", JsonElement.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(CONDITIONAL_FAIL_PARSING_ERROR, t);
		}

		TooltipCondition condition;
		try {
			condition = TooltipCondition.LOADER.load(require(object, "condition", JsonElement.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(CONDITION_PARSING_ERROR, t);
		}

		return BuiltInFactory.CONDITIONAL.create(trueFactory, falseFactory, condition);
	}

	private TooltipFactory parseMultiple(JsonObject object) {
		ArrayList<TooltipFactory> parsedFactories = new ArrayList<>();

		JsonArray factories = require(object, "texts", JsonArray.class);

		for (int i = 0; i < factories.size(); i++) {

			TooltipFactory condition;

			try {
				condition = TooltipFactory.LOADER.load(factories.get(i));
			} catch (Throwable t) {
				throw new TooltipLoaderException(String.format(TEXT_ARRAY_PARSING_ERROR, i), t);
			}

			parsedFactories.add(condition);
		}

		return BuiltInFactory.MULTIPLE.create(parsedFactories.toArray());
	}

	private TooltipFactory parseMix(JsonObject object) {
		ArrayList<TooltipFactory> parsedFactories = new ArrayList<>();

		JsonArray factories = require(object, "texts", JsonArray.class);

		for (int i = 0; i < factories.size(); i++) {

			TooltipFactory condition;

			try {
				condition = TooltipFactory.LOADER.load(factories.get(i));
			} catch (Throwable t) {
				throw new TooltipLoaderException(String.format(TEXT_ARRAY_PARSING_ERROR, i), t);
			}

			parsedFactories.add(condition);
		}

		return BuiltInFactory.MIX.create(parsedFactories.toArray());
	}

	private TooltipFactory parseEffect(JsonObject object) {
		byte rawId = require(object, "rawId", byte.class);
		int duration = require(object, "duration", int.class);
		int strength = suggest(object, "strength", int.class).orElse(0);

		return BuiltInFactory.EFFECT.create(rawId, duration, strength);
	}

	private TooltipFactory parseLimit(JsonObject object) {
		TooltipFactory factory;
		try {
			factory = TooltipFactory.LOADER.load(require(object, "text", JsonElement.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(TEXT_PARSING_ERROR, t);
		}

		int limit = require(object, "length", int.class);

		return BuiltInFactory.LIMIT.create(factory, limit);
	}

	private TooltipFactory parseLimitLines(JsonObject object) {
		TooltipFactory factory;
		try {
			factory = TooltipFactory.LOADER.load(require(object, "text", JsonElement.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(TEXT_PARSING_ERROR, t);
		}

		int limit = require(object, "limit", int.class);

		return BuiltInFactory.LIMIT_LINES.create(factory, limit);
	}

	private TooltipFactoryLoader() {
	}

}
