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
package me.b0iizz.advancednbttooltip.mixin;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.b0iizz.advancednbttooltip.config.ConfigManager;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Formatting;
import net.minecraft.tag.ItemTags;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.ComposterBlock;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	private static final int TAG_INT = 3;
	private static Map<Item, Integer> FuelTimeMap = null;
	private static final int[] AXOLOTL_COLORS = new int[] { 0xFFC0CB, 0x835C3B, 0xFFFF00, 0xCCFFFF, 0x728FCE };

	@Shadow
	public abstract Item getItem();

	@Shadow
	@Nullable
	public abstract NbtCompound getNbt();

	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "net.minecraft.item.ItemStack.getHideFlags()I"), method = "getTooltip")
	private int advancednbttooltip$rewriteHideFlags(int i) {
		if (ConfigManager.overrideHideFlags()) {
			return i & ConfigManager.getHideflagOverrideBitmask();
		}
		return i;
	}

	// TODO: If a reasonable JSON-based solution is implemented, this stuff (and relevant declarations and imports) should be removed.
	@Inject(method = "getTooltip", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onGetTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci, List<Text> list) {

		if (!ConfigManager.getTooltipToggle()) return;

		Item item = this.getItem();
		int line = Math.min(1, list.size());
		int size = list.size();

		if (ConfigManager.isShowMiningSpeed() && item instanceof ToolItem) {

			int level = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, (ItemStack)(Object)this);
			float multiplier = ((ToolItem)item).getMaterial().getMiningSpeedMultiplier();
			int offset = context.isAdvanced() ? 1 + (((ItemStack)(Object)this).isDamaged() ? 1 : 0) + (((ItemStack)(Object)this).hasNbt() ? 1 : 0) : 0;

			TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.miningspeed");
			LiteralText value = new LiteralText(" " + String.valueOf(Math.pow(level, 2) + multiplier));

			// TODO: Adding more stuff to this tooltip will result in the mining speed being displayed in the wrong place.
			list.add(Math.max(0, list.size() - offset), value.append(label).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.DARK_GREEN))));
			size++;
		}

		if (ConfigManager.isShowEnchantability() && ((ItemStack)(Object)this).isEnchantable()) {

			TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.enchantability");

			list.add(line, label.append(String.valueOf(item.getEnchantability())).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));
		}

		if (ConfigManager.isShowAxolotlVariant() && item == Items.AXOLOTL_BUCKET) {

			NbtCompound tag = this.getNbt();

			if (tag != null && tag.contains(AxolotlEntity.VARIANT_KEY, TAG_INT)) {

				int id = tag.getInt(AxolotlEntity.VARIANT_KEY);

				AxolotlEntity.Variant variant = AxolotlEntity.Variant.VARIANTS[id];
				TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.axolotl");
				LiteralText value = new LiteralText(variant.getName());

				label.setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)));
				
				// Make sure we don't go out of bounds if mods add more axolotl types.
				if (id < AXOLOTL_COLORS.length) {
					value.setStyle(Style.EMPTY.withColor(AXOLOTL_COLORS[id]));
				}

				list.add(line, label.append(value));
			}
		}

		if (ConfigManager.isShowBlastResistance() && item instanceof BlockItem) {

			float blastResistance = ((BlockItem)item).getBlock().getBlastResistance();
			TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.blastresistance");

			list.add(line, label.append(String.valueOf(blastResistance)).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));			
		}

		if (ConfigManager.isShowBlockHardness() && item instanceof BlockItem) {

			float hardness = ((BlockItem)item).getBlock().getHardness();
			TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.hardness");

			list.add(line, label.append(String.valueOf(hardness)).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));			
		}

		if (ConfigManager.isShowCompostingChance()) {

			float chance = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item);

			if (chance > 0) {

				TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.compostingchance");

				list.add(line, label.append(String.valueOf(chance * 100) + "%").setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));
			}
		}

		if (ConfigManager.isShowFuelTime()) {

			if (FuelTimeMap == null)
				FuelTimeMap = AbstractFurnaceBlockEntity.createFuelTimeMap();

			int time = FuelTimeMap.getOrDefault(item, 0);

			if (time > 0) {

				TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.fueltime");

				list.add(line, label.append(String.valueOf(time)).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));
			}
		}

		if (ConfigManager.isShowFoodStats() && ((ItemStack)(Object)this).isFood()) {

			FoodComponent component = item.getFoodComponent();
			int hunger = component.getHunger();
			float saturation = 2 * hunger * component.getSaturationModifier();
			TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.foodstats");
			TranslatableText labelHunger = new TranslatableText("text.advancednbttooltip.tooltip.foodstats.hunger");
			TranslatableText labelSaturation = new TranslatableText("text.advancednbttooltip.tooltip.foodstats.saturation");
			LiteralText valueHunger = new LiteralText(" " + String.valueOf(hunger));
			LiteralText valueSaturation = new LiteralText(" " + String.valueOf(saturation));
			int offset = context.isAdvanced() ? 1 + (((ItemStack)(Object)this).isDamaged() ? 1 : 0) + (((ItemStack)(Object)this).hasNbt() ? 1 : 0) : 0;

			line = Math.max(0, list.size() - offset);
			list.add(line, label.setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));
			list.add(line + 1, valueHunger.append(labelHunger).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.DARK_GREEN))));
			list.add(line + 2, valueSaturation.append(labelSaturation).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.DARK_GREEN))));
		}

		if (ConfigManager.isShowLightLevel()) {

			Identifier id = Registry.ITEM.getKey(item).get().getValue();
			int luminance = Registry.BLOCK.get(id).getDefaultState().getLuminance();

			if (luminance > 0) {

				TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.lightlevel");

				list.add(line, label.append(String.valueOf(luminance)).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));
			}
		}

		if (ConfigManager.isShowMusicDisc() && item instanceof MusicDiscItem) {

			TranslatableText label = new TranslatableText("text.advancednbttooltip.tooltip.disc");

			list.add(++line, label.append(String.valueOf(((MusicDiscItem)item).getComparatorOutput())).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))));
		}
		
		if (size != list.size())
			list.add(line, new LiteralText(""));
	}
}
