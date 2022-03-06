package me.b0iizz.advancednbttooltip.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	
	@Shadow
	protected MinecraftClient client;

	@Overwrite
	public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		TooltipContext ctx = this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED
				: TooltipContext.Default.NORMAL;
		List<TooltipComponent> tooltip = stack.getTooltip(this.client.player, ctx).stream().map(Text::asOrderedText)
				.map(TooltipComponent::of).collect(Collectors.toCollection(ArrayList::new));
		stack.getTooltipData().map(TooltipComponent::of).ifPresent(tooltip::add);
		
		AdvancedNBTTooltips.getTooltip(stack, ctx, tooltip);	
		this.renderTooltipFromComponents(matrices, tooltip, x, y);
	}

	@Shadow
	public abstract void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x,
			int y);

}
