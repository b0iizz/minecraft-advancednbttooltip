package me.b0iizz.advancednbttooltip.tooltip;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.tooltip.CustomTooltip.TooltipCondition;
import me.b0iizz.advancednbttooltip.tooltip.CustomTooltip.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TooltipDeserializer {

	public static CustomTooltip deserializeTooltip(JsonObject root) throws Exception {
		String name = root.get("name").getAsString();
		SerializedTooltipFactory factory = new SerializedTooltipFactory();
		
		for(JsonElement line : root.get("lines").getAsJsonArray()) {
			if(!line.isJsonObject()) throw new Exception("Wrong type in lines array in " + name);
			factory.addPattern(LinePattern.parse(line.getAsJsonObject()));
		}
		
		CustomTooltip result = new CustomTooltip(name, factory);
		
		for(JsonElement condition : root.get("conditions").getAsJsonArray()) {
			if(!condition.isJsonObject()) throw new Exception("Wrong type in conditions array in " + name);
			result.addCondition(TooltipConditionPattern.parse(condition.getAsJsonObject()));
		}
		
		return result;
	}
	
	private static class SerializedTooltipFactory implements TooltipFactory {
		
		List<LinePattern> pattern = new ArrayList<>();
		
		public void addPattern(LinePattern lp) {
			pattern.add(lp);
		}
		
		@Override
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context) {
			List<Text> result = new ArrayList<>();
			for(LinePattern lp : pattern) {
				result.add(new LiteralText(lp.getText(item,tag,context)));
			}
			return result;
		}
		
	}
	
	private static class TooltipConditionPattern {
		
		public static TooltipCondition parse(JsonObject obj) {
			String name = obj.get("name").getAsString();
			switch(name.toUpperCase()) {
			case "HAS_TAG":
				{
					return new TooltipCondition("HAS_TAG",obj.get("tag").getAsString());
				}
			case "HAS_ITEM":
				{
					List<Item> accepted = new ArrayList<>();
					try {
						obj.get("arguments").getAsJsonArray().forEach((element) -> {
							Identifier id = new Identifier(element.getAsString());
							Item i = Registry.ITEM.get(id);
							accepted.add(i);
						});
					} catch (Exception e) {}
					return new TooltipCondition("HAS_ITEM", accepted.toArray());
				}
			case "NOT":
				{
					TooltipCondition condition = parse(obj.get("condition").getAsJsonObject());
					return new TooltipCondition("NOT", condition);
				}
			case "AND":
				{
					List<TooltipCondition> args = new ArrayList<>();
					for(JsonElement element : obj.get("conditions").getAsJsonArray()) {
						if(element.isJsonObject())
							args.add(parse(element.getAsJsonObject()));
					}
					return new TooltipCondition("AND", args.toArray());
				}
			case "OR": 
				{
					List<TooltipCondition> args = new ArrayList<>();
					for(JsonElement element : obj.get("conditions").getAsJsonArray()) {
						if(element.isJsonObject())
							args.add(parse(element.getAsJsonObject()));
					}
					return new TooltipCondition("OR", args.toArray());
				}
			default:
				return TooltipCondition.FALSE;
			}
		}
		
	}
	
	private static class LinePattern {
		String pattern;
		LinePatternArgument[] arguments;
		
		public LinePattern(String pattern, LinePatternArgument[] arguments) {
			this.pattern = pattern;
			this.arguments = arguments;
		}
		
		public String getText(Item item, CompoundTag tag, TooltipContext context) {
			Object[] resolved = new Object[arguments.length];
			for(int i = 0; i < resolved.length; i++) {
				resolved[i] = arguments[i].getText(item, tag, context);
			}
			return String.format(pattern, resolved);
		}
		
		public static LinePattern parse(JsonObject obj) throws Exception {
			List<LinePatternArgument> args = new ArrayList<>();
			if(obj.get("arguments").isJsonArray()) {
				for(JsonElement element : obj.get("arguments").getAsJsonArray()) {
					if(element.isJsonObject())
						args.add(LinePatternArgument.parse(element.getAsJsonObject()));
				}
			}
			
			return new LinePattern(obj.get("pattern").getAsString(), args.toArray(new LinePatternArgument[args.size()]));
		}
	}
	
	private static class LinePatternArgument {
		
		public static LinePatternArgument parse(JsonObject obj) throws Exception{
			String type = obj.get("type").getAsString();
			switch(type.toUpperCase()) {
			case "GET_TAG":
				return GET_TAG(obj.get("tag").getAsString());
			case "CONSTANT":
				return CONSTANT(obj.get("text").getAsString());
			case "TRANSLATE_TEXT":
				List<LinePatternArgument> args = new ArrayList<>();
				if(obj.get("arguments").isJsonArray()) {
					for(JsonElement element : obj.get("arguments").getAsJsonArray()) {
						if(element.isJsonObject())
							args.add(LinePatternArgument.parse(element.getAsJsonObject()));
					}
				}
				return TRANSLATE_TEXT(obj.get("translationKey").getAsString(), args.toArray(new LinePatternArgument[args.size()]));
			default:
				return new LinePatternArgument();
			}
		}
		
		public static LinePatternArgument GET_TAG(String path) {
			return new LinePatternArgument() {
				@Override
				public String getText(Item item, CompoundTag tag, TooltipContext context) {
					return findPath(path, tag);
				}
				
				private String findPath(String p, CompoundTag tag) {
					if(tag == null) return "-";
					if(p.trim().isEmpty()) return "-";
					
					int idx;
					if((idx = p.indexOf('.')) >= 0) {
						String compound = p.substring(0, idx);
						String nextPath = p.substring(idx+1);
						
						if(!tag.getCompound(compound).isEmpty()) {
							return findPath(nextPath, tag.getCompound(compound));
						} else {
							return "-";
						}
					} else {
						try {
							return tag.get(p).asString();
						} catch (Exception e) {
							return "-";
						}
					}
				}
			};
		}
		
		public static LinePatternArgument TRANSLATE_TEXT(String key, LinePatternArgument[] args) {
			return new LinePatternArgument() {
				@Override
				public String getText(Item item, CompoundTag tag, TooltipContext context) {
					Object[] resolved = new Object[args.length];
					for(int i = 0; i < resolved.length; i++) {
						resolved[i] = args[i].getText(item, tag, context);
					}
					return I18n.translate(key, resolved);
				}
				
			};
		}
		
		public static LinePatternArgument CONSTANT(String constant) {
			return new LinePatternArgument() {
				@Override
				public String getText(Item item, CompoundTag tag, TooltipContext context) {
					return constant;
				}
				
			};
		}
		
		public String getText(Item item, CompoundTag tag, TooltipContext context) {
			return "-";
		}
		
	}
}
