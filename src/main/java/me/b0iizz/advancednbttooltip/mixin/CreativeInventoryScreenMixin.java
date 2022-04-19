package me.b0iizz.advancednbttooltip.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.b0iizz.advancednbttooltip.gui.CustomTooltipRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {

	@Inject(
			method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/gui/screen/ingame/CreativeInventoryScreen.renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void redirectRenderToCustomTooltip(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci,
			List<Text> list, List<Text> list2) {
		((CustomTooltipRenderer) this).renderTooltip(matrices, stack, x, y,
				MinecraftClient.getInstance().options.advancedItemTooltips, list2,
				MinecraftClient.getInstance().player);
		ci.cancel();
	}

}
