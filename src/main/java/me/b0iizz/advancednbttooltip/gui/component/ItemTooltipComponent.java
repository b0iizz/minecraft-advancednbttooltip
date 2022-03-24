package me.b0iizz.advancednbttooltip.gui.component;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ItemTooltipComponent implements TooltipComponent {

	private final ItemStack[] items;

	private final int maxColumns;

	public ItemTooltipComponent(ItemStack[] items, int maxColumns) {
		this.items = items;
		this.maxColumns = maxColumns;
	}

	@Override
	public int getHeight() {
		return getRows() * 18;
	}

	@Override
	public int getWidth(TextRenderer tr) {
		return getColumns() * 18 + 1;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer,
			int z) {
		int idx = 0;
		for(int row = 0; row < getRows(); row++)
			for(int col = 0; col < getColumns(); col++) {
				ItemStack stack = items[idx++];
				itemRenderer.renderInGuiWithOverrides(stack, col * 18 + x + 1, row * 18 + y + 1, idx);
				itemRenderer.renderGuiItemOverlay(textRenderer, stack, col * 18 + x + 1, row * 18 + y + 1);
			}
	}

	private int getColumns() {
		return Math.min(maxColumns > 0 ? maxColumns : items.length, items.length);
	}

	private int getRows() {
		return Math.floorDiv(items.length, maxColumns) + (items.length % maxColumns != 0 ? 1 : 0);
	}

}
