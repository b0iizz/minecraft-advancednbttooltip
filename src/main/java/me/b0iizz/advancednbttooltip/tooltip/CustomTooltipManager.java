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
package me.b0iizz.advancednbttooltip.tooltip;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * A class handling registering and appending all the {@link CustomTooltip custom tooltips}'s tooltip.
 * 
 * @author B0IIZZ
 */
public final class CustomTooltipManager {

	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * A map containing all the registered {@link CustomTooltip CustomTooltips}.
	 */
	private static Map<String,CustomTooltip> registeredTooltips;
	
	/**
	 * Registers a new {@link CustomTooltip CustomTooltip}.
	 * @param tooltip : The {@link CustomTooltip CustomTooltip} to be registered.
	 */
	public static void registerTooltip(CustomTooltip tooltip) {
		if(registeredTooltips.containsKey(tooltip.name)) return;
		registeredTooltips.put(tooltip.name, tooltip);
	}
	
	/**
	 * Used by the classes of the me.b0iizz.advancednbttooltip.mixin package to interact with the tooltip pipeline.
	 */
	public static void appendCustomTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo info) {
		appendCustomTooltip(stack, world, tooltip, context);
	}
	
	/**
	 * Used by the classes of the me.b0iizz.advancednbttooltip.mixin package to interact with the tooltip pipeline.
	 */
	public static void appendCustomTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if(world == null) return;
		Item item = stack.getItem();
		CompoundTag tag = stack.getTag();
		for(CustomTooltip t : registeredTooltips.values()) {
			tooltip.addAll(t.makeTooltip(item, tag, context));
		}
	}
	
	static {
		registeredTooltips = new HashMap<>();
	}

	private static void registerBuiltInTooltips() {
		//TODO: SUSPICIOUS STEW
				registerTooltip(new CustomTooltip("SUSPICIOUS_STEW", (item,tag,context) ->  {
					List<Text> result = new ArrayList<>();
					ListTag effects = tag.getList("Effects", 10);
					for(int i = 0; i < effects.size();i++) {
						CompoundTag effect = effects.getCompound(i);
						byte effectId = effect.getByte("EffectId");
						int effectDuration = effect.contains("EffectDuration") ? effect.getInt("EffectDuration") : 160;
						
						StatusEffect eff = StatusEffect.byRawId(effectId);
						StatusEffectInstance inst = new StatusEffectInstance(eff, effectDuration);
						
						MutableText line = new TranslatableText(inst.getTranslationKey());
						
						String duration = StatusEffectUtil.durationToString(inst, 1);
						
						line.append(" (" + duration + ")");
						
						result.add(line.formatted(eff.getType().getFormatting()));
					}
					return result;
				}).addCondition("HAS_ITEM", Items.SUSPICIOUS_STEW).addCondition("HAS_TAG", "Effects").addCondition((i,t,c) -> {return ConfigManager.getSuspiciousStewToggle();}));
				
				//TODO: COMPASS
				registerTooltip(new CustomTooltip("COMPASS", (item,tag,context) ->  {
					List<Text> result = new ArrayList<>();
					if(tag.getBoolean("LodestoneTracked")) {
						String lodestoneDimension = tag.getString("LodestoneDimension");
						MutableText line = new TranslatableText(Blocks.LODESTONE.getTranslationKey()).append(": ").formatted(Formatting.DARK_GRAY);
						if(tag.contains("LodestonePos")) {
							CompoundTag lodestonePosition = tag.getCompound("LodestonePos");
							
							LiteralText position = new LiteralText("(" + lodestonePosition.getInt("X") + " " + lodestonePosition.getInt("Y") + " " + lodestonePosition.getInt("Z") + ")");
							line.append(position.formatted(Formatting.GRAY));
							result.add(line);
							
						} else {
							result.add(line.append(" ").append(new LiteralText("UNKNOWN").formatted(Formatting.GRAY,Formatting.OBFUSCATED)));
						}
						line = new LiteralText("  ("+ lodestoneDimension + ")").formatted(Formatting.GRAY);
						result.add(line);
						
						result.add(new LiteralText(""));
					}
					return result;
				}).addCondition("HAS_ITEM", Items.COMPASS).addCondition("HAS_TAG", "LodestoneTracked").addCondition((i,t,c) -> {return ConfigManager.getCompassToggle();}));
				
				//TODO: BOOK
				registerTooltip(new CustomTooltip("BOOK", (item,tag,context) -> {
					List<Text> result = new ArrayList<>();
					
					int pages = tag.getList("pages", 8).size();
					MutableText line = new TranslatableText("book.pageIndicator",1,pages).formatted(Formatting.GRAY);
					result.add(line);
					
					if(tag.contains("resolved")) {
						line = new LiteralText("Resolved: ").formatted(Formatting.GRAY);
						boolean resolved = tag.getBoolean("resolved");
						line.append(new LiteralText(Boolean.toString(resolved)).formatted(Formatting.YELLOW));
						
						result.add(line);
					}
					
					if(tag.contains("title")) {
						line = new LiteralText("Title: ").formatted(Formatting.GRAY);
						String title = tag.getString("title");
						line.append(new LiteralText(title).formatted(Formatting.AQUA));
						
						result.add(line);
					}
					
					return result;
				}).addCondition("HAS_TAG", "pages").addCondition("HAS_ITEM", Items.WRITTEN_BOOK, Items.WRITABLE_BOOK).addCondition((i,t,c) -> {return ConfigManager.getBookToggle();}));
				
				//TODO: CUSTOMMODELDATA
				registerTooltip(new CustomTooltip("CUSTOMMODELDATA", (item,tag,context) ->  {
					List<Text> result = new ArrayList<>();
					int data = tag.getInt("CustomModelData");
					
					MutableText line = new LiteralText("CustomModelData: ").formatted(Formatting.GRAY);
					line.append(new LiteralText(Integer.toString(data)).formatted(Formatting.YELLOW));
					
					result.add(line);
					return result;
				}).addCondition("HAS_TAG", "CustomModelData").addCondition((i,t,c) -> {return ConfigManager.getCustomModelDataToggle();}));
				
				//TODO: REPAIRCOST
				registerTooltip(new CustomTooltip("REPAIRCOST", (item,tag,context) ->  {
					List<Text> result = new ArrayList<>();
					int data = tag.getInt("RepairCost");
					
					MutableText line = new LiteralText("RepairCost: ").formatted(Formatting.GRAY);
					line.append(new LiteralText(Integer.toString(data)).formatted(Formatting.YELLOW));
					
					result.add(line);
					return result;
				}).addCondition("HAS_TAG", "RepairCost").addCondition((i,t,c) -> {return ConfigManager.getRepairCostToggle();}));
	}
	
	public static void registerAllCustomTooltips() {
		registeredTooltips.clear();
		
		//TODO: BuiltIn
		registerBuiltInTooltips();
		
		//TODO: Custom
		@SuppressWarnings("resource")
		File tooltipsFolder = new File(MinecraftClient.getInstance().runDirectory, "tooltips");
		
		LOGGER.info("Attempting to load custom tooltips from " + tooltipsFolder.getAbsolutePath());
		
		if(tooltipsFolder.exists()) {
			for(File tooltip : tooltipsFolder.listFiles((dir, name) -> {return name.endsWith(".json");})) {
				LOGGER.info("Loading custom tooltip: " + tooltip.getName());

				try {
					JsonReader reader = new JsonReader(new FileReader(tooltip));
					JsonParser parser = new JsonParser();
					JsonObject root = parser.parse(reader).getAsJsonObject();
					
					CustomTooltip toRegister = TooltipDeserializer.deserializeTooltip(root);
					
					registerTooltip(toRegister);
					
				} catch (Exception e) {
					LOGGER.error("Error while parsing " + tooltip.getName() + ":" + e);
				}
			}
		} else {
			tooltipsFolder.mkdir();
		}
		
		
	}
}
