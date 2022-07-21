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

import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.List;

/**
 * @author B0IIZZ
 */
@TooltipCode("builtin_food_stats")
public class BuiltInFoodStatsFactory implements TooltipFactory {

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		if (!item.isFood()) return Collections.emptyList();

		FoodComponent component = item.getFoodComponent();
		int hunger = component.getHunger();
		float saturation = 2 * hunger * component.getSaturationModifier();
		var label = Text.translatable("text.advancednbttooltip.tooltip.foodstats");
		var labelHunger = Text.translatable("text.advancednbttooltip.tooltip.foodstats.hunger", "%d".formatted(hunger));
		var labelSaturation = Text.translatable("text.advancednbttooltip.tooltip.foodstats.saturation", "%.1f".formatted(saturation));

		return List.of(label.formatted(Formatting.GRAY), labelHunger.formatted(Formatting.DARK_GREEN), labelSaturation.formatted(Formatting.DARK_GREEN));
	}

}
