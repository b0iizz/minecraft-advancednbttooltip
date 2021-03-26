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
package me.b0iizz.advancednbttooltip.tooltip.api;

import java.util.List;
import java.util.function.BooleanSupplier;

import me.b0iizz.advancednbttooltip.tooltip.CustomTooltip;
import me.b0iizz.advancednbttooltip.tooltip.builtin.BuiltInCondition;
import me.b0iizz.advancednbttooltip.tooltip.loader.Loader;
import me.b0iizz.advancednbttooltip.tooltip.loader.TooltipLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

/**
 * An interface describing the structure of a {@link CustomTooltip}
 * 
 * @author B0IIZZ
 */
public interface AbstractCustomTooltip {

	/**
	 * The {@link Loader} of this class
	 */
	public static final Loader<AbstractCustomTooltip> LOADER = TooltipLoader.INSTANCE;
	
	/**
	 * Adds a pre-defined {@link TooltipCondition Condition} which has to be met to
	 * show the tooltip.
	 * 
	 * @param condition : See {@link BuiltInCondition}
	 * @param args      : The appropriate arguments for the specific condition.
	 * @return The original {@link AbstractCustomTooltip} object. Generally used for
	 *         chaining.
	 */
	public AbstractCustomTooltip addCondition(BuiltInCondition condition, Object... args);

	/**
	 * Adds a {@link TooltipCondition Condition} which has to be met to show the
	 * tooltip.
	 * 
	 * @param condition The condition to be added.
	 * @return The original {@link AbstractCustomTooltip} object. Generally used for
	 *         chaining.
	 */
	public AbstractCustomTooltip addCondition(TooltipCondition condition);

	/**
	 * Adds a {@link BooleanSupplier boolean condition} which has to be met to show
	 * the tooltip.
	 * 
	 * @param condition The condition to be added.
	 * @return The original {@link AbstractCustomTooltip} object. Generally used for
	 *         chaining.
	 */
	public default AbstractCustomTooltip addCondition(BooleanSupplier condition) {
		return this.addCondition((i, t, c) -> condition.getAsBoolean());
	}

	/**
	 * Decides whether the tooltip should be applied or not.
	 * 
	 * @param item    The {@link Item} the tooltip will be added to.
	 * @param tag     The Item's {@link CompoundTag NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return <b>true</b>, when the custom tooltip should be appended to the
	 *         pre-existing tooltip. <b>false</b>, when not.
	 */
	public boolean isTooltipVisible(Item item, CompoundTag tag, TooltipContext context);

	/**
	 * Creates the tooltip text for the Item.
	 * 
	 * @param item    The {@link Item} the tooltip will be added to.
	 * @param tag     The Item's {@link CompoundTag NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return A {@link List} of {@link Text} to be applied to the Item's tooltip.
	 */
	List<Text> makeTooltip(Item item, CompoundTag tag, TooltipContext context);

}