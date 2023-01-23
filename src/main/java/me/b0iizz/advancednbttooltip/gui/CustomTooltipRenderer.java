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
package me.b0iizz.advancednbttooltip.gui;

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CustomTooltipRenderer {

	void renderComponents(MatrixStack matrices, @Nullable ItemStack stack, List<TooltipComponent> components, int x, int y);

	default void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, boolean advanced, @Nullable List<Text> vanilla_lines, @Nullable PlayerEntity player) {
		TooltipContext ctx = advanced ? TooltipContext.ADVANCED : TooltipContext.BASIC;
		this.renderTooltip(matrices, stack, x, y, ctx, vanilla_lines, player);
	}

	default void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, TooltipContext ctx, @Nullable List<Text> vanilla_lines, @Nullable PlayerEntity player) {
		List<TooltipComponent> tooltip = Optional.ofNullable(vanilla_lines).orElse(stack.getTooltip(player, ctx))
				.stream().sequential().map(Text::asOrderedText).map(TooltipComponent::of)
				.collect(Collectors.toCollection(ArrayList::new));

		stack.getTooltipData().map(CustomTooltipRenderer::fromTooltipData).ifPresent(tooltip::add);

		AdvancedNBTTooltips.getTooltip(stack, ctx, tooltip);
		this.renderComponents(matrices, stack, tooltip, x, y);
	}

	private static TooltipComponent fromTooltipData(TooltipData data) {
		TooltipComponent result = TooltipComponentCallback.EVENT.invoker().getComponent(data);
		return result != null ? result : TooltipComponent.of(data);
	}

}
