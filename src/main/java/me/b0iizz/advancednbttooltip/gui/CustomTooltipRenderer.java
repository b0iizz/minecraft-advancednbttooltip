package me.b0iizz.advancednbttooltip.gui;

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
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

	default void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, boolean advanced,
							   @Nullable List<Text> vanilla_lines, @Nullable PlayerEntity player) {
		TooltipContext ctx = advanced ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL;
		this.renderTooltip(matrices, stack, x, y, ctx, vanilla_lines, player);
	}

	default void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, TooltipContext ctx,
							   @Nullable List<Text> vanilla_lines, @Nullable PlayerEntity player) {
		List<TooltipComponent> tooltip = Optional.ofNullable(vanilla_lines).orElse(stack.getTooltip(player, ctx))
				.stream()
				.sequential().map(Text::asOrderedText).map(TooltipComponent::of)
				.collect(Collectors.toCollection(ArrayList::new));

		stack.getTooltipData().map(TooltipComponent::of).ifPresent(tooltip::add);

		AdvancedNBTTooltips.getTooltip(stack, ctx, tooltip);
		this.renderComponents(matrices, stack, tooltip, x, y);
	}

}
