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
package me.b0iizz.advancednbttooltip.misc;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.config.ConfigManager;

/**
 * The entrypoint for ModMenu by prospector <br>
 * <br>
 * <b>Implements:</b> <br>
 * {@link ModMenuApi}
 * 
 * @author B0IIZZ
 *
 */
public class ModMenuEntry implements ModMenuApi {

	/**
	 * @return This mod's modid.
	 */
	@Override
	public String getModId() {
		return AdvancedNBTTooltips.modid;
	}

	/**
	 * @return This mod's config's {@link ConfigScreenFactory}
	 */
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			return ConfigManager.getConfigScreen(parent).get();
		};

	}
}