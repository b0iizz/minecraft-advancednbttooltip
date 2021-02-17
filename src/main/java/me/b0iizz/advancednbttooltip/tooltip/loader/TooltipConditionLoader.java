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

import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.suggest;
import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.require;

import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.tooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.tooltip.builtin.BuiltInCondition;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * The JSON loader for {@link TooltipCondition TooltipConditions}
 * 
 * @author B0IIZZ
 */
public class TooltipConditionLoader implements Loader<TooltipCondition> {

	/**
	 * The instance of this class as there is no need for multiple instances of this
	 * class
	 */
	public static final TooltipConditionLoader INSTANCE = new TooltipConditionLoader();

	// Error messages
	private static final String GENERAL_ERROR = "Exception while parsing TooltipCondition from json";
	private static final String GENERAL_UNKNOWN_ID = "Unknown TooltipCondition id %s";
	private static final String GENERAL_PARSING_ERROR = "Error while parsing %s: %s";

	private static final String CONDITION_PARSING_ERROR = "Could not load condition";
	private static final String CONDITION_ARRAY_PARSING_ERROR = "Could not load condition %s";

	private static final String ITEM_ARRAY_ERROR = "Could not load id for item %s";
	private static final String ITEM_RESOLVE_ERROR = "Could not resolve item id %s";

	/**
	 * @return All possible error messages that can be caused by this class
	 */
	public static String[] getAllErrorMessages() {
		return new String[] { GENERAL_ERROR, GENERAL_UNKNOWN_ID, GENERAL_PARSING_ERROR, CONDITION_PARSING_ERROR,
				CONDITION_ARRAY_PARSING_ERROR, ITEM_ARRAY_ERROR, ITEM_RESOLVE_ERROR };
	}

	@Override
	public TooltipCondition load(JsonObject object) {
		try {
			return loadUnsafe(object);
		} catch (Exception e) {
			throw new TooltipLoaderException(GENERAL_ERROR, e);
		}
	}

	private TooltipCondition loadUnsafe(JsonObject object) {
		String id = require(object, "id", String.class);

		try {
			switch (id) {
			case "not":
				return parseNot(object);
			case "and":
				return parseAnd(object);
			case "or":
				return parseOr(object);
			case "has_tag":
				return parseHasTag(object);
			case "tag_matches":
				return parseTagMatches(object);
			case "is_item":
				return parseIsItem(object);
			case "is_advanced_context":
				return BuiltInCondition.IS_ADVANCED_CONTEXT.create();
			case "is_hud_context":
				return BuiltInCondition.IS_HUD_CONTEXT.create();
			case "true":
				return TooltipCondition.TRUE;
			case "false":
				return TooltipCondition.FALSE;
			default:
				throw new TooltipLoaderException(String.format(GENERAL_UNKNOWN_ID, id));
			}
		} catch (Throwable t) {
			throw new TooltipLoaderException(String.format(GENERAL_PARSING_ERROR, id, t.getMessage()), t);
		}
	}

	private TooltipCondition parseNot(JsonObject object) {
		TooltipCondition condition;
		try {
			condition = TooltipCondition.LOADER.load(require(object, "condition", JsonObject.class));
		} catch (Throwable t) {
			throw new TooltipLoaderException(CONDITION_PARSING_ERROR, t);
		}

		return BuiltInCondition.NOT.create(condition);
	}

	private TooltipCondition parseAnd(JsonObject object) {
		ArrayList<TooltipCondition> parsedConditions = new ArrayList<TooltipCondition>();

		JsonArray conditions = require(object, "conditions", JsonArray.class);

		for (int i = 0; i < conditions.size(); i++) {

			TooltipCondition condition;

			try {
				condition = TooltipCondition.LOADER.load(conditions.get(i).getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException(String.format(CONDITION_ARRAY_PARSING_ERROR, i), t);
			}

			parsedConditions.add(condition);
		}

		return BuiltInCondition.AND.create((Object[]) parsedConditions.toArray());
	}

	private TooltipCondition parseOr(JsonObject object) {
		ArrayList<TooltipCondition> parsedConditions = new ArrayList<TooltipCondition>();

		JsonArray conditions = require(object, "conditions", JsonArray.class);

		for (int i = 0; i < conditions.size(); i++) {

			TooltipCondition condition;

			try {
				condition = TooltipCondition.LOADER.load(conditions.get(i).getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException(String.format(CONDITION_ARRAY_PARSING_ERROR, i), t);
			}

			parsedConditions.add(condition);
		}

		return BuiltInCondition.OR.create((Object[]) parsedConditions.toArray());
	}

	private TooltipCondition parseHasTag(JsonObject object) {
		String tag = require(object, "tag", String.class);

		Optional<Integer> typeRequirement = suggest(object, "type", Integer.class);
		if (typeRequirement.isPresent()) {
			int type = typeRequirement.orElse(0);
			return BuiltInCondition.HAS_TAG.create(tag, type);
		} else {
			return BuiltInCondition.HAS_TAG.create(tag);
		}
	}

	private TooltipCondition parseTagMatches(JsonObject object) {
		String tag = require(object, "tag", String.class);

		String value = require(object, "value", JsonElement.class).toString().replaceAll("\"^", "").replaceAll("$\"",
				"");

		return BuiltInCondition.TAG_MATCHES.create(tag, value);
	}

	private TooltipCondition parseIsItem(JsonObject object) {
		ArrayList<ItemConvertible> items = new ArrayList<>();

		JsonArray itemList = require(object, "items", JsonArray.class);

		for (int i = 0; i < itemList.size(); i++) {
			String id;
			try {
				id = itemList.get(i).getAsString();
			} catch (Throwable t) {
				throw new TooltipLoaderException(String.format(ITEM_ARRAY_ERROR, i), t);
			}
			Identifier trueId = new Identifier(id);
			if (!Registry.ITEM.containsId(trueId))
				throw new TooltipLoaderException(String.format(ITEM_RESOLVE_ERROR, id));
			items.add(Registry.ITEM.get(trueId));
		}

		return BuiltInCondition.IS_ITEM.create((Object[]) items.toArray());
	}

	private TooltipConditionLoader() {
	}

}
