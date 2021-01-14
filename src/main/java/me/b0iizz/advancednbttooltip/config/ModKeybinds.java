package me.b0iizz.advancednbttooltip.config;

import org.lwjgl.glfw.GLFW;

import me.b0iizz.advancednbttooltip.ModMain;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
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
		openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + ModMain.modid + ".openConfig",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category." + ModMain.modid + ".keys"));
	}

	/**
	 * Updates all KeyBindings
	 * 
	 * @param client The Minecraft Client
	 */
	public static void updateKeyBindings(MinecraftClient client) {
		if (openConfig.isPressed() && client.currentScreen == null) {
			client.openScreen(ConfigManager.getConfigScreen(null).get());
		}
	}

}
