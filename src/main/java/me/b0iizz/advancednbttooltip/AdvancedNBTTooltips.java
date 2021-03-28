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
package me.b0iizz.advancednbttooltip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.b0iizz.advancednbttooltip.api.AbstractCustomTooltip;
import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.gui.HudTooltipRenderer;
import me.b0iizz.advancednbttooltip.misc.CustomTooltipResourceReloadListener;
import me.b0iizz.advancednbttooltip.misc.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * The Fabric Entrypoint of this mod. <br>
 * <br>
 * <b>Implements:</b> <br>
 * {@link ClientModInitializer}
 * 
 * @author B0IIZZ
 */
public class AdvancedNBTTooltips implements ClientModInitializer {

	/**
	 * The mod's modid
	 */
	public static final String modid = "advancednbttooltip";

	/**
	 * The list of all loaded tooltips
	 */
	public static final Map<Identifier, AbstractCustomTooltip> TOOLTIPS = new HashMap<>();

	/**
	 * Constructs a new {@link Identifier} consisting of this mod's modid and the
	 * given name.
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

		ItemTooltipCallback.EVENT.register(AdvancedNBTTooltips::getTooltip);

		ModKeybinds.initKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(ModKeybinds::updateKeyBindings);

		HudTooltipRenderer.setup();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new CustomTooltipResourceReloadListener());

		UpdateChecker.refreshUpdates();
	}

	/**
	 * The tooltip handler for the ItemTooltipCallback
	 * 
	 * @param stack The item stack
	 * @param ctx   The context of the tooltip
	 * @param lines The lines in the tooltip
	 */
	public static void getTooltip(ItemStack stack, TooltipContext ctx, List<Text> lines) {
		if (ConfigManager.getTooltipToggle()) {
			ArrayList<Text> text = new ArrayList<>();
			appendCustomTooltip(stack.copy(), text, ctx);

			if (!lines.isEmpty() && !text.isEmpty())
				text.add(0, new LiteralText(""));

			if (ConfigManager.getTooltipPosition() == TooltipPosition.TOP && !text.isEmpty() && lines.size() > 1)
				text.add(new LiteralText(" "));

			lines.addAll(ConfigManager.getTooltipPosition().position(lines), text);
		}
	}

	/**
	 * Used by the ItemTooltipCallback function to interact with the tooltip
	 * pipeline.
	 * 
	 * @param stack   The {@link ItemStack} of which a tooltip should be generated.
	 * @param tooltip The List of text to add Tooltips to.
	 * @param context The {@link TooltipContext} where the tooltip is being
	 *                generated.
	 *
	 */
	public static void appendCustomTooltip(ItemStack stack, List<Text> tooltip, TooltipContext context) {
		Item item = stack.getItem();
		CompoundTag tag = stack.getTag();
		TOOLTIPS.entrySet().stream().sorted((a, b) -> a.getKey().toString().compareTo(b.getKey().toString()))
				.forEachOrdered(t -> tooltip.addAll(t.getValue().makeTooltip(item, tag, context)));
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

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
