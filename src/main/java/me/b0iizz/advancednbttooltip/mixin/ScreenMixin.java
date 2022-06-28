package me.b0iizz.advancednbttooltip.mixin;

import me.b0iizz.advancednbttooltip.gui.CustomTooltipRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin implements CustomTooltipRenderer {

	@Shadow
	protected MinecraftClient client;

	/**
	 * @author b0iizz
	 */
	@Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", cancellable = true)
	public void renderTooltipHook(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
		this.renderTooltip(matrices, stack, x, y, this.client.options.advancedItemTooltips, null, this.client.player);
		ci.cancel();
	}

	@Override
	public void renderComponents(MatrixStack matrices, ItemStack stack, List<TooltipComponent> components, int x, int y) {
		this.renderTooltipFromComponents(matrices, components, x, y);
	}

	@Shadow
	protected abstract void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x,
													 int y);

}
