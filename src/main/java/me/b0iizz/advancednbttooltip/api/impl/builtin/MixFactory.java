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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Suggested;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Combines multiple {@link TooltipFactory TooltipFactories} next to each other
 * together.
 * 
 * @author B0IIZZ
 */
@TooltipCode("mix")
public class MixFactory implements TooltipFactory {

	/**
	 * An array of {@link TooltipFactory TooltipFactories} which will be appended
	 * under one another.
	 */
	@Required
	public TooltipFactory[] texts;

	/**
	 * Whether each line of the texts should be appended separately or all appended
	 * in one big line.
	 */
	@Suggested
	public boolean separate_lines = true;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		List<List<Text>> tooltips = new ArrayList<>();

		for (TooltipFactory factory : texts) {
			tooltips.add(factory.getTooltipText(item, tag, context));
		}

		if (separate_lines) {
			int maxLines = tooltips.stream().mapToInt(List::size).reduce(Integer::max).orElse(0);

			Text[] res = new Text[maxLines];

			for (int line = 0; line < maxLines; line++) {
				final int idx = line;
				res[line] = tooltips.stream().flatMap(list -> {
					if (idx < list.size())
						return Stream.of(list.get(idx));
					if (!list.isEmpty())
						return Stream.of(list.get(list.size() - 1));
					return Stream.empty();
				}).map(Text::shallowCopy).reduce(MutableText::append).orElse(new LiteralText(""));
			}

			return Arrays.asList(res);
		} else {
			return tooltips.stream().flatMap(List::stream).map(Text::shallowCopy).reduce(MutableText::append)
					.map(m -> (Text) m).map(Collections::singletonList).orElseGet(Collections::emptyList);
		}
	}

}
