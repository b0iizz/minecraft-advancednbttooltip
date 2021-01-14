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

import me.b0iizz.advancednbttooltip.tooltip.loader.Loader;
import me.b0iizz.advancednbttooltip.tooltip.loader.TooltipConditionLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
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
	 * Decides whether the tooltip should be applied or not.
	 * 
	 * @param item    The {@link Item} the tooltip will be added to.
	 * @param tag     The Item's {@link CompoundTag NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return Whether the tooltip should be displayed.
	 */
	public boolean isConditionMet(Item item, CompoundTag tag, TooltipContext context);

	

}