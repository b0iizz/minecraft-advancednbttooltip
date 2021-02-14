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

import java.util.List;

import me.b0iizz.advancednbttooltip.ModMain;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;

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
@Config(name = ModMain.modid)
public class ModConfig extends PartitioningSerializer.GlobalData {

	@ConfigEntry.Category("nbt_general")
	@ConfigEntry.Gui.TransitiveObject
	GeneralConfig general = new GeneralConfig();
	
	@ConfigEntry.Category("nbt_toggle")
	@ConfigEntry.Gui.TransitiveObject
	ToggleConfig toggles = new ToggleConfig();
	
	@ConfigEntry.Category("nbt_hud")
	@ConfigEntry.Gui.TransitiveObject
	HudConfig hud = new HudConfig();
	
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
	 * The Category of the config containing all toggles
	 *
	 * @author B0IIZZ
	 */
	@Config(name = "nbt_toggle")
	public static class ToggleConfig implements ConfigData {

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleSuspiciousStewTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleCompassTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleBookTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleCustomModelDataTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleRepairCostTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleBeeTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleSpawnEggTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleSignsTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleCommandBlocksTooltip = true;

		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean toggleHideFlagsTooltip = true;
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
		int tooltipColor = 0x5000ff;
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		@ConfigEntry.Gui.Tooltip
		HudTooltipPosition hudTooltipPosition = HudTooltipPosition.TOP_LEFT;
		
	}
	
	/**
	 * An enum representing the position of custom tooltips in the tooltip list
	 * 
	 * @author B0IIZZ
	 */
	@SuppressWarnings("javadoc")
	public static enum TooltipPosition {
		TOP(1), BOTTOM(-1);

		private final int offset;

		private TooltipPosition(int offset) {
			this.offset = offset;
		}

		public int position(List<?> list) {
			return offset < 0 ? list.size() + offset + 1 : offset;
		}
	}
	
	/**
	 * An enum representing the position of the HUD tooltip 
	 * 
	 * @author B0IIZZ
	 */
	@SuppressWarnings("javadoc")
	public static enum HudTooltipPosition {
		TOP_LEFT(Anchor.START, Anchor.START), TOP(Anchor.MIDDLE, Anchor.START), TOP_RIGHT(Anchor.END, Anchor.START), CENTER(Anchor.MIDDLE, Anchor.MIDDLE_START), BOTTOM_LEFT(Anchor.START, Anchor.END), BOTTOM_RIGHT(Anchor.END, Anchor.END);
		
		private final Anchor x;
		private final Anchor y;
		
		private HudTooltipPosition(Anchor x, Anchor y) {
			this.x = x;
			this.y = y;
		}
		
		public Anchor getX() {
			return x;
		}
		
		public Anchor getY() {
			return y;
		}
		
		public static enum Anchor {
			START,MIDDLE,MIDDLE_START,END;
			
			
			public int get(int sizeObj, int maxSize, int offset) {
				int maxS = maxSize - 2 * offset;
				int prefX = 0;
				switch(this) {
				case START:
					prefX = 0;
					break;
				case MIDDLE:
					prefX = maxS / 2 - sizeObj / 2;
					break;
				case MIDDLE_START:
					prefX = maxS / 2 + 2 * offset;
					if(prefX + offset + sizeObj > maxSize) {
						prefX = maxSize - offset - sizeObj;
					}
					break;
				case END:
					prefX = maxS-sizeObj;
					break;
				}
				return prefX + offset;
			}
			
		}
		
	}
}
