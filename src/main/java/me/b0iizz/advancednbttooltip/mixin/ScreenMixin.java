package me.b0iizz.advancednbttooltip.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.b0iizz.advancednbttooltip.gui.CustomTooltipRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin(Screen.class)
public abstract class ScreenMixin implements CustomTooltipRenderer {
	
	@Shadow
	protected MinecraftClient client;

	@Overwrite
	public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		this.renderTooltip(matrices, stack, x, y, this.client.options.advancedItemTooltips, null, this.client.player);
	}
	
	@Override
	public void renderComponents(MatrixStack matrices, ItemStack stack, List<TooltipComponent> components, int x, int y) {
		this.renderTooltipFromComponents(matrices, components, x, y);
	}
	
	@Shadow
	public abstract void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x,
			int y);

}
