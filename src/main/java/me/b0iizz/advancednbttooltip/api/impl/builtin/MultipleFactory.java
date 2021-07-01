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

import java.util.Arrays;
import java.util.List;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipIdentifier;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

/**
 * Combines multiple {@link TooltipFactory TooltipFactories} under each other
 * together.
 * 
 * @author B0IIZZ
 */
@TooltipIdentifier("multiple")
public class MultipleFactory implements TooltipFactory {
	
	/**
	 * An array of {@link TooltipFactory TooltipFactories} which
	 * will be appended under one another.
	 */
	@Required
	public TooltipFactory[] texts;
	
	/** Default constructor*/
	public MultipleFactory() {}
	
	/**
	 * @param texts The factories to be appended.
	 */
	public MultipleFactory(TooltipFactory[] texts) {
		this.texts = texts;
	}
	
	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return Arrays.stream(texts).flatMap(text -> text.getTooltipText(item, tag, context).stream()).toList();
	}

}
