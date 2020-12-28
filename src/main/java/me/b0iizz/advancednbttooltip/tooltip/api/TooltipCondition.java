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
package me.b0iizz.advancednbttooltip.tooltip.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import me.b0iizz.advancednbttooltip.tooltip.loader.Loader;
import me.b0iizz.advancednbttooltip.tooltip.loader.TooltipConditionLoader;
import me.b0iizz.advancednbttooltip.tooltip.util.NBTPath;
import me.b0iizz.advancednbttooltip.tooltip.util.NBTUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;

/**
 * An interface used to restrict the visibility of a tooltip.
 * 
 * @author B0IIZZ
 */
@FunctionalInterface
public interface TooltipCondition {

	/**
	 * The {@link Loader} of this interface
	 */
	public static final Loader<TooltipCondition> LOADER = TooltipConditionLoader.INSTANCE;

	/**
	 * Condition which is always false
	 */
	public static final TooltipCondition TRUE = (i, t, c) -> true;

	/**
	 * Condition which is always false
	 */
	public static final TooltipCondition FALSE = (i, t, c) -> false;

	/**
	 * Used for built-in {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @param name : One of {@link TooltipCondition.HasTagConditionResolver
	 *             "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 *             "TAG_MATCHES"}, {@link TooltipCondition.IsItemConditionResolver
	 *             "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 *             {@link TooltipCondition.OrConditionResolver
	 *             "OR"},{@link TooltipCondition.NotConditionResolver "NOT"}
	 * @param args : The appropriate arguments for the specific condition.
	 * @return The built-in {@link TooltipCondition} with the given name
	 */
	public static TooltipCondition builtIn(String name, Object... args) {
		switch (name) {
		case "HAS_TAG":
			if (args.length > 1) {
				return new HasTagConditionResolver((String) args[0], (int) args[1]);
			} else {
				return new HasTagConditionResolver((String) args[0]);
			}
		case "TAG_MATCHES":
			return new TagMatchesConditionResolver((String) args[0], (String) args[1]);
		case "IS_ITEM":
			return new IsItemConditionResolver(Arrays.copyOf(args, args.length, ItemConvertible[].class));
		case "AND":
			return new AndConditionResolver(Arrays.copyOf(args, args.length, TooltipCondition[].class));
		case "OR":
			return new OrConditionResolver(Arrays.copyOf(args, args.length, TooltipCondition[].class));
		case "NOT":
			return new NotConditionResolver((TooltipCondition) args[0]);
		case "TRUE":
			return TRUE;
		case "FALSE":
		default:
			return FALSE;
		}
	}

	/**
	 * Decides whether the tooltip should be applied or not.
	 * 
	 * @param item    The {@link Item} the tooltip will be added to.
	 * @param tag     The Item's {@link CompoundTag NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return Whether the tooltip should be displayed.
	 */
	public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context);

	/**
	 * {@link TooltipCondition ConditionResolver} which activates when all
	 * {@link TooltipCondition child conditions} are met. <br>
	 * <br>
	 * <b>Arguments:</b> <br>
	 * {@link TooltipCondition TooltipCondition[]} <b>conditions</b> : The List of
	 * required {@link TooltipCondition conditions}. <br>
	 * <br>
	 * One of {@link TooltipCondition.HasTagConditionResolver
	 * "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 * "TAG_MATCHES"}, {@link TooltipCondition.IsItemConditionResolver
	 * "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver
	 * "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in
	 * {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @author B0IIZZ
	 */
	public static class AndConditionResolver implements TooltipCondition {

		/**
		 * The array of required {@link TooltipCondition conditions}.
		 */
		private TooltipCondition[] conditions;

		/**
		 * @param conditions : The Collection of required {@link TooltipCondition
		 *                   conditions}.
		 */
		public AndConditionResolver(TooltipCondition... conditions) {
			this.conditions = conditions;
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
	 * {@link TooltipCondition ConditionResolver} which activates when a specific
	 * {@link Item} is detected. <br>
	 * <br>
	 * <b>Arguments:</b> <br>
	 * {@link Item Item[]} <b>items</b> : The {@link Item Items} for which the
	 * condition will be met. <br>
	 * <br>
	 * One of {@link TooltipCondition.HasTagConditionResolver
	 * "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 * "TAG_MATCHES"}, {@link TooltipCondition.IsItemConditionResolver
	 * "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver
	 * "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in
	 * {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @author B0IIZZ
	 */
	public static class IsItemConditionResolver implements TooltipCondition {

		/**
		 * The {@link Item Items} for which the condition will be met.
		 */
		private Item[] allowedItems;

		/**
		 * @param items : The {@link ItemConvertible Items} for which the condition will
		 *              be met.
		 */
		public IsItemConditionResolver(ItemConvertible... items) {
			this.allowedItems = Stream.of(items).map((itemc) -> itemc.asItem()).toArray((i) -> new Item[i]);
		}

		/**
		 * @return The {@link Item Items} for which the
		 *         {@link TooltipCondition.IsItemConditionResolver condition} will be
		 *         met.
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
	 * {@link TooltipCondition ConditionResolver} which activates when the
	 * {@link Item} has a specific {@link CompoundTag NBT-tag} property. <br>
	 * <br>
	 * <b>Arguments:</b> <br>
	 * {@link String} <b>id</b> : The path to the required {@link CompoundTag
	 * NBT-tag} from the root tag. <br>
	 * {@link Integer int} <b>type</b> (optional) : The {@link net.minecraft.nbt.Tag
	 * type} of property. <br>
	 * <br>
	 * One of {@link TooltipCondition.HasTagConditionResolver
	 * "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 * "TAG_MATCHES"}, {@link TooltipCondition.IsItemConditionResolver
	 * "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver
	 * "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in
	 * {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @author B0IIZZ
	 */
	public static class HasTagConditionResolver implements TooltipCondition {

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

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return path.getOptional(tag).filter((t) -> this.type == 99 ? true : t.getType() == this.type).isPresent();
		}
		
	}

	/**
	 * {@link TooltipCondition ConditionResolver} which activates when the
	 * {@link Item} has a specific {@link CompoundTag NBT-tag} property. <br>
	 * <br>
	 * <b>Arguments:</b> <br>
	 * {@link String} <b>id</b> : The path to the required {@link CompoundTag
	 * NBT-tag} from the root tag. <br>
	 * {@link Integer int} <b>type</b> (optional) : The {@link net.minecraft.nbt.Tag
	 * type} of property. <br>
	 * <br>
	 * One of {@link TooltipCondition.HasTagConditionResolver
	 * "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 * "TAG_MATCHES"}, {@link TooltipCondition.TagMatchesConditionResolver
	 * "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver
	 * "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in
	 * {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @author B0IIZZ
	 */
	public static class TagMatchesConditionResolver implements TooltipCondition {

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

		@Override
		public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context) {
			return path.getOptional(tag).filter((t) -> NBTUtil.isEqualTo(t, value)).isPresent();
		}

	}

	/**
	 * {@link TooltipCondition ConditionResolver} which activates when the
	 * {@link TooltipCondition child condition} is not met. <br>
	 * <br>
	 * <b>Arguments:</b> <br>
	 * {@link TooltipCondition} <b>condition</b> : The {@link TooltipCondition child
	 * condition}. <br>
	 * <br>
	 * One of {@link TooltipCondition.HasTagConditionResolver
	 * "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 * "TAG_MATCHES"}, {@link TooltipCondition.IsItemConditionResolver
	 * "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver
	 * "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in
	 * {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @author B0IIZZ
	 */
	public static class NotConditionResolver implements TooltipCondition {

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

	/**
	 * {@link TooltipCondition ConditionResolver} which activates when one
	 * {@link TooltipCondition child condition} is met. <br>
	 * <br>
	 * <b>Arguments:</b> <br>
	 * {@link TooltipCondition TooltipCondition[]} <b>conditions</b> : The List of
	 * combined {@link TooltipCondition conditions}. <br>
	 * <br>
	 * One of {@link TooltipCondition.HasTagConditionResolver
	 * "HAS_TAG"},{@link TooltipCondition.TagMatchesConditionResolver
	 * "TAG_MATCHES"}, {@link TooltipCondition.IsItemConditionResolver
	 * "IS_ITEM"},{@link TooltipCondition.AndConditionResolver "AND"},
	 * {@link TooltipCondition.OrConditionResolver
	 * "OR"},{@link TooltipCondition.NotConditionResolver "NOT"} built-in
	 * {@link TooltipCondition ConditionResolvers}.
	 * 
	 * @author B0IIZZ
	 */
	public static class OrConditionResolver implements TooltipCondition {

		/**
		 * The array of combined {@link TooltipCondition conditions}.
		 */
		private TooltipCondition[] conditions;

		/**
		 * @param conditions : The Collection of combined {@link TooltipCondition
		 *                   conditions}.
		 */
		public OrConditionResolver(TooltipCondition... conditions) {
			this.conditions = conditions;
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

}