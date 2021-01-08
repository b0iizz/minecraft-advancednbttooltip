/*	MIT License
	
	Copyright (c) 2020 b0iizz
	
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
package me.b0iizz.advancednbttooltip.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.config.ModConfig.TooltipPosition;
import me.b0iizz.advancednbttooltip.tooltip.CustomTooltipManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@SuppressWarnings("javadoc")
@Mixin(ItemStack.class)
public class ItemStackMixin {

	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "net.minecraft.item.ItemStack.getHideFlags()I"), method = "getTooltip")
	public int rewriteHideFlags(int i) {
		if (ConfigManager.isUsingItemStackInjection()) {
			return i & ConfigManager.getItemStackInjectorBitmask();
		}
		return i;
	}

	@Shadow
	public ItemStack copy() {
		return ItemStack.EMPTY;
	}

	@Inject(at = @At("RETURN"), method = "getTooltip", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	public void appendTooltips(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci,
			List<Text> list) {
		ArrayList<Text> text = new ArrayList<>();
		CustomTooltipManager.appendCustomTooltip(copy(), player == null ? null : player.world, text, context, ci);

		if (!list.isEmpty() && !text.isEmpty())
			text.add(0, new LiteralText(""));
		
		if(ConfigManager.getTooltipPosition() == TooltipPosition.TOP && !text.isEmpty() && list.size() > 1)
			text.add(new LiteralText(" "));
		
		list.addAll(ConfigManager.getTooltipPosition().position(list), text);

		ci.setReturnValue(list);
	}
}
