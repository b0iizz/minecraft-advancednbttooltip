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

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author B0IIZZ
 */
@TooltipCode("builtin_hideflags")
public class BuiltInHideflagsFactory implements TooltipFactory {

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		List<Text> result = new ArrayList<>();

		int hideFlags = tag.getInt("HideFlags");

		result.add(new TranslatableText("text." + AdvancedNBTTooltips.modid + ".tooltip.hideflag")
				.formatted(Formatting.GRAY));

		for (int i = 0; i < ItemStack.TooltipSection.values().length; i++) {
			if (((1 << i) & hideFlags) > 0) {
				LiteralText line = new LiteralText(" -");
				line.append(new TranslatableText("text." + AdvancedNBTTooltips.modid + ".hideflag."
						+ ItemStack.TooltipSection.values()[i].name().toLowerCase()));
				result.add(line.formatted(Formatting.GRAY, Formatting.ITALIC));
			}
		}

		return result;
	}

}
