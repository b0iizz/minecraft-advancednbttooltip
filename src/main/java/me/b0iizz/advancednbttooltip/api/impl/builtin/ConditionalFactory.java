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
package me.b0iizz.advancednbttooltip.api.impl.builtin;

import java.util.List;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

/**
 * A factory which creates two different tooltips depending on a
 * {@link TooltipCondition condition}
 * 
 * @author B0IIZZ
 */
@TooltipCode("conditional")
public class ConditionalFactory implements TooltipFactory {

	/**
	 * The factory used when the condition is true
	 */
	@Required
	public TooltipFactory success;
	
	/**
	 * The factory used when the condition is false
	 */
	@Required
	public TooltipFactory fail;
	
	/**
	 * The condition
	 */
	@Required
	public TooltipCondition condition;
	
	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return (condition != null && condition.isEnabled(item, tag, context) ? success : fail).getTooltipText(item, tag, context);
	}
	
	@Override
	public List<TooltipComponent> getTooltip(Item item, NbtCompound tag, TooltipContext context) {
		return (condition != null && condition.isEnabled(item, tag, context) ? success : fail).getTooltip(item, tag, context);
	}
	
}
