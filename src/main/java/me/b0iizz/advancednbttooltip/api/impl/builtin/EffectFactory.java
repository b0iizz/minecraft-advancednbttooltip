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

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.Suggested;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Creates per line a minecraft effect formatting according to id, duration and
 * strength.
 *
 * @author B0IIZZ
 */
@TooltipCode("effect")
public class EffectFactory implements TooltipFactory {

	/**
	 * The raw id of the effect
	 */
	@Required
	public TooltipFactory rawId;

	/**
	 * The duration of the effect
	 */
	@Required
	public TooltipFactory duration;

	/**
	 * The strength of the effect
	 */
	@Suggested
	public TooltipFactory strength = null;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		List<Text> rawIds = rawId.getTooltipText(item, tag, context);
		List<Text> durations = duration.getTooltipText(item, tag, context);
		List<Text> strengths = strength != null ? strength.getTooltipText(item, tag, context)
				: Stream.generate(() -> Text.of("0")).limit(rawIds.size()).toList();

		int numEffects = Math.max(rawIds.size(),
				Math.max(durations.size(), strength == null ? 0 : strengths.size()));

		List<Text> result = new ArrayList<>();

		for (int i = 0; i < numEffects; i++) {
			byte rawId;
			int duration, strength;
			try {
				rawId = new BigDecimal(rawIds.get(i).getString().trim().replaceAll("[A-Za-z]$", "")).byteValue();
				duration = new BigDecimal(durations.get(i).getString().trim().replaceAll("[A-Za-z]$", ""))
						.intValue();
				strength = new BigDecimal(strengths.get(i).getString().trim().replaceAll("[A-Za-z]$", ""))
						.intValue();
			} catch (Throwable t) {
				t.printStackTrace();
				continue;
			}

			StatusEffect eff = StatusEffect.byRawId(rawId);
			StatusEffectInstance inst = new StatusEffectInstance(eff, duration, strength);

			MutableText line = Text.translatable(inst.getTranslationKey());

			if (inst.getAmplifier() > 0) {
				line = Text.translatable("potion.withAmplifier",
						line, Text.translatable("potion.potency." + inst.getAmplifier()));
			}
			if (inst.getDuration() > 20) {
				line = Text.translatable("potion.withDuration",
						line, StatusEffectUtil.durationToString(inst, 1));
			}

			//noinspection ConstantConditions
			result.add(line.formatted(eff.getCategory().getFormatting()));
		}
		return result;
	}

}
