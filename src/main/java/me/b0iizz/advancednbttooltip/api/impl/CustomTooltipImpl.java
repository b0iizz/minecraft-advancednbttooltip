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

import me.b0iizz.advancednbttooltip.api.CustomTooltip;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of {@link CustomTooltip} used in the .json tooltips.
 *
 * @author B0IIZZ
 */
final class CustomTooltipImpl implements CustomTooltip {

	private final List<TooltipFactory> factories = new ArrayList<>();
	private final List<TooltipCondition> conditions = new ArrayList<>();

	@Override
	public boolean isEnabled(Item item, NbtCompound tag, TooltipContext context) {
		for (TooltipCondition condition : conditions)
			if (condition != null && !condition.isEnabled(item, tag, context))
				return false;
		return true;
	}

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		if (!isEnabled(item, tag, context))
			return Collections.emptyList();
		return factories.stream().sequential().flatMap(factory -> factory.getTooltipText(item, tag, context).stream())
				.toList();
	}

	@Override
	public List<TooltipComponent> getTooltip(Item item, NbtCompound tag, TooltipContext context) {
		if (!isEnabled(item, tag, context))
			return Collections.emptyList();
		return factories.stream().sequential().flatMap(factory -> factory.getTooltip(item, tag, context).stream())
				.toList();
	}

	@Override
	public CustomTooltip addText(TooltipFactory text) {
		if (text != null)
			factories.add(text);
		return this;
	}

	@Override
	public CustomTooltip addCondition(TooltipCondition condition) {
		if (condition != null)
			conditions.add(condition);
		return this;
	}

}
