package me.b0iizz.advancednbttooltip.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.tooltip.hud.HudTooltipRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("javadoc")
@Mixin(InGameHud.class)
public class InGameHudMixin {

	private HudTooltipRenderer tooltipHudRenderer;

	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/client/MinecraftClient;)V")
	private void onInit(MinecraftClient client, CallbackInfo ci) {
		tooltipHudRenderer = new HudTooltipRenderer(client);
	}

	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V")
	private void onDraw(MatrixStack matrices, float delta, CallbackInfo ci) {
		if (ConfigManager.isHudRenderingEnabled())
			tooltipHudRenderer.draw(matrices, delta);
	}

}
