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

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips.TooltipPosition;
import me.b0iizz.advancednbttooltip.gui.HudTooltipRenderer.HudTooltipPosition;
import me.b0iizz.advancednbttooltip.gui.HudTooltipRenderer.HudTooltipZIndex;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

/**
 * The class representation of this mod's config. Based on this class,
 * AutoConfig generates a <i>.json</i> config file as well as an option screen.
 * 
 * <br>
 * <br>
 * <b>Implements:</b> <br>
 * {@link ConfigData}
 * 
 * @author B0IIZZ
 */
@Config(name = AdvancedNBTTooltips.modid)
public class ModConfig extends PartitioningSerializer.GlobalData {

	@ConfigEntry.Category("nbt_general")
	@ConfigEntry.Gui.TransitiveObject
	GeneralConfig general = new GeneralConfig();

	@ConfigEntry.Category("nbt_hud")
	@ConfigEntry.Gui.TransitiveObject
	HudConfig hud = new HudConfig();

	@ConfigEntry.Category("nbt_misc")
	@ConfigEntry.Gui.TransitiveObject
	MiscConfig misc = new MiscConfig();

	/**
	 * The Category of the config containing all general options
	 *
	 * @author B0IIZZ
	 */
	@Config(name = "nbt_general")
	public static class GeneralConfig implements ConfigData {

		/**
		 * See In-game description.
		 */
		boolean enableTooltips = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		@ConfigEntry.Gui.Tooltip
		TooltipPosition tooltipPosition = TooltipPosition.TOP;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean mainMenuUpdateNotice = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.PrefixText
		@ConfigEntry.Gui.Tooltip(count = 2)
		boolean overrideHideFlags = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.CollapsibleObject
		@ConfigEntry.Gui.Tooltip
		HideFlagsOverrides hideflagOverrides = new HideFlagsOverrides();

		static class HideFlagsOverrides {

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideEnchantments = true;

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideAttributeModifiers = true;

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideUnbreakable = true;

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideCanDestroy = true;

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideCanPlaceOn = true;

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideAppendTooltip = true;

			/**
			 * See In-game description.
			 */
			@ConfigEntry.Gui.Tooltip
			boolean overrideDyeTooltip = true;
		}
	}

	/**
	 * The Category of the config containing everything related to the HUD rendering
	 *
	 * @author B0IIZZ
	 */
	@Config(name = "nbt_hud")
	public static class HudConfig implements ConfigData {

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean enableHudRendering = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleDroppedItem = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleItemFrame = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleArmorStand = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.BoundedDiscrete(min = 0, max = 10)
		@ConfigEntry.Gui.Tooltip
		int tooltipLineLimit = 7;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.ColorPicker
		@ConfigEntry.Gui.Tooltip
		int tooltipColor = 0x270080;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		@ConfigEntry.Gui.Tooltip
		HudTooltipPosition hudTooltipPosition = HudTooltipPosition.TOP_LEFT;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		@ConfigEntry.Gui.Tooltip
		HudTooltipZIndex hudTooltipZ = HudTooltipZIndex.TOP;

	}

	/**
	 * The Category of the config containing all general options
	 *
	 * @author Rooftop Joe
	 */
	@Config(name = "nbt_misc")
	public static class MiscConfig implements ConfigData {

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean showMaxEnchantmentLevel = true;	

	}
}
