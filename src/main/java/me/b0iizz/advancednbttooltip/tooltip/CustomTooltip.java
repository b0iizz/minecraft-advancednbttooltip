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
import java.util.List;

import me.b0iizz.advancednbttooltip.tooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.tooltip.api.AbstractCustomTooltip;
import me.b0iizz.advancednbttooltip.tooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.tooltip.builtin.BuiltInCondition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

/**
 * The class representation of a custom tooltip. It is used to determine whether
 * this specific tooltip should be applied or not and what it actually appends
 * to the items tooltip.
 * 
 * @author B0IIZZ
 */
public final class CustomTooltip implements AbstractCustomTooltip {

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
	 * Constructs a new CustomTooltip with the given name and TooltipFactory
	 * 
	 * @param name            a name used for registration
	 * @param tooltipProvider a provider for {@link Text} for the tooltip
	 */
	public CustomTooltip(String name, TooltipFactory tooltipProvider) {
		this.name = name;
		this.tooltipProvider = tooltipProvider;
		conditions = new ArrayList<>();
	}

	@Override
	public CustomTooltip addCondition(String conditionName, Object... args) {
		addCondition(BuiltInCondition.valueOf(conditionName).create(args));
		return this;
	}

	@Override
	public AbstractCustomTooltip addCondition(BuiltInCondition condition, Object... args) {
		addCondition(condition.create(args));
		return this;
	}
	
	@Override
	public CustomTooltip addCondition(TooltipCondition condition) {
		conditions.add(condition);
		return this;
	}

	@Override
	public boolean isTooltipVisible(Item item, CompoundTag tag, TooltipContext context) {
		for (TooltipCondition condition : conditions) {
			if (!condition.isConditionMet(item, tag, context))
				return false;
		}
		return true;
	}

	@Override
	public List<Text> makeTooltip(Item item, CompoundTag tag, TooltipContext context) {
		if (isTooltipVisible(item, tag, context)) {
			return tooltipProvider.createTooltip(item, tag, context);
		} else {
			return tooltipProvider.createTooltipWhenDisabled(item, tag, context);
		}
	}

	@Override
	public String getName() {
		return name;
	}


}
