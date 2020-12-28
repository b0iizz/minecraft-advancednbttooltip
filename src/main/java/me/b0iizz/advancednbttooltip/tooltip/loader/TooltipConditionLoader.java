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

import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.optional;
import static me.b0iizz.advancednbttooltip.tooltip.loader.JSONUtil.require;

import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.tooltip.api.TooltipCondition;
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

	@Override
	public TooltipCondition load(JsonObject object) {
		try {
			return loadUnsafe(object);
		} catch (Exception e) {
			throw new TooltipLoaderException("Exception while parsing TooltipCondition from json", e);
		}
	}

	private TooltipCondition loadUnsafe(JsonObject object) {
		String id;
		try {
			id = require(object, "id").getAsString();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing ??? : could not load id", t);
		}
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
		case "true":
			return TooltipCondition.TRUE;
		case "false":
			return TooltipCondition.FALSE;
		default:
			throw new TooltipLoaderException("Unknown id parsing TooltipCondition " + id);
		}
	}

	private TooltipCondition parseNot(JsonObject object) {
		TooltipCondition condition;
		try {
			condition = TooltipCondition.LOADER.load(require(object, "condition").getAsJsonObject());
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing NOT: could not load condition", t);
		}

		return TooltipCondition.builtIn("NOT", condition);
	}

	private TooltipCondition parseAnd(JsonObject object) {
		ArrayList<TooltipCondition> parsedConditions = new ArrayList<TooltipCondition>();

		JsonArray conditions;
		try {
			conditions = require(object, "conditions").getAsJsonArray();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing AND: could not load conditions", t);
		}

		for (int i = 0; i < conditions.size(); i++) {

			TooltipCondition condition;

			try {
				condition = TooltipCondition.LOADER.load(conditions.get(i).getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing AND: could not load condition " + i, t);
			}

			parsedConditions.add(condition);
		}

		return TooltipCondition.builtIn("AND", parsedConditions.toArray());
	}

	private TooltipCondition parseOr(JsonObject object) {
		ArrayList<TooltipCondition> parsedConditions = new ArrayList<TooltipCondition>();

		JsonArray conditions;
		try {
			conditions = require(object, "conditions").getAsJsonArray();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing OR: could not load conditions", t);
		}

		for (int i = 0; i < conditions.size(); i++) {

			TooltipCondition condition;

			try {
				condition = TooltipCondition.LOADER.load(conditions.get(i).getAsJsonObject());
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing OR: could not load condition " + i, t);
			}

			parsedConditions.add(condition);
		}

		return TooltipCondition.builtIn("OR", parsedConditions.toArray());
	}

	private TooltipCondition parseHasTag(JsonObject object) {
		String tag;
		try {
			tag = require(object, "tag").getAsString();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing HAS_TAG: could not load tag", t);
		}

		Optional<JsonElement> typeRequirement = optional(object, "type");
		if (typeRequirement.isPresent()) {
			int type;
			try {
				type = typeRequirement.get().getAsInt();
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing HAS_TAG: could not load type", t);
			}
			return TooltipCondition.builtIn("HAS_TAG", tag, type);
		} else {
			return TooltipCondition.builtIn("HAS_TAG", tag);
		}
	}
	
	private TooltipCondition parseTagMatches(JsonObject object) {
		String tag;
		try {
			tag = require(object, "tag").getAsString();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing TAG_MATCHES: could not load tag", t);
		}
		
		String value;
		try {
			value = require(object, "value").toString().replaceAll("\"^", "").replaceAll("$\"", "");
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing TAG_MATCHES: could not load value", t);
		}
		
		return TooltipCondition.builtIn("TAG_MATCHES", tag, value);
	}
	
	private TooltipCondition parseIsItem(JsonObject object) {
		ArrayList<ItemConvertible> items = new ArrayList<>();

		JsonArray itemList;
		try {
			itemList = require(object, "items").getAsJsonArray();
		} catch (Throwable t) {
			throw new TooltipLoaderException("Exception while parsing IS_ITEM: could not load items", t);
		}

		for (int i = 0; i < itemList.size(); i++) {
			String id;
			try {
				id = itemList.get(i).getAsString();
			} catch (Throwable t) {
				throw new TooltipLoaderException("Exception while parsing IS_ITEM: could not load id for item " + i, t);
			}
			Identifier trueId = new Identifier(id);
			if (!Registry.ITEM.containsId(trueId))
				throw new TooltipLoaderException("Could not resolve item id " + id);
			items.add(Registry.ITEM.get(trueId));
		}

		return TooltipCondition.builtIn("IS_ITEM", items.toArray());
	}

	private TooltipConditionLoader() {
	}

}
