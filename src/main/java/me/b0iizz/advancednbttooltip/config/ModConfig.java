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

import org.apache.logging.log4j.Level;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;

/**
 * The class representation of this mod's config. Based on this class,
 * AutoConfig generates a <i>.json</i> config file as well as an option screen.
 * 
 * <br><br> <b>Implements:</b>
 * <br>{@link ConfigData}
 * 
 * @author B0IIZZ
 */
@Config(name = "advancednbttooltip")
public class ModConfig implements ConfigData {
	
	//TODO: Category General Options
	
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
	
	
	
	//TODO: Category Technical Options
	
	/**
	 * See In-game description.
	 */
	@ConfigEntry.Gui.PrefixText
	@ConfigEntry.Gui.Tooltip(count = 2)
	boolean useItemStackInjector = true;
	
	/**
	 * See In-game description.
	 */
	@ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	HideFlagsOverrides injectorOptions = new HideFlagsOverrides();
	
	/**
	 * See In-game description.
	 */
	@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
	DeserializerLevel level = DeserializerLevel.DEBUG;
	
	static class HideFlagsOverrides {
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean overrideEnchantments = false;
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean overrideAttributeModifiers = false;
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean overrideUnbreakable = false;
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean overrideCanDestroy = false;
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean overrideCanPlaceOn = false;
		
		/**
		 * See In-game description.
		 */
		@ConfigEntry.Gui.Tooltip
		boolean overrideAppendTooltip = true;
	}
	
	static enum DeserializerLevel {
		DEBUG(Level.DEBUG),INFO(Level.INFO);
		
		private Level level;
		
		private DeserializerLevel(Level level) {
			this.level = level;
		}
		
		public Level getLevel()  {
			return level;
		}
	}
}
