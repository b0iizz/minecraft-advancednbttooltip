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
package me.b0iizz.advancednbttooltip.tooltip;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.tooltip.api.AbstractCustomTooltip;
import me.b0iizz.advancednbttooltip.tooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.tooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.tooltip.builtin.BuiltInCondition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * A class for loading {@link CustomTooltip CustomTooltips} from a Json file.
 * 
 * @author B0IIZZ
 * @deprecated since 1.1.0
 */
@Deprecated()
public class TooltipDeserializer {

	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Builds a {@link CustomTooltip} from a json object.
	 * For an example please visit <a href="https://github.com/b0iizz/minecraft-advancednbttooltip/blob/master/tooltip_examples/example_tooltip.json">my GitHub page</a>.
	 * 
	 * @param root The root json of the tooltip
	 * @return The parsed custom tooltip
	 * @throws Exception when an error in the parsing process occurs.
	 */
	public static AbstractCustomTooltip deserializeTooltip(JsonObject root) throws Exception {
		String name = root.get("name").getAsString();
		
		//Logging
		logType(name);
		
		DeserializedTooltipFactory factory = new DeserializedTooltipFactory();
		DeserializedLine[] lines = deserializeLines(root);
		for (DeserializedLine line : lines)
			factory.lines.add(line);

		AbstractCustomTooltip result = new CustomTooltip(name, factory);

		TooltipCondition[] conditions = deserializeConditions(root);
		for (TooltipCondition condition : conditions)
			result.addCondition(condition);

		//Logging
		retractPath();
		
		return result;
	}

	private static DeserializedLine[] deserializeLines(JsonObject obj) throws Exception {
		if (!obj.has("lines") || !obj.get("lines").isJsonArray())
			return new DeserializedLine[0];
		JsonArray array = require(obj,"lines").getAsJsonArray();

		DeserializedLine[] result = new DeserializedLine[array.size()];
		for (int i = 0; i < result.length; i++) {
			//Logging
			logArray(i);
			
			result[i] = deserializeLine(array.get(i));
			
			//Logging
			retractPath();
		}
		return result;
	}

	private static DeserializedLine deserializeLine(JsonElement element) throws Exception {
		if (!element.isJsonObject())
			throw new IllegalArgumentException("A line has to be a JSON Object!");
		JsonObject obj = element.getAsJsonObject();

		String pattern = require(obj,"pattern").getAsString();

		DeserializedLineArgument[] arguments = deserializeLineArguments(obj);

		TooltipCondition[] conditions = deserializeConditions(obj);
		TooltipCondition condition = BuiltInCondition.forName("AND", (Object[]) conditions);

		return new DeserializedLine(pattern, condition, arguments);
	}

	private static DeserializedLineArgument[] deserializeLineArguments(JsonObject obj) throws Exception {
		if (!obj.has("arguments") || !obj.get("arguments").isJsonArray())
			return new DeserializedLineArgument[0];
		JsonArray array = require(obj,"arguments").getAsJsonArray();

		DeserializedLineArgument[] result = new DeserializedLineArgument[array.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = deserializeLineArgument(array.get(i));
		}
		return result;
	}

	private static TooltipCondition[] deserializeConditions(JsonObject obj) throws Exception {
		if (!obj.has("conditions") || !obj.get("conditions").isJsonArray())
			return new TooltipCondition[0];
		JsonArray array = require(obj,"conditions").getAsJsonArray();

		TooltipCondition[] result = new TooltipCondition[array.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = deserializeCondition(array.get(i));
		}
		return result;
	}

	private static TooltipCondition deserializeCondition(JsonElement element) throws Exception {
		if (!element.isJsonObject())
			throw new IllegalArgumentException("A condition has to be a JSON Object!");
		JsonObject obj = element.getAsJsonObject();

		String type = require(obj,"type").getAsString().toUpperCase();
		
		//Logging
		logType(type);
		
		switch (type) {
		case "HAS_TAG": {
			//Logging
			retractPath();
			
			return BuiltInCondition.forName(type, require(obj,"tag").getAsString());
		}
		case "HAS_ITEM": {
			List<Item> items = new ArrayList<>();
			require(obj,"items").getAsJsonArray().forEach((jsonId) -> {
				Identifier id = new Identifier(jsonId.getAsString());
				Item item = Registry.ITEM.get(id);
				items.add(item);
			});
			
			//Logging
			retractPath();
			
			return BuiltInCondition.forName(type, items.toArray());
		}
		case "NOT": {
			TooltipCondition condition = deserializeCondition(require(obj,"condition"));
			
			//Logging
			retractPath();
			
			return BuiltInCondition.forName(type, condition);
		}
		case "AND": {
			TooltipCondition[] conditions = deserializeConditions(obj);
			
			//Logging
			retractPath();
			
			return BuiltInCondition.forName(type, (Object[]) conditions);
		}
		case "OR": {
			TooltipCondition[] conditions = deserializeConditions(obj);
			
			//Logging
			retractPath();
			
			return BuiltInCondition.forName(type, (Object[]) conditions);
		}
		case "TRUE": {
			
			//Logging
			retractPath();
			
			return TooltipCondition.TRUE;
		}
		case "FALSE": {
			
			//Logging
			retractPath();
			
			return TooltipCondition.FALSE;
		}
		default:
			throw new IllegalArgumentException("unknown condition type: " + type);
		}
	}

	private static DeserializedLineArgument deserializeLineArgument(JsonElement element) throws Exception {
		if (!element.isJsonObject())
			throw new IllegalArgumentException("A line argument has to be a JSON Object!");
		JsonObject obj = element.getAsJsonObject();

		String type = require(obj,"type").getAsString().toUpperCase();
		
		//Logging
		logType(type);
		
		switch (type) {
		case "GET_TAG": {
			String path = require(obj,"tag").getAsString();
			
			//Logging
			retractPath();
			
			return new DeserializedLineArgument() {
				public String getText(Item item, CompoundTag tag, TooltipContext context) {
					return tracePath(path, tag);
				}

				private String tracePath(String p, CompoundTag tag) {
					if (tag == null)
						return "";
					if (p.trim().isEmpty())
						return "";

					int idx;
					if ((idx = p.indexOf('.')) >= 0) {
						String compound = p.substring(0, idx);
						String nextPath = p.substring(idx + 1);

						if (!tag.getCompound(compound).isEmpty()) {
							return tracePath(nextPath, tag.getCompound(compound));
						} else {
							return "";
						}
					} else {
						try {
							return tag.get(p).asString();
						} catch (Exception e) {
							return "";
						}
					}
				}
			};
		}
		case "PATTERN": {
			DeserializedLine line = deserializeLine(obj);
			
			//Logging
			retractPath();
			
			return (item,tag,context) -> line.shouldDisplay(item, tag, context) ? line.getText(item, tag, context) : "";
		}
		case "TRANSLATE_TEXT": {
			String translationKey = require(obj,"translationKey").getAsString();

			DeserializedLineArgument[] arguments;
			if (obj.has("arguments")) {
				arguments = deserializeLineArguments(obj);
			} else {
				arguments = new DeserializedLineArgument[0];
			}

			//Logging
			retractPath();
			
			return (item, tag, context) -> {
				Object[] args = new Object[arguments.length];
				for (int i = 0; i < args.length; i++) {
					args[i] = arguments[i].getText(item, tag, context);
				}
				return I18n.translate(translationKey, args);
			};
		}
		case "FORMATTED": {
			DeserializedLineArgument argument = deserializeLineArgument(require(obj,"argument"));

			boolean bold = obj.has("bold") ? obj.get("bold").getAsBoolean() : false;
			boolean italic = obj.has("italic") ? obj.get("italic").getAsBoolean() : false;
			boolean strikethrough = obj.has("strikethrough") ? obj.get("strikethrough").getAsBoolean() : false;
			boolean underline = obj.has("underline") ? obj.get("underline").getAsBoolean() : false;
			boolean obfuscated = obj.has("obfuscated") ? obj.get("obfuscated").getAsBoolean() : false;

			String color = obj.has("color") ? obj.get("color").getAsString() : "";

			//Logging
			retractPath();
			
			return (item, tag, context) -> {
				String text = argument.getText(item, tag, context);

				if (bold)
					text = Formatting.BOLD + text;
				if (italic)
					text = Formatting.ITALIC + text;
				if (strikethrough)
					text = Formatting.STRIKETHROUGH + text;
				if (underline)
					text = Formatting.UNDERLINE + text;
				if (obfuscated)
					text = Formatting.OBFUSCATED + text;

				if (!color.isEmpty() && Formatting.byName(color) != null && Formatting.byName(color).isColor())
					text = Formatting.byName(color) + text;

				return Formatting.RESET + text + Formatting.RESET;
			};

		}
		case "CONDITIONAL": {
			TooltipCondition condition = deserializeCondition(require(obj,"condition"));

			DeserializedLineArgument success;
			if (obj.has("success")) {
				success = deserializeLineArgument(obj.get("success"));
			} else {
				success = (i, t, c) -> "";
			}

			DeserializedLineArgument fail;
			if (obj.has("fail")) {
				fail = deserializeLineArgument(obj.get("fail"));
			} else {
				fail = (i, t, c) -> "";
			}

			//Logging
			retractPath();
			
			return (item, tag, context) -> condition.isConditionMet(item, tag, context)
					? success.getText(item, tag, context)
					: fail.getText(item, tag, context);

		}
		case "CONSTANT": {
			String text = require(obj,"text").getAsString();

			//Logging
			retractPath();
			
			return (item, tag, context) -> text;
		}
		default:
			//Logging
			retractPath();
			throw new IllegalArgumentException("unknown line argument type: " + type);
		}
	}

	private static class DeserializedTooltipFactory implements TooltipFactory {

		List<DeserializedLine> lines = new ArrayList<>();

		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			List<Text> result = new ArrayList<>();
			for (DeserializedLine line : lines) {
				if (line.shouldDisplay(item, tag, context))
					result.add(new LiteralText(line.getText(item, tag, context)));
			}
			return result;
		}

	}

	private static class DeserializedLine {

		final String pattern;
		TooltipCondition condition;

		DeserializedLineArgument[] arguments;

		public DeserializedLine(String pattern, TooltipCondition condition, DeserializedLineArgument[] arguments) {
			this.pattern = pattern;
			this.condition = condition;
			this.arguments = arguments;
		}

		public boolean shouldDisplay(Item item, CompoundTag tag, TooltipContext context) {
			return condition.isConditionMet(item, tag, context);
		}

		public String getText(Item item, CompoundTag tag, TooltipContext context) {
			Object[] args = new Object[arguments.length];
			for (int i = 0; i < args.length; i++) {
				args[i] = arguments[i].getText(item, tag, context);
			}
			return String.format(pattern, args);
		}

	}

	private static interface DeserializedLineArgument {

		public String getText(Item item, CompoundTag tag, TooltipContext context);

	}

	private static JsonElement require(JsonObject obj, String memberName) {
		if(!obj.has(memberName)) throw new NullPointerException("Could not find " + memberName + " in json object although it is required!");
		return obj.get(memberName);
	}
	
	private static Deque<String> pathStack = new LinkedList<>();
	
	static {
		pathStack.push("ROOT");
	}
	
	private static void retractPath() {
		pathStack.pop();
	}
	
	private static void logPath() {
		String text = "";
		for(String element : pathStack) {
			text = element + (text.isEmpty() ? "" : ".") + text;
		}
		
		LOGGER.log(Level.INFO,"Deserializing " + text);
	}
	
	private static void logArray(int index) {
		pathStack.push("pattern[" + index + "]");
		logPath();
	}
	
	private static void logType(String name) {
		pathStack.push(name);
		logPath();
	}
}
