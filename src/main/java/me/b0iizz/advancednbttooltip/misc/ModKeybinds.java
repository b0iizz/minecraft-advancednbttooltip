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

import org.lwjgl.glfw.GLFW;

import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.gui.TooltipsScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * A class containing everything important to Keybindings
 * 
 * @author B0IIZZ
 */
public final class ModKeybinds {

	private static KeyBinding openConfig;

	/**
	 * Initializes all KeyBindings
	 */
	public static void initKeyBindings() {
		openConfig = KeyBindingHelper
				.registerKeyBinding(new KeyBinding("key." + AdvancedNBTTooltips.modid + ".openConfig",
						InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category." + AdvancedNBTTooltips.modid + ".keys"));
	}

	/**
	 * Updates all KeyBindings
	 * 
	 * @param client The Minecraft Client
	 */
	public static void updateKeyBindings(MinecraftClient client) {
		if (openConfig.isPressed() && client.currentScreen == null) {
			client.setScreen(new TooltipsScreen());
		}
	}

}
