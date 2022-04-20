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
package me.b0iizz.advancednbttooltip.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips.TooltipPosition;
import me.b0iizz.advancednbttooltip.gui.HudTooltipRenderer.HudTooltipPosition;
import me.b0iizz.advancednbttooltip.gui.HudTooltipRenderer.HudTooltipZIndex;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

/**
 * Handles communication between the mod's config and the rest of the mod.
 * 
 * @author B0IIZZ
 */
public class ConfigManager {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(AdvancedNBTTooltips.modid);

	private static ModConfig config;

	private static Map<Identifier, Boolean> toggles = new HashMap<>();

	/**
	 * Registers the config for AutoConfig at the start of the game. Should not be
	 * called except in {@link me.b0iizz.advancednbttooltip.AdvancedNBTTooltips
	 * ModMain}
	 */
	public static void registerConfig() {
		AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
	}

	/**
	 * Loads the config at the start of the game. Should not be called except in
	 * {@link me.b0iizz.advancednbttooltip.AdvancedNBTTooltips ModMain}
	 */
	public static void loadConfig() {
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		readToggles();
	}

	/**
	 * @param parent The parent screen
	 * @return A new Config Screen
	 */
	public static Supplier<Screen> getConfigScreen(Screen parent) {
		return AutoConfig.getConfigScreen(ModConfig.class, parent);
	}

	// TODO: toggles

	/**
	 * Reads all toggles from the file
	 */
	public static void readToggles() {
		File tFile = configPath.resolve("toggles.json").toFile();
		if (!tFile.exists())
			try {
				tFile.createNewFile();
			} catch (IOException ignored) {
			}
		try (InputStream is = new FileInputStream(tFile)) {
			try (Reader r = new InputStreamReader(is)) {
				JsonElement json = gson.fromJson(r, JsonElement.class);
				if (json != null && json.isJsonObject()) {
					json.getAsJsonObject().entrySet().stream().filter(e -> e.getValue().isJsonPrimitive())
							.forEach(e -> toggles.put(new Identifier(e.getKey()), e.getValue().getAsBoolean()));
				}
			}
		} catch (Throwable t) {
			System.err.println("Could not load toggles!");
			t.printStackTrace();
		}
	}

	/**
	 * Writes all toggles to the file
	 */
	public static void writeToggles() {
		File tFile = configPath.resolve("toggles.json").toFile();
		if (!tFile.exists())
			try {
				tFile.createNewFile();
			} catch (IOException ignored) {
			}
		try (OutputStream os = new FileOutputStream(tFile)) {
			try (Writer w = new OutputStreamWriter(os)) {
				gson.toJson(toggles.entrySet().stream()
						.sorted((a, b) -> a.getKey().toString().compareTo(b.getKey().toString()))
						.collect(JsonObject::new, (obj, e) -> obj.addProperty(e.getKey().toString(), e.getValue()),
								(obj, obj2) -> obj2.entrySet().forEach(e -> obj.add(e.getKey(), e.getValue()))),
						gson.newJsonWriter(w));
			}
		} catch (Throwable t) {
			System.err.println("Could not write toggles!");
			t.printStackTrace();
		}
	}

	/**
	 * @param id the id of a tooltip
	 * @return if the tooltip is enabled
	 */
	public static boolean isEnabled(Identifier id) {
		return toggles.computeIfAbsent(id, newId -> newId.getNamespace().equals(AdvancedNBTTooltips.modid));
	}

	/**
	 * Toggles a tooltip with the specified id
	 * 
	 * @param id the id of the tooltip
	 * @return the new state of the tooltip
	 */
	public static boolean toggle(Identifier id) {
		toggles.put(id, !isEnabled(id));
		return isEnabled(id);
	}

	// TODO: Category General Options

	/**
	 * @return true when custom tooltips should be shown
	 */
	public static boolean getTooltipToggle() {
		return config.general.enableTooltips;
	}

	/**
	 * @return The state of the toggle controlling whether a notice should be added
	 *         to the title screen when a new update is out.
	 */
	public static boolean getMainMenuUpdateNoticeToggle() {
		return config.general.mainMenuUpdateNotice;
	}

	/**
	 * @return The location where custom tooltips should be placed
	 */
	public static TooltipPosition getTooltipPosition() {
		return config.general.tooltipPosition;
	}

	/**
	 * @return <b>true</b>, when the result of {@link #getHideflagOverrideBitmask()}
	 *         should be bitwise AND-ed with the HideFlags property. <br>
	 *         <b>false</b>, when not.
	 */
	public static boolean overrideHideFlags() {
		return config.general.overrideHideFlags;
	}

	/**
	 * @return The bitmask which is then bitwise AND-ed with the HideFlags property
	 */
	public static int getHideflagOverrideBitmask() {
		int mask = 0x7F;

		if (config.general.hideflagOverrides.overrideEnchantments)
			mask &= 0x7E;
		if (config.general.hideflagOverrides.overrideAttributeModifiers)
			mask &= 0x7D;
		if (config.general.hideflagOverrides.overrideUnbreakable)
			mask &= 0x7B;
		if (config.general.hideflagOverrides.overrideCanDestroy)
			mask &= 0x77;
		if (config.general.hideflagOverrides.overrideCanPlaceOn)
			mask &= 0x6F;
		if (config.general.hideflagOverrides.overrideAppendTooltip)
			mask &= 0x5F;
		if (config.general.hideflagOverrides.overrideDyeTooltip)
			mask &= 0x3F;

		return mask;
	}

	// TODO: HUD options

	/**
	 * @return true when HUD rendering is enabled
	 */
	public static boolean isHudRenderingEnabled() {
		return config.hud.enableHudRendering;
	}

	/**
	 * @return true when the HUD shows tooltips on dropped items
	 */
	public static boolean getDroppedItemToggle() {
		return config.hud.toggleDroppedItem;
	}

	/**
	 * @return true when the HUD shows tooltips on dropped items
	 */
	public static boolean getItemFrameToggle() {
		return config.hud.toggleItemFrame;
	}

	/**
	 * @return true when the HUD shows tooltips on dropped items
	 */
	public static boolean getArmorStandToggle() {
		return config.hud.toggleArmorStand;
	}

	/**
	 * @return the number of allowed lines in the HUD tooltip
	 */
	public static int getHudTooltipLineLimt() {
		return config.hud.tooltipLineLimit;
	}

	/**
	 * @return the color of the HUD tooltip
	 */
	public static int getHudTooltipColor() {
		return config.hud.tooltipColor;
	}

	/**
	 * @return the color of the HUD tooltip
	 */
	public static HudTooltipPosition getHudTooltipPosition() {
		return config.hud.hudTooltipPosition;
	}

	/**
	 * @return the z-index of the HUD tooltip
	 */
	public static HudTooltipZIndex getHudTooltipZIndex() {
		return config.hud.hudTooltipZ;
	}

	/**
	 * @return true when the maximum enchantment level should be appended
	 */
	public static boolean isShowMaxEnchantmentLevel() {
		return config.misc.showMaxEnchantmentLevel;
	}

	/**
	 * @return true if we should show the variant for axolotl buckets
	 */
	public static boolean isShowMiningSpeed() {
		return config.misc.showMiningSpeed;
	}

	/**
	 * @return true if we should show the variant for axolotl buckets
	 */
	public static boolean isShowAxolotlVariant() {
		return config.misc.showAxolotlVariant;
	}

	/**
	 * @return true if we should show the light level for light sources
	 */
	public static boolean isShowLightLevel() {
		return config.misc.showLightLevel;
	}
}
