package me.b0iizz.advancednbttooltip.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemTooltipComponent implements TooltipComponent {

	private final ItemStack[] items;
	private final int width;
	private final float scale;

	public ItemTooltipComponent(ItemStack[] items, int width, float scale) {
		this.items = items;
		this.width = width;
		this.scale = MathHelper.clamp(scale, .25f, 2f);
	}

	@Override
	public int getHeight() {
		return Math.round(this.scale * getRows() * 18) + 1;
	}

	@Override
	public int getWidth(TextRenderer tr) {
		return Math.round(this.scale * getColumns() * 18) + 1;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer,
			int z) {
		MatrixStack itemMatrices = RenderSystem.getModelViewStack();
		itemMatrices.push();
		itemMatrices.translate(x + 1, y + 1, 0);
		itemMatrices.scale(this.scale, this.scale, 1);
		int idx = 0;
		for (int row = 0; row < getRows(); row++)
			for (int col = 0; col < getColumns(); col++) {
				ItemStack stack = items[idx++];
				itemRenderer.renderInGuiWithOverrides(stack, col * 18, row * 18, idx);
				itemRenderer.renderGuiItemOverlay(textRenderer, stack, col * 18, row * 18);
			}
		itemMatrices.pop();
	}

	private int getColumns() {
		return Math.min(width > 0 ? width : items.length, items.length);
	}

	private int getRows() {
		return width == 0 ? 1 : Math.floorDiv(items.length, width) + (items.length % width != 0 ? 1 : 0);
	}

}
