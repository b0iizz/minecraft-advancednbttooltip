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

import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.castBoolean;
import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.castString;
import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.optional;
import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.require;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.tooltip.CustomTooltip.TooltipCondition;
import me.b0iizz.advancednbttooltip.tooltip.CustomTooltip.TooltipFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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

	@Override
	public TooltipFactory load(JsonObject object) {
		try {
			return loadUnsafe(object);
		} catch (Exception e) {
			throw new TooltipLoaderException("Exception while parsing TooltipFactory from json", e);
		}
	}

	private TooltipFactory loadUnsafe(JsonObject object) {
		String id;
		try {
			id = require(object, "id").getAsString();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing ??? : could not load id", t);
		}
		switch (id) {
		case "literal":
			return parseLiteral(object);
		case "formatted":
			return parseFormatted(object);
		case "translated":
			return parseTranslated(object);
		case "conditional":
			return parseConditional(object);
		case "multiple":
			return parseMultiple(object);
		case "mix":
			return parseMix(object);
		case "empty":
			return (i, t, c) -> Arrays.asList();
		default:
			throw new TooltipLoaderException("Unknown id parsing TooltipFactory " + id);
		}
	}

	private TooltipFactory parseLiteral(JsonObject object) {
		String text;
		try {
			text = require(object, "text").getAsString();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing LITERAL: could not load text", t);
		}

		return (i, t, c) -> Arrays.asList(new LiteralText(text));
	}

	private TooltipFactory parseFormatted(JsonObject object) {
		TooltipFactory factory;
		try {
			factory = TooltipFactory.LOADER.load(require(object, "text").getAsJsonObject());
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing FORMATTED: could not load text", t);
		}

		ArrayList<Formatting> formattings = new ArrayList<>();

		boolean bold = castBoolean(optional(object, "bold"), false);
		boolean italic = castBoolean(optional(object, "italic"), false);
		boolean strikethrough = castBoolean(optional(object, "strikethrough"), false);
		boolean underline = castBoolean(optional(object, "underline"), false);
		boolean obfuscated = castBoolean(optional(object, "obfuscated"), false);

		String colorName = castString(optional(object, "color"), "");

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
		return (i, t, c) -> {
			return factory.createTooltip(i, t, c).stream().map((text) -> text.shallowCopy().formatted(allFormattings))
					.collect(Collectors.toList());
		};
	}

	private TooltipFactory parseTranslated(JsonObject object) {
		String key;
		try {
			key = require(object, "key").getAsString();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing TRANSLATED: could not load key", t);
		}

		Optional<JsonElement> argsProvider = optional(object, "argument_provider");
		if (argsProvider.isPresent()) {
			TooltipFactory provider;

			try {
				provider = TooltipFactory.LOADER.load(argsProvider.get().getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing TRANSLATED: could not load argument_provider",
						t);
			}

			return (i, t, c) -> {
				List<Text> args = provider.createTooltip(i, t, c);
				return Arrays.asList(new TranslatableText(key, args.stream().map(Text::asString).toArray()));
			};
		} else {
			return (i, t, c) -> Arrays.asList(new TranslatableText(key));
		}

	}

	private TooltipFactory parseConditional(JsonObject object) {
		TooltipFactory trueFactory;
		try {
			trueFactory = TooltipFactory.LOADER.load(require(object, "success").getAsJsonObject());
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing CONDITIONAL: could not load success case text",
					t);
		}

		TooltipFactory falseFactory;
		try {
			falseFactory = TooltipFactory.LOADER.load(require(object, "fail").getAsJsonObject());
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing CONDITIONAL: could not load fail case text", t);
		}

		TooltipCondition condition;
		try {
			condition = TooltipCondition.LOADER.load(require(object, "condition").getAsJsonObject());
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing CONDITIONAL: could not load condition", t);
		}

		return (i, t, c) -> (condition.isConditionMet(i, t, c) ? trueFactory : falseFactory).createTooltip(i, t, c);
	}

	private TooltipFactory parseMultiple(JsonObject object) {
		ArrayList<TooltipFactory> parsedFactories = new ArrayList<>();

		JsonArray factories;
		try {
			factories = require(object, "texts").getAsJsonArray();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing MULTIPLE: could not load texts", t);
		}

		for (int i = 0; i < factories.size(); i++) {

			TooltipFactory condition;

			try {
				condition = TooltipFactory.LOADER.load(factories.get(i).getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing MULTIPLE: could not load text " + i, t);
			}

			parsedFactories.add(condition);
		}

		return (i, t, c) -> {
			ArrayList<Text> res = new ArrayList<>();
			for (TooltipFactory factory : parsedFactories) {
				res.addAll(factory.createTooltip(i, t, c));
			}
			return res;
		};
	}

	private TooltipFactory parseMix(JsonObject object) {
		ArrayList<TooltipFactory> parsedFactories = new ArrayList<>();

		JsonArray factories;
		try {
			factories = require(object, "texts").getAsJsonArray();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing MIX: could not load texts", t);
		}

		for (int i = 0; i < factories.size(); i++) {

			TooltipFactory condition;

			try {
				condition = TooltipFactory.LOADER.load(factories.get(i).getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing MIX: could not load text " + i, t);
			}

			parsedFactories.add(condition);
		}

		return (i, t, c) -> {

			ArrayList<Text> list = new ArrayList<>();
			for (TooltipFactory factory : parsedFactories) {
				list.addAll(factory.createTooltip(i, t, c));
			}

			if(list.isEmpty()) return Arrays.asList();
			
			Text res = list.stream().reduce(new LiteralText(""), ((a, b) -> a.shallowCopy().append(b).append(new LiteralText(" "))));
			return Arrays.asList(res);
		};
	}

	private TooltipFactoryLoader() {
	}

}
