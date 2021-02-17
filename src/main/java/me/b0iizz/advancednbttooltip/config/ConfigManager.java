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

import java.util.function.Supplier;

import me.b0iizz.advancednbttooltip.config.ModConfig.HudTooltipPosition;
import me.b0iizz.advancednbttooltip.config.ModConfig.HudTooltipZIndex;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import net.minecraft.client.gui.screen.Screen;

/**
 * Handles communication between the mod's config and the rest of the mod.
 * 
 * @author B0IIZZ
 */
public class ConfigManager {

	/**
	 * This mod's config
	 */
	private static ModConfig config;

	/**
	 * Registers the config for AutoConfig at the start of the game. Should not be
	 * called except in {@link me.b0iizz.advancednbttooltip.ModMain ModMain}
	 */
	public static void registerConfig() {
		AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
	}

	/**
	 * Loads the config at the start of the game. Should not be called except in
	 * {@link me.b0iizz.advancednbttooltip.ModMain ModMain}
	 */
	public static void loadConfig() {
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}
	
	/**
	 * @param parent The parent screen
	 * @return A new Config Screen
	 */
	public static Supplier<Screen> getConfigScreen(Screen parent) {
		return AutoConfig.getConfigScreen(ModConfig.class, parent);
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
	public static ModConfig.TooltipPosition getTooltipPosition() {
		return config.general.tooltipPosition;
	}

	/**
	 * @return <b>true</b>, when the result of
	 *         {@link #getHideflagOverrideBitmask()} should be bitwise AND-ed with
	 *         the HideFlags property. <br>
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

	// TODO: Category Toggle Options

	/**
	 * @return The state of the toggle controlling the "Suspicious Stew" tooltip.
	 */
	public static boolean getSuspiciousStewToggle() {
		return config.toggles.toggleSuspiciousStewTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Compass" tooltip.
	 */
	public static boolean getCompassToggle() {
		return config.toggles.toggleCompassTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Book" tooltip.
	 */
	public static boolean getBookToggle() {
		return config.toggles.toggleBookTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "CustomModelData" tooltip.
	 */
	public static boolean getCustomModelDataToggle() {
		return config.toggles.toggleCustomModelDataTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "RepairCost" tooltip.
	 */
	public static boolean getRepairCostToggle() {
		return config.toggles.toggleRepairCostTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Bee" tooltip.
	 */
	public static boolean getBeeToggle() {
		return config.toggles.toggleBeeTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "SpawnEggs" tooltip.
	 */
	public static boolean getSpawnEggToggle() {
		return config.toggles.toggleSpawnEggTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Signs" tooltip.
	 */
	public static boolean getSignsToggle() {
		return config.toggles.toggleSignsTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Command Blocks" tooltip.
	 */
	public static boolean getCommandBlocksToggle() {
		return config.toggles.toggleCommandBlocksTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "HideFlags" tooltip.
	 */
	public static boolean getHideFlagsToggle() {
		return config.toggles.toggleHideFlagsTooltip;
	}
	
	//TODO: HUD options
	
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
}
