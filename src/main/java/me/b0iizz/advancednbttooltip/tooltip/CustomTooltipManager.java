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

import me.b0iizz.advancednbttooltip.ModMain;
import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.tooltip.api.AbstractCustomTooltip;
import net.minecraft.block.Block;
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
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * A class handling registering and appending all the {@link CustomTooltip
 * custom tooltips}'s tooltip.
 * 
 * @author B0IIZZ
 */
public final class CustomTooltipManager {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * A map containing all the registered {@link CustomTooltip CustomTooltips}.
	 */
	private static Map<String, AbstractCustomTooltip> registeredTooltips;

	/**
	 * Registers a new {@link CustomTooltip CustomTooltip}.
	 * 
	 * @param tooltip : The {@link CustomTooltip CustomTooltip} to be registered.
	 */
	public static void registerTooltip(AbstractCustomTooltip tooltip) {
		if (registeredTooltips.containsKey(tooltip.getName()))
			return;
		registeredTooltips.put(tooltip.getName(), tooltip);
	}

	/**
	 * Used by the classes of the me.b0iizz.advancednbttooltip.mixin package to
	 * interact with the tooltip pipeline.
	 *
	 * @param stack   The {@link ItemStack} of which a tooltip should be generated.
	 * @param world   The {@link World} where this task is being executed.
	 * @param tooltip The List of text to add Tooltips to.
	 * @param context The {@link TooltipContext} where the tooltip is being
	 *                generated.
	 * @param info    The {@link CallbackInfo} provided by the mixins.
	 */
	public static void appendCustomTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context,
			CallbackInfo info) {
		appendCustomTooltip(stack, world, tooltip, context);
	}

	/**
	 * Used by the classes of the me.b0iizz.advancednbttooltip.mixin package to
	 * interact with the tooltip pipeline.
	 * 
	 * @param stack   The {@link ItemStack} of which a tooltip should be generated.
	 * @param world   The {@link World} where this task is being executed.
	 * @param tooltip The List of text to add Tooltips to.
	 * @param context The {@link TooltipContext} where the tooltip is being
	 *                generated.
	 *
	 */
	public static void appendCustomTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if (world == null)
			return;
		Item item = stack.getItem();
		CompoundTag tag = stack.getTag();
		for (AbstractCustomTooltip t : registeredTooltips.values()) {
			tooltip.addAll(t.makeTooltip(item, tag, context));
		}
	}

	static {
		registeredTooltips = new HashMap<>();
	}

	@SuppressWarnings("resource")
	private static void registerBuiltInTooltips() {
		// TODO: SUSPICIOUS STEW
		registerTooltip(new CustomTooltip("SUSPICIOUS_STEW", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();
			ListTag effects = tag.getList("Effects", 10);
			for (int i = 0; i < effects.size(); i++) {
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
		}).addCondition("IS_ITEM", Items.SUSPICIOUS_STEW).addCondition("HAS_TAG", "Effects")
				.addCondition((i, t, c) -> {
					return ConfigManager.getSuspiciousStewToggle();
				}));

		// TODO: COMPASS
		registerTooltip(new CustomTooltip("COMPASS", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();
			if (tag.getBoolean("LodestoneTracked")) {
				String lodestoneDimension = tag.getString("LodestoneDimension");
				MutableText line = new TranslatableText(Blocks.LODESTONE.getTranslationKey()).append(": ")
						.formatted(Formatting.DARK_GRAY);
				if (tag.contains("LodestonePos")) {
					CompoundTag lodestonePosition = tag.getCompound("LodestonePos");

					LiteralText position = new LiteralText("(" + lodestonePosition.getInt("X") + " "
							+ lodestonePosition.getInt("Y") + " " + lodestonePosition.getInt("Z") + ")");
					line.append(position.formatted(Formatting.GRAY));
					result.add(line);

				} else {
					result.add(line.append(" ")
							.append(new LiteralText("UNKNOWN").formatted(Formatting.GRAY, Formatting.OBFUSCATED)));
				}
				line = new LiteralText("  (" + lodestoneDimension + ")").formatted(Formatting.GRAY);
				result.add(line);

				result.add(new LiteralText(""));
			}
			return result;
		}).addCondition("IS_ITEM", Items.COMPASS).addCondition("HAS_TAG", "LodestoneTracked")
				.addCondition((i, t, c) -> {
					return ConfigManager.getCompassToggle();
				}));

		// TODO: BOOK
		registerTooltip(new CustomTooltip("BOOK", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();

			int pages = tag.getList("pages", 8).size();
			MutableText line = new TranslatableText("text." + ModMain.modid + ".tooltip.book.pages", pages)
					.formatted(Formatting.GRAY);
			result.add(line);

			if (tag.contains("resolved")) {
				line = new TranslatableText("text." + ModMain.modid + ".tooltip.book.resolved")
						.formatted(Formatting.GRAY);
				boolean resolved = tag.getBoolean("resolved");
				line.append(new LiteralText(Boolean.toString(resolved)).formatted(Formatting.YELLOW));

				result.add(line);
			}

			if (tag.contains("title")) {
				line = new TranslatableText("text." + ModMain.modid + ".tooltip.book.title").formatted(Formatting.GRAY);
				String title = tag.getString("title");
				line.append(new LiteralText(title).formatted(Formatting.AQUA));

				result.add(line);
			}

			return result;
		}).addCondition("HAS_TAG", "pages").addCondition("IS_ITEM", Items.WRITTEN_BOOK, Items.WRITABLE_BOOK)
				.addCondition((i, t, c) -> {
					return ConfigManager.getBookToggle();
				}));

		// TODO: CUSTOMMODELDATA
		registerTooltip(new CustomTooltip("CUSTOMMODELDATA", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();
			int data = tag.getInt("CustomModelData");

			MutableText line = new TranslatableText("text." + ModMain.modid + ".tooltip.custommodeldata")
					.formatted(Formatting.GRAY);
			line.append(new LiteralText(Integer.toString(data)).formatted(Formatting.YELLOW));

			result.add(line);
			return result;
		}).addCondition("HAS_TAG", "CustomModelData").addCondition((i, t, c) -> {
			return ConfigManager.getCustomModelDataToggle();
		}));

		// TODO: REPAIRCOST
		registerTooltip(new CustomTooltip("REPAIRCOST", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();
			int data = tag.getInt("RepairCost");

			if (data == 0)
				return result;

			MutableText line = new TranslatableText("text." + ModMain.modid + ".tooltip.repaircost")
					.formatted(Formatting.GRAY);
			line.append(new LiteralText(Integer.toString(data)).formatted(Formatting.YELLOW));

			result.add(line);
			return result;
		}).addCondition("HAS_TAG", "RepairCost").addCondition((i, t, c) -> {
			return ConfigManager.getRepairCostToggle();
		}));

		// TODO: BEES
		registerTooltip(new CustomTooltip("BEENESTS", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();

			ListTag bees = tag.getCompound("BlockEntityTag").getList("Bees", 10);
			int size = bees.size();

			if (size == 0)
				return result;

			MutableText line = new TranslatableText("text." + ModMain.modid + ".tooltip.bees")
					.formatted(Formatting.GRAY);
			line.append(new LiteralText(Integer.toString(size)).formatted(Formatting.YELLOW));

			result.add(line);
			return result;
		}).addCondition("HAS_TAG", "BlockEntityTag.Bees").addCondition((i, t, c) -> {
			return ConfigManager.getBeeToggle();
		}));

		// TODO: SPAWNEGGS
		registerTooltip(new CustomTooltip("SPAWNEGGS", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();

			CompoundTag entityTag = tag.getCompound("EntityTag");
			String id = entityTag.getString("id");

			MutableText line = new TranslatableText("text." + ModMain.modid + ".tooltip.spawneggs")
					.formatted(Formatting.GRAY);
			line.append(new TranslatableText("entity." + id.replace(':', '.')).formatted(Formatting.YELLOW));

			result.add(line);
			return result;
		}).addCondition("HAS_TAG", "EntityTag").addCondition((i, t, c) -> {
			return ConfigManager.getSpawnEggToggle();
		}));

		// TODO: SIGNS
		registerTooltip(new CustomTooltip("SIGNS", (item, tag, context) -> {
			final String startText = "----------------";
			final String endText = "----------------";

			List<Text> result = new ArrayList<>();

			CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");

			int preferredWidth = MinecraftClient.getInstance().textRenderer.getWidth(endText);

			result.add(new LiteralText(startText).formatted(Formatting.GRAY));

			for (int i = 1; i < 5; i++) {
				boolean hasText = blockEntityTag.contains("Text" + i);
				String text = hasText ? Text.Serializer.fromJson(blockEntityTag.getString("Text" + i)).asString() : "";
				while (preferredWidth >= MinecraftClient.getInstance().textRenderer.getWidth(' ' + text + ' '))
					text = ' ' + text + ' ';
				result.add(new LiteralText(text).formatted(Formatting.GRAY));
			}

			result.add(new LiteralText(endText).formatted(Formatting.GRAY));

			return result;
		}).addCondition((item, tag, context) -> {
			if (tag == null)
				return false;

			boolean isSign = Block.getBlockFromItem(item).isIn(BlockTags.SIGNS);

			boolean text1 = tag.getCompound("BlockEntityTag").contains("Text1");
			boolean text2 = tag.getCompound("BlockEntityTag").contains("Text2");
			boolean text3 = tag.getCompound("BlockEntityTag").contains("Text3");
			boolean text4 = tag.getCompound("BlockEntityTag").contains("Text4");

			return isSign && (text1 || text2 || text3 || text4);
		}).addCondition((i, t, c) -> {
			return ConfigManager.getSignsToggle();
		}));

		// TODO: COMMAND_BLOCKS
		registerTooltip(new CustomTooltip("COMMAND_BLOCKS", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();

			CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");

			MutableText line = new TranslatableText("text." + ModMain.modid + ".tooltip.command_blocks")
					.formatted(Formatting.GRAY);
			result.add(line);

			String command = blockEntityTag.getString("Command");
			if (command.length() > 40)
				command = command.substring(0, 40) + "...";
			line = new LiteralText("  " + command).formatted(Formatting.GRAY);

			result.add(line);
			return result;
		}).addCondition("IS_ITEM", Blocks.COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK)
				.addCondition("HAS_TAG", "BlockEntityTag").addCondition((i, t, c) -> {
					return ConfigManager.getCommandBlocksToggle();
				}));

		// TODO: HIDEFLAGS

		registerTooltip(new CustomTooltip("HIDEFLAGS", (item, tag, context) -> {
			List<Text> result = new ArrayList<>();

			int hideFlags = tag.getInt("HideFlags");

			result.add(new TranslatableText("text." + ModMain.modid + ".tooltip.hideflag").formatted(Formatting.GRAY));

			for (int i = 0; i < ItemStack.TooltipSection.values().length; i++) {
				if (((1 << i) & hideFlags) > 0) {
					LiteralText line = new LiteralText(" -");
					line.append(new TranslatableText("text." + ModMain.modid + ".hideflag."
							+ ItemStack.TooltipSection.values()[i].name().toLowerCase()));
					result.add(line.formatted(Formatting.GRAY, Formatting.ITALIC));
				}
			}

			return result;
		}).addCondition("HAS_TAG", "HideFlags").addCondition((i, t, c) -> {
			return ConfigManager.getHideFlagsToggle();
		}));
	}

	/**
	 * Reloads all custom tooltips
	 */
	public static void reloadAllCustomTooltips() {
		registeredTooltips.clear();

		// TODO: BuiltIn
		registerBuiltInTooltips();

		// TODO: Custom
		@SuppressWarnings("resource")
		File tooltipsFolder = new File(MinecraftClient.getInstance().runDirectory, "tooltips");

		LOGGER.info("Attempting to load custom tooltips from " + tooltipsFolder.getAbsolutePath());

		if (tooltipsFolder.exists()) {
			for (File tooltip : tooltipsFolder.listFiles((dir, name) -> {
				return name.endsWith(".json");
			})) {
				LOGGER.info("Loading custom tooltip: " + tooltip.getName());

				try {
					JsonReader reader = new JsonReader(new FileReader(tooltip));
					JsonParser parser = new JsonParser();
					JsonObject root = parser.parse(reader).getAsJsonObject();

					AbstractCustomTooltip toRegister = AbstractCustomTooltip.LOADER.load(root);

					registerTooltip(toRegister);

				} catch (Exception e) {
					LOGGER.error("Error while parsing " + tooltip.getName() + ":", e);
				}
			}
		} else {
			tooltipsFolder.mkdir();
		}

	}
}
