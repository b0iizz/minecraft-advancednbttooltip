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
import java.util.List;

import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * @author B0IIZZ
 */
@TooltipIdentifier("builtin_signs")
public class BuiltInSignsFactory implements TooltipFactory {

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		final String startText = "----------------";
		final String endText = "----------------";

		List<Text> result = new ArrayList<>();

		NbtCompound blockEntityTag = tag.getCompound("BlockEntityTag");

		int preferredWidth = MinecraftClient.getInstance().textRenderer.getWidth(endText);

		result.add(new LiteralText(startText).formatted(Formatting.GRAY));

		for (int i = 1; i < 5; i++) {
			boolean hasText = blockEntityTag.contains("Text" + i);
			String text = hasText ? Text.Serializer.fromJson(blockEntityTag.getString("Text" + i)).asString() : "";
			while (preferredWidth >= MinecraftClient.getInstance().textRenderer.getWidth(' ' + text + ' '))
				text = ' ' + text + ' ';
			result.add(new LiteralText(text).formatted(Formatting.GRAY));
		}

		result.add(new LiteralText(endText).formatted(Formatting.GRAY));

		return result;
	}

}
