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
package me.b0iizz.advancednbttooltip.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

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
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
	}

	/**
	 * Loads the config at the start of the game. Should not be called except in
	 * {@link me.b0iizz.advancednbttooltip.ModMain ModMain}
	 */
	public static void loadConfig() {
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	// TODO: Category General Options

	/**
	 * @return The state of the toggle controlling the "Suspicious Stew" tooltip.
	 */
	public static boolean getSuspiciousStewToggle() {
		return config.toggleSuspiciousStewTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Compass" tooltip.
	 */
	public static boolean getCompassToggle() {
		return config.toggleCompassTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Book" tooltip.
	 */
	public static boolean getBookToggle() {
		return config.toggleBookTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "CustomModelData" tooltip.
	 */
	public static boolean getCustomModelDataToggle() {
		return config.toggleCustomModelDataTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "RepairCost" tooltip.
	 */
	public static boolean getRepairCostToggle() {
		return config.toggleRepairCostTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Bee" tooltip.
	 */
	public static boolean getBeeToggle() {
		return config.toggleBeeTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "SpawnEggs" tooltip.
	 */
	public static boolean getSpawnEggToggle() {
		return config.toggleSpawnEggTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Signs" tooltip.
	 */
	public static boolean getSignsToggle() {
		return config.toggleSignsTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "Command Blocks" tooltip.
	 */
	public static boolean getCommandBlocksToggle() {
		return config.toggleCommandBlocksTooltip;
	}

	/**
	 * @return The state of the toggle controlling the "HideFlags" tooltip.
	 */
	public static boolean getHideFlagsToggle() {
		return config.toggleHideFlagsTooltip;
	}

	/**
	 * @return The location where custom tooltips should be placed
	 */
	public static ModConfig.TooltipPosition getTooltipPosition() {
		return config.tooltipPosition;
	}

	// TODO: Category Technical Options

	/**
	 * @return The state of the toggle controlling whether a notice should be added
	 *         to the title screen when a new update is out.
	 */
	public static boolean getMainMenuUpdateNoticeToggle() {
		return config.mainMenuUpdateNotice;
	}

	/**
	 * @return <b>true</b>, when the result of {@link "getItemStackInjectorBitmask()"} should be bitwise AND-ed with the
	 *         HideFlags property. <br>
	 *         <b>false</b>, when not.
	 */
	public static boolean isUsingItemStackInjection() {
		return config.useItemStackInjector;
	}

	/**
	 * @return The bitmask which is then bitwise AND-ed with the HideFlags property
	 */
	public static int getItemStackInjectorBitmask() {
		int mask = 0x7F;

		if (config.injectorOptions.overrideEnchantments)
			mask &= 0x7E;
		if (config.injectorOptions.overrideAttributeModifiers)
			mask &= 0x7D;
		if (config.injectorOptions.overrideUnbreakable)
			mask &= 0x7B;
		if (config.injectorOptions.overrideCanDestroy)
			mask &= 0x77;
		if (config.injectorOptions.overrideCanPlaceOn)
			mask &= 0x6F;
		if (config.injectorOptions.overrideAppendTooltip)
			mask &= 0x5F;
		if (config.injectorOptions.overrideDyeTooltip)
			mask &= 0x3F;

		return mask;
	}
}
