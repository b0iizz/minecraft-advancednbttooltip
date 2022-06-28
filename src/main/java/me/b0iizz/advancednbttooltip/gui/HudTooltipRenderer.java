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
package me.b0iizz.advancednbttooltip.gui;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for rendering tooltips on the in-game HUD
 *
 * @author B0IIZZ
 */
public class HudTooltipRenderer implements CustomTooltipRenderer {

	public static void setup() {
		HudTooltipRenderer tooltipHudRenderer = new HudTooltipRenderer(MinecraftClient.getInstance());

		HudRenderCallback.EVENT.register((matrices, delta) -> {
			if (ConfigManager.isHudRenderingEnabled())
				tooltipHudRenderer.draw(matrices, delta);
		});
	}

	private final MinecraftClient client;
	private final HudTooltipPicker picker;

	/**
	 * @param client The client this renderer should draw with
	 */
	private HudTooltipRenderer(MinecraftClient client) {
		this.client = client;
		this.picker = new HudTooltipPicker(client);
	}

	/**
	 * @param matrices  the current MatrixStack
	 * @param tickDelta the number of unprocessed ticks
	 */
	public void draw(MatrixStack matrices, float tickDelta) {
		this.picker.getItem(tickDelta).ifPresent(stack -> renderTooltip(matrices, stack, 0, 0,
				HudTooltipContext.valueOf(this.client.options.advancedItemTooltips), null, this.client.player));
	}

	@Override
	public void renderComponents(MatrixStack matrices, ItemStack stack, List<TooltipComponent> components, int x,
								 int y) {
		int componentLimit = ConfigManager.getHudTooltipLineLimt();
		if (components.size() > componentLimit && componentLimit > 0) {
			components = components.stream().limit(componentLimit).collect(Collectors.toCollection(ArrayList::new));
			components.add(TooltipComponent.of(Text.of("...").asOrderedText()));
		}

		int width = this.client.getWindow().getScaledWidth();
		int height = this.client.getWindow().getScaledHeight();

		int tooltipWidth = TooltipRenderingUtils.getWidth(this.client.textRenderer, components);
		int tooltipHeight = TooltipRenderingUtils.getHeight(this.client.textRenderer, components);

		tooltipHeight = Math.max(tooltipHeight, 16) + 8;

		HudTooltipPosition position = ConfigManager.getHudTooltipPosition();

		x = position.getX().get(tooltipWidth + 23, width, 10);
		y = position.getY().get(tooltipHeight, height, 10);

		int z = ConfigManager.getHudTooltipZIndex().getZ();
		int color = ConfigManager.getHudTooltipColor();

		matrices.push();
		TooltipRenderingUtils.drawBox(matrices, x, y, 24, 24, -100, color);

		this.client.getItemRenderer().renderInGui(stack, x + 4, y + 4);

		if (!components.isEmpty()) {
			TooltipRenderingUtils.drawBox(matrices, x + 23, y, tooltipWidth, tooltipHeight, z, color);
			TooltipRenderingUtils.drawComponents(this.client.textRenderer, this.client.getItemRenderer(), matrices,
					x + 28, y + 4, z, components);
		}
		matrices.pop();
	}

	public enum HudTooltipContext implements TooltipContext {
		NORMAL, ADVANCED;

		@Override
		public boolean isAdvanced() {
			return this.ordinal() >= ADVANCED.ordinal();
		}

		public static HudTooltipContext valueOf(boolean isAdvanced) {
			return isAdvanced ? ADVANCED : NORMAL;
		}

	}

	/**
	 * An enum representing the position of the HUD tooltip
	 *
	 * @author B0IIZZ
	 */
	public enum HudTooltipPosition {
		TOP_LEFT(Anchor.START, Anchor.START), TOP(Anchor.MIDDLE, Anchor.START), TOP_RIGHT(Anchor.END, Anchor.START),
		CENTER(Anchor.MIDDLE, Anchor.MIDDLE_START), BOTTOM_LEFT(Anchor.START, Anchor.END),
		BOTTOM_RIGHT(Anchor.END, Anchor.END);

		private final Anchor x;
		private final Anchor y;

		HudTooltipPosition(Anchor x, Anchor y) {
			this.x = x;
			this.y = y;
		}

		public Anchor getX() {
			return x;
		}

		public Anchor getY() {
			return y;
		}

		@Override
		public String toString() {
			return name().toLowerCase().replace('_', ' ');
		}

		public enum Anchor {
			START, MIDDLE, MIDDLE_START, END;

			public int get(int sizeObj, int maxSize, int offset) {
				int maxS = maxSize - 2 * offset;
				int prefX = 0;
				switch (this) {
					case START:
						prefX = 0;
						break;
					case MIDDLE:
						prefX = maxS / 2 - sizeObj / 2;
						break;
					case MIDDLE_START:
						prefX = maxS / 2 + offset;
						if (prefX + offset + sizeObj > maxSize) {
							prefX = maxSize - 2 * offset - sizeObj;
						}
						break;
					case END:
						prefX = maxS - sizeObj;
						break;
				}
				return prefX + offset;
			}

		}

	}

	/**
	 * An enum representing the position of custom tooltips in the tooltip list
	 *
	 * @author B0IIZZ
	 */
	public enum HudTooltipZIndex {
		TOP(400), BOTTOM(-100);

		private final int z;

		HudTooltipZIndex(int z) {
			this.z = z;
		}

		public int getZ() {
			return z;
		}

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
