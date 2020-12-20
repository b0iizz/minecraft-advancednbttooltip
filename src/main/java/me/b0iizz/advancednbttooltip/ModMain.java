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
package me.b0iizz.advancednbttooltip;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.config.CustomTooltipResourceReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

/**
 * The Fabric Entrypoint of this mod.
 * <br><br> <b>Implements:</b>
 * <br>{@link ClientModInitializer}
 * 
 * @author B0IIZZ
 */
public class ModMain implements ClientModInitializer {
	
	/**
	 * The mod's modid
	 */
	public static final String modid = "advancednbttooltip";
	
	/**
	 * Constructs a new {@link Identifier} consisting of this mod's modid and the given name.
	 * 
	 * @param name a name
	 * @return the {@link Identifier} of this mod corresponding to the given name.
	 */
	public static Identifier id(String name) {
		return new Identifier(modid, name);
	}
	
	/**
	 * Called on initialization. Registers and loads this mod's config.
	 */
	@Override
	public void onInitializeClient() {
		ConfigManager.registerConfig();
		ConfigManager.loadConfig();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new CustomTooltipResourceReloadListener());
		
		UpdateChecker.refreshUpdates();
		
		
	}

}
