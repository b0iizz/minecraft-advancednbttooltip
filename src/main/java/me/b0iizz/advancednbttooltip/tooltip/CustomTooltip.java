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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import me.b0iizz.advancednbttooltip.ModUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

/**
 * The class representation of a custom tooltip. It is used to determine
 *  whether this specific tooltip should be applied or not and what it 
 *  actually appends to the items tooltip.
 * 
 * @author B0IIZZ
 */
public final class CustomTooltip {

	/**
	 * The identifier for this tooltip used for registration.
	 */
	public final String name;
	
	/**
	 * Provider of the actual tooltip text.
	 */
	public final TooltipFactory tooltipProvider;
	
	/**
	 * List of conditions which have to be met to show the custom tooltip.
	 */
	public final List<TooltipCondition> conditions; 
	
	/**
	 * 
	 * @param name The identifier for this tooltip used for registration. Generally UPPER CASE.
	 * @param tooltipProvider Provider of the actual tooltip text.
	 */
	public CustomTooltip(String name, TooltipFactory tooltipProvider) {
		this.name = name;
		this.tooltipProvider = tooltipProvider;
		conditions = new ArrayList<>();
	}
	
	/**
	 * Adds a {@link TooltipCondition Condition} which has to be met to show the tooltip.
	 * 
	 * @param resolver Provider of the situation specific boolean output of this {@link TooltipCondition Condition}.
	 * @return The original {@link CustomTooltip} object. Generally used for chaining.
	 */
	public CustomTooltip addCondition(ConditionResolver resolver) {
		addCondition(new TooltipCondition(resolver));
		return this;
	}
	
	/**
	 * Adds a pre-defined {@link TooltipCondition Condition} which has to be met to show the tooltip.
	 * 
	 * @param conditionName : One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
	 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"}
	 * @param args : The appropriate arguments for the specific condition.
	 * @return The original {@link CustomTooltip} object. Generally used for chaining.
	 */
	public CustomTooltip addCondition(String conditionName, Object... args) {
		addCondition(new TooltipCondition(conditionName, args));
		return this;
	}
	
	/**
	 * Adds a {@link TooltipCondition Condition} which has to be met to show the tooltip.
	 * 
	 * @param condition The condition to be added.
	 * @return The original {@link CustomTooltip} object. Generally used for chaining.
	 */
	public CustomTooltip addCondition(TooltipCondition condition) {
		conditions.add(condition);
		return this;
	}
	
	/**
	 * Decides whether the tooltip should be applied or not.
	 * 
	 * @param item The {@link Item} the tooltip will be added to.
	 * @param tag The Item's {@link CompoundTag NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return <b>true</b>, when the custom tooltip should be appended to the pre-existing tooltip. <b>false</b>, when not.
	 */
	public boolean isTooltipVisible(Item item, CompoundTag tag, TooltipContext context) {
		for(TooltipCondition condition : conditions) {
			if(!condition.resolver.isConditionMet(item, tag, context)) return false;
		}
		return true;
	}
	
	
	/**
	 * Creates the tooltip text for the Item.
	 * 
	 * @param item The {@link Item} the tooltip will be added to.
	 * @param tag The Item's {@link CompoundTag NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return A {@link List} of {@link Text} to be applied to the Item's tooltip.
	 */
	public List<Text> makeTooltip(Item item, CompoundTag tag, TooltipContext context) {
		if(isTooltipVisible(item, tag, context)) {
			return tooltipProvider.createTooltip(item, tag, context);
		} else  {
			return tooltipProvider.createTooltipWhenDisabled(item, tag, context);
		}
	}
	
	/**
	 * An interface used for providing the actual {@link CustomTooltip custom tooltip} text for an {@link Item}.
	 * <br>A lambda implementation is recommended.
	 * 
	 * @author B0IIZZ
	 */
	public static interface TooltipFactory {
		
		/**
		 * Creates the tooltip text for the Item when it should be displayed.
		 * 
		 * <br> A lambda implementation is recommended.
		 * 
		 * @param item The {@link Item} the tooltip will be added to.
		 * @param tag The Item's {@link CompoundTag NBT-tag}.
		 * @param context The current {@link TooltipContext}.
		 * @return A {@link List} of {@link Text} to be applied to the Item's tooltip.
		 */
		public List<Text> createTooltip(Item item, CompoundTag tag, TooltipContext context);
		
		/**
		 * Creates the tooltip text for the Item when it should not be displayed.
		 * 
		 * <br> Overwriting this method is generally not needed.
		 * 
		 * @param item The {@link Item} the tooltip will be added to.
		 * @param tag The Item's {@link CompoundTag NBT-tag}.
		 * @param context The current {@link TooltipContext}.
		 * @return A {@link List} of {@link Text} to be applied to the Item's tooltip.
		 */
		public default List<Text> createTooltipWhenDisabled(Item item, CompoundTag tag, TooltipContext context) {
			return new ArrayList<Text>();
		}
		
	}
	
	/**
	 * An interface used to restrict the visibility of a tooltip.
	 * 
	 * @author B0IIZZ
	 */
	public static interface ConditionResolver {
		
		/**
		 * Decides whether the tooltip should be applied or not.
		 * 
		 * @param item The {@link Item} the tooltip will be added to.
		 * @param tag The Item's {@link CompoundTag NBT-tag}.
		 * @param context The current {@link TooltipContext}.
		 * @return Whether the tooltip should be displayed.
		 */
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context);
		
	}
	
	/**
	 * A class used to restrict the visibility of a tooltip.
	 * 
	 * @author B0IIZZ
	 */
	public static final class TooltipCondition {
		
		/**
		 * Condition which is always true
		 */
		public static final TooltipCondition TRUE = new TooltipCondition((i,t,c) -> true);
		
		/**
		 * Condition which is always false
		 */
		public static final TooltipCondition FALSE = new TooltipCondition((i,t,c) -> false);
		
		/**
		 * Used to index custom conditions.
		 */
		private static int counter = 1;
		
		/**
		 * The identifier for this condition.
		 */
		public final String name;
		
		/**
		 * Provider of the situation specific boolean output of this condition.
		 */
		public final ConditionResolver resolver;
		
		/**
		 * @param resolver : Provider of the situation specific boolean output of this condition.
		 */
		public TooltipCondition(ConditionResolver resolver) {
			this.name = "custom_" + counter++;
			this.resolver = resolver;
		}
		
		/**
		 * Used for built-in {@link ConditionResolver ConditionResolvers}.
		 * 
		 * @param name : One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
		 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
		 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"}
		 * @param args : The appropriate arguments for the specific condition.
		 */
		public TooltipCondition(String name, Object... args) {
			switch(name) {
			case "HAS_TAG":
				this.name=name;
				if(args.length > 1) {
					this.resolver = new HasTagConditionResolver((String) args[0], (int) args[1]);
				} else {
					this.resolver = new HasTagConditionResolver((String) args[0]);
				}
				break;
			case "HAS_ITEM":
				this.name=name;
				this.resolver = new HasItemConditionResolver(ModUtils.castArray(args,ItemConvertible[].class));
				break;
			case "AND":
				this.name = name;
				this.resolver = new AndConditionResolver(ModUtils.castArray(args,TooltipCondition[].class));
				break;
			case "OR":
				this.name = name;
				this.resolver = new OrConditionResolver(ModUtils.castArray(args,TooltipCondition[].class));
				break;
			case "NOT":
				this.name = name;
				this.resolver = new NotConditionResolver((TooltipCondition) args[0]);
				break;
			default:
				this.name = "custom_" + counter++ + "_always_false";
				this.resolver = (item,tag,context) -> {return false;};
				break;
			}
			
		}
		
		/**
		 * Decides whether the tooltip should be applied or not.
		 * 
		 * @param item The {@link Item} the tooltip will be added to.
		 * @param tag The Item's {@link CompoundTag NBT-tag}.
		 * @param context The current {@link TooltipContext}.
		 * @return Whether the tooltip should be displayed.
		 */
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return resolver.isConditionMet(item, tag, context);
		}
		
		
		/**
		 * {@link ConditionResolver ConditionResolver} which activates when the {@link Item} has a specific {@link CompoundTag NBT-tag} property.
		 * <br>
		 * <br><b>Arguments:</b> 
		 * <br>{@link String} <b>id</b> : The path to the required {@link CompoundTag NBT-tag} from the root tag.
		 * <br>{@link Integer int} <b>type</b> (optional) : The {@link net.minecraft.nbt.Tag type} of property.
		 * <br>
		 * <br>One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
		 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
		 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in {@link ConditionResolver ConditionResolvers}. 
		 * 
		 * @author B0IIZZ
		 */
		public static class HasTagConditionResolver implements ConditionResolver {

			private String id;
			private int type;
			
			/**
			 * @param id : The path to the required {@link CompoundTag NBT-tag} from the root tag.
			 */
			public HasTagConditionResolver(String id) {
				this.id = id;
				this.type = 99;
			}
			
			/**
			 * @param id : The path to the required {@link CompoundTag NBT-tag} from the root tag.
			 * @param type : The {@link net.minecraft.nbt.Tag type} of property.
			 */
			public HasTagConditionResolver(String id, int type) {
				this.id = id;
				this.type = type;
			}
			
			@Override
			public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
				return findPath(id, tag);
			}
			
			/**
			 * Traverses the specified path through recursion.
			 */
			private boolean findPath(String path, CompoundTag tag) {
				if(tag == null) return false;
				if(path.trim().isEmpty()) return true;
				
				int idx;
				if((idx = path.indexOf('.')) >= 0) {
					String compound = path.substring(0, idx);
					String nextPath = path.substring(idx+1);
					
					if(!tag.getCompound(compound).isEmpty()) {
						return findPath(nextPath, tag.getCompound(compound));
					} else {
						return false;
					}
				}
				
				if(type == 99) return tag.getType(path) > 0;
				return tag.getType(path) == type;
			}
			
		}
		
		/**
		 * {@link ConditionResolver ConditionResolver} which activates when a specific {@link Item} is detected.
		 * <br>
		 * <br><b>Arguments:</b> 
		 * <br>{@link Item Item[]} <b>items</b> : The {@link Item Items} for which the condition will be met.
		 * <br>
		 * <br>One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
		 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
		 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in {@link ConditionResolver ConditionResolvers}. 
		 * 
		 * @author B0IIZZ
		 */
		public static class HasItemConditionResolver implements ConditionResolver {
			
			/**
			 * The {@link Item Items} for which the condition will be met.
			 */
			private Item[] allowedItems;
			
			/**
			 * @param items : The {@link ItemConvertible Items} for which the condition will be met.
			 */
			public HasItemConditionResolver(ItemConvertible... items) {
				this.allowedItems = Stream.of(items).map((itemc) -> itemc.asItem()).toArray((i) -> new Item[i]);
			}
			
			@Override
			public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
				return getAllowedItems().contains(item);
			}
			
			/**
			 * @return The {@link Item Items} for which the {@link TooltipCondition.HasItemConditionResolver condition} will be met.
			 */
			public List<Item> getAllowedItems() {
				return Arrays.asList(allowedItems);
			}
			
		}
		
		/**
		 * {@link ConditionResolver ConditionResolver} which activates when all {@link TooltipCondition child conditions} are met.
		 * <br>
		 * <br><b>Arguments:</b> 
		 * <br>{@link TooltipCondition TooltipCondition[]} <b>conditions</b> : The List of required {@link TooltipCondition conditions}.
		 * <br>
		 * <br>One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
		 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
		 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in {@link ConditionResolver ConditionResolvers}. 
		 * 
		 * @author B0IIZZ
		 */
		public static class AndConditionResolver implements ConditionResolver {
			
			/**
			 * The array of required {@link TooltipCondition conditions}.
			 */
			private TooltipCondition[] conditions;
			
			/**
			 * @param conditions : The Collection of required {@link TooltipCondition conditions}.
			 */
			public AndConditionResolver(TooltipCondition... conditions) {
				this.conditions = conditions;
			}
			
			@Override
			public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
				for(TooltipCondition condition : conditions) {
					if(!condition.resolver.isConditionMet(item, tag, context)) return false;
				}
				return true;
			}
		}
		
		/**
		 * {@link ConditionResolver ConditionResolver} which activates when one {@link TooltipCondition child condition} is met.
		 * <br>
		 * <br><b>Arguments:</b> 
		 * <br>{@link TooltipCondition TooltipCondition[]} <b>conditions</b> : The List of combined {@link TooltipCondition conditions}.
		 * <br>
		 * <br>One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
		 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
		 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in {@link ConditionResolver ConditionResolvers}. 
		 * 
		 * @author B0IIZZ
		 */
		public static class OrConditionResolver implements ConditionResolver {
			
			/**
			 * The array of combined {@link TooltipCondition conditions}.
			 */
			private TooltipCondition[] conditions;
			
			/**
			 * @param conditions : The Collection of combined {@link TooltipCondition conditions}.
			 */
			public OrConditionResolver(TooltipCondition... conditions) {
				this.conditions = conditions;
			}
			
			@Override
			public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
				for(TooltipCondition condition : conditions) {
					if(condition.resolver.isConditionMet(item, tag, context)) return true;
				}
				return false;
			}
		}
		
		/**
		 * {@link ConditionResolver ConditionResolver} which activates when the {@link TooltipCondition child condition} is not met.
		 * <br>
		 * <br><b>Arguments:</b> 
		 * <br>{@link TooltipCondition} <b>condition</b> : The {@link TooltipCondition child condition}.
		 * <br>
		 * <br>One of {@link TooltipCondition.HasTagConditionResolver "HAS_TAG"},
		 * {@link TooltipCondition.HasItemConditionResolver "HAS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
		 * {@link TooltipCondition.OrConditionResolver "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in {@link ConditionResolver ConditionResolvers}. 
		 * 
		 * @author B0IIZZ
		 */
		public static class NotConditionResolver implements ConditionResolver {
			
			/**
			 * The {@link TooltipCondition child condition}.
			 */
			private TooltipCondition condition;
			
			/**
			 * @param condition : The {@link TooltipCondition child condition}.
			 */
			public NotConditionResolver(TooltipCondition condition) {
				this.condition = condition;
			}
			
			@Override
			public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
				return !condition.isConditionMet(item, tag, context);
			}
		}
		
	}
	
}
