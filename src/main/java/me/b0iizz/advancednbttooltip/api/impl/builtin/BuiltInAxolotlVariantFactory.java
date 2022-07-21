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

import me.b0iizz.advancednbttooltip.api.JsonTooltips;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.List;

/**
 * @author B0IIZZ
 */
@JsonTooltips.TooltipCode("builtin_axolotl_variant")
public class BuiltInAxolotlVariantFactory implements TooltipFactory {

	private static final int[] AXOLOTL_COLORS = new int[]{0xFFC0CB, 0x835C3B, 0xFFFF00, 0xCCFFFF, 0x728FCE};

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		if (!tag.contains(AxolotlEntity.VARIANT_KEY, NbtType.INT)) return Collections.emptyList();

		int id = tag.getInt(AxolotlEntity.VARIANT_KEY);

		AxolotlEntity.Variant variant = AxolotlEntity.Variant.VARIANTS[id];
		MutableText text = Text.translatable("text.advancednbttooltip.tooltip.axolotl").formatted(Formatting.GRAY);
		// Make sure we don't go out of bounds if mods add more axolotl types.
		if (id < AXOLOTL_COLORS.length) {
			text.append(Text.literal(variant.getName()).setStyle(Style.EMPTY.withColor(AXOLOTL_COLORS[id])));
		}
		return List.of(text);
	}
}
