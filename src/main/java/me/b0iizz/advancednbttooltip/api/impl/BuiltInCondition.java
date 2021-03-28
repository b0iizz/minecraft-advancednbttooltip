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
package me.b0iizz.advancednbttooltip.api.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.gui.HudTooltipRenderer.HudTooltipContext;
import me.b0iizz.advancednbttooltip.util.NBTPath;
import me.b0iizz.advancednbttooltip.util.NBTUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;

/**
 * An enumeration of built-in {@link TooltipCondition TooltipConditions}
 * 
 * @author B0IIZZ
 */
public enum BuiltInCondition {
	/**
	 * A condition which is true when all of its children {@link TooltipCondition
	 * TooltipConditions} are true.<br>
	 * <br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipCondition TooltipCondition[]} conditions - The children of the
	 * {@link TooltipCondition}<br>
	 */
	AND(AndConditionResolver::new),
	/**
	 * A condition which is true when the currently checked {@link Item} is part of
	 * a specified list.<br>
	 * <br>
	 * <b>Parameters: </b><br>
	 * {@link Item Item[]} items - The list of {@link Item Items}<br>
	 */
	IS_ITEM(IsItemConditionResolver::new),
	/**
	 * A condition which is true when the currently checked {@link Item} has a
	 * specified {@link net.minecraft.nbt.Tag Tag}.<br>
	 * <br>
	 * <b>Parameters: </b><br>
	 * {@link String} path - The path of the required {@link net.minecraft.nbt.Tag
	 * Tag}<br>
	 * {@link Integer int} type - <i>(optional)</i> The type of
	 * {@link net.minecraft.nbt.Tag Tag} the specified path has to lead to.<br>
	 */
	HAS_TAG(HasTagConditionResolver::new),
	/**
	 * A condition which is true when a specified {@link net.minecraft.nbt.Tag Tag}
	 * of the currently checked {@link Item} has a certain value.<br>
	 * <br>
	 * <b>Parameters: </b><br>
	 * {@link String} path - The path of the required {@link net.minecraft.nbt.Tag
	 * Tag}<br>
	 * {@link String} value - The expected value<br>
	 */
	TAG_MATCHES(TagMatchesConditionResolver::new),
	/**
	 * A condition which is true when its child {@link TooltipCondition} is
	 * false.<br>
	 * <br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipCondition TooltipCondition[]} conditions - The children of the
	 * {@link TooltipCondition}<br>
	 */
	NOT(NotConditionResolver::new),
	/**
	 * A condition which is true when any of its children {@link TooltipCondition
	 * TooltipConditions} are true.<br>
	 * <br>
	 * <b>Parameters: </b><br>
	 * {@link TooltipCondition TooltipCondition[]} conditions - The children of the
	 * {@link TooltipCondition}<br>
	 */
	OR(OrConditionResolver::new),
	/**
	 * A condition which is true when {@link TooltipContext#isAdvanced()
	 * TooltipContext.isAdvanced()} returns true.
	 */
	IS_ADVANCED_CONTEXT(AdvancedContextConditionResolver::new),
	/**
	 * A condition which is true when the Tooltip is shown using the HUD feature.
	 */
	IS_HUD_CONTEXT(HudContextConditionResolver::new);

	/**
	 * Creates a pre-defined {@link TooltipCondition Condition}.
	 * 
	 * @param conditionName : See {@link BuiltInCondition}
	 * @param args          : The appropriate arguments for the specific condition.
	 * @return A new {@link TooltipCondition}
	 */
	public static final TooltipCondition forName(String conditionName, Object... args) {
		return valueOf(conditionName).create(args);
	}

	private final Function<Object[], TooltipCondition> constructor;

	private BuiltInCondition(Function<Object[], TooltipCondition> constructor) {
		this.constructor = constructor;
	}

	/**
	 * Creates a new instance of the built-in {@link TooltipCondition}
	 * 
	 * @param args The arguments required for the {@link TooltipCondition}
	 * @return the new {@link TooltipCondition}
	 */
	public TooltipCondition create(Object... args) {
		return constructor.apply(args);
	}

	/**
	 * See {@link BuiltInCondition#AND}
	 * 
	 * @author B0IIZZ
	 */
	protected static class AndConditionResolver implements TooltipCondition {

		private TooltipCondition[] conditions;

		/**
		 * @param conditions : The Collection of required {@link TooltipCondition
		 *                   conditions}.
		 */
		public AndConditionResolver(TooltipCondition... conditions) {
			this.conditions = conditions;
		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public AndConditionResolver(Object... args) {
			this(Arrays.copyOf(args, args.length, TooltipCondition[].class));
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			for (TooltipCondition condition : conditions) {
				if (!condition.isConditionMet(item, tag, context))
					return false;
			}
			return true;
		}
	}

	/**
	 * See {@link BuiltInCondition#IS_ITEM}
	 * 
	 * @author B0IIZZ
	 */
	protected static class IsItemConditionResolver implements TooltipCondition {

		private Item[] allowedItems;

		/**
		 * @param items : The {@link ItemConvertible Items} for which the condition will
		 *              be met.
		 */
		public IsItemConditionResolver(ItemConvertible... items) {
			this.allowedItems = Stream.of(items).map((itemc) -> itemc.asItem()).toArray((i) -> new Item[i]);
		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public IsItemConditionResolver(Object... args) {
			this(Arrays.copyOf(args, args.length, ItemConvertible[].class));
		}

		/**
		 * @return The {@link Item Items} for which the {@link IsItemConditionResolver
		 *         condition} will be met.
		 */
		public List<Item> getAllowedItems() {
			return Arrays.asList(allowedItems);
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return getAllowedItems().contains(item);
		}

	}

	/**
	 * See {@link BuiltInCondition#HAS_TAG}
	 * 
	 * @author B0IIZZ
	 */
	protected static class HasTagConditionResolver implements TooltipCondition {

		private NBTPath path;
		private int type;

		/**
		 * @param id : The path to the required {@link CompoundTag NBT-tag} from the
		 *           root tag.
		 */
		public HasTagConditionResolver(String id) {
			this(id, 99);
		}

		/**
		 * @param id   : The path to the required {@link CompoundTag NBT-tag} from the
		 *             root tag.
		 * @param type : The {@link net.minecraft.nbt.Tag type} of property.
		 */
		public HasTagConditionResolver(String id, int type) {
			this.path = new NBTPath(id);
			this.type = type;
		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public HasTagConditionResolver(Object... args) {
			this((String) args[0], args.length > 1 ? (int) args[1] : 99);
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return path.getOptional(tag).filter((t) -> this.type == 99 ? true : t.getType() == this.type).isPresent();
		}

	}

	/**
	 * See {@link BuiltInCondition#TAG_MATCHES}
	 * 
	 * @author B0IIZZ
	 */
	protected static class TagMatchesConditionResolver implements TooltipCondition {

		private NBTPath path;
		private String value;

		/**
		 * @param id    : The path to the required {@link CompoundTag NBT-tag} from the
		 *              root tag.
		 * @param value : The value of the given property
		 */
		public TagMatchesConditionResolver(String id, String value) {
			this.path = new NBTPath(id);
			this.value = value;
		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public TagMatchesConditionResolver(Object... args) {
			this((String) args[0], (String) args[1]);
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return path.getOptional(tag).filter((t) -> NBTUtil.isEqualTo(t, value)).isPresent();
		}

	}

	/**
	 * See {@link BuiltInCondition#NOT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class NotConditionResolver implements TooltipCondition {

		private TooltipCondition condition;

		/**
		 * @param condition : The {@link TooltipCondition child condition}.
		 */
		public NotConditionResolver(TooltipCondition condition) {
			this.condition = condition;
		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public NotConditionResolver(Object... args) {
			this((TooltipCondition) args[0]);
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return !condition.isConditionMet(item, tag, context);
		}
	}

	/**
	 * See {@link BuiltInCondition#OR}
	 * 
	 * @author B0IIZZ
	 */
	protected static class OrConditionResolver implements TooltipCondition {

		private TooltipCondition[] conditions;

		/**
		 * @param conditions : The Collection of combined {@link TooltipCondition
		 *                   conditions}.
		 */
		public OrConditionResolver(TooltipCondition... conditions) {
			this.conditions = conditions;
		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public OrConditionResolver(Object... args) {
			this(Arrays.copyOf(args, args.length, TooltipCondition[].class));
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			for (TooltipCondition condition : conditions) {
				if (condition.isConditionMet(item, tag, context))
					return true;
			}
			return false;
		}
	}

	/**
	 * See {@link BuiltInCondition#IS_ADVANCED_CONTEXT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class AdvancedContextConditionResolver implements TooltipCondition {

		/**
		 * 
		 */
		public AdvancedContextConditionResolver() {

		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public AdvancedContextConditionResolver(Object... args) {
			this();
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return context.isAdvanced();
		}

	}

	/**
	 * See {@link BuiltInCondition#IS_HUD_CONTEXT}
	 * 
	 * @author B0IIZZ
	 */
	protected static class HudContextConditionResolver implements TooltipCondition {

		/**
		 * 
		 */
		public HudContextConditionResolver() {

		}

		/**
		 * @param args The arguments required for the {@link TooltipCondition}
		 */
		public HudContextConditionResolver(Object... args) {
			this();
		}

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return context instanceof HudTooltipContext;
		}

	}

}
