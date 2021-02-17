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
package me.b0iizz.advancednbttooltip.tooltip.hud;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

final class TooltipRenderingUtils {

	public static void drawItem(ItemStack stack, ItemRenderer itemRenderer, TextRenderer textRenderer,
			MatrixStack matrices, List<? extends OrderedText> lines, int x, int y, int width, int height, int z,
			int color) {
		matrices.push();
		drawBox(matrices, x, y, 24, 24, -100, color);

		itemRenderer.renderInGui(stack, x + 4, y + 4);

		if (!lines.isEmpty()) {
			drawBox(matrices, x + 24, y, width - 24, height, z, color);
			drawLines(textRenderer, matrices, x + 28, y + 4, z, lines);
		}
		matrices.pop();
	}

	public static void drawBox(MatrixStack matrices, int x, int y, int width, int height, int z, int accentColor) {
		matrices.push();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		Matrix4f modelMatrix = matrices.peek().getModel();

		int alpha = 0xff;
		int backgroundAlpha = 0xe0;

		int r = (accentColor >> 16) & 0xff;
		int g = (accentColor >> 8) & 0xff;
		int b = (accentColor >> 0) & 0xff;

		int accent = alpha << 24 | getPrimaryColor(r, g, b);
		int main = alpha << 24 | getSecondaryColor(r, g, b);
		int background = backgroundAlpha << 24 | getBackgroundColor(r, g, b);

		int innerX = x + 4;
		int innerY = y + 4;
		int innerW = width - 8;
		int innerH = height - 8;

		fillGradient(modelMatrix, bufferBuilder, innerX - 3, innerY - 4, innerX + innerW + 3, innerY - 3, z, background,
				background);
		fillGradient(modelMatrix, bufferBuilder, innerX - 3, innerY + innerH + 3, innerX + innerW + 3,
				innerY + innerH + 4, z, background, background);
		fillGradient(modelMatrix, bufferBuilder, innerX - 3, innerY - 3, innerX + innerW + 3, innerY + innerH + 3, z,
				background, background);
		fillGradient(modelMatrix, bufferBuilder, innerX - 4, innerY - 3, innerX - 3, innerY + innerH + 3, z, background,
				background);
		fillGradient(modelMatrix, bufferBuilder, innerX + innerW + 3, innerY - 3, innerX + innerW + 4,
				innerY + innerH + 3, z, background, background);
		fillGradient(modelMatrix, bufferBuilder, innerX - 3, innerY - 3 + 1, innerX - 3 + 1, innerY + innerH + 3 - 1, z,
				accent, main);
		fillGradient(modelMatrix, bufferBuilder, innerX + innerW + 2, innerY - 3 + 1, innerX + innerW + 3,
				innerY + innerH + 3 - 1, z, accent, main);
		fillGradient(modelMatrix, bufferBuilder, innerX - 3, innerY - 3, innerX + innerW + 3, innerY - 3 + 1, z, accent,
				accent);
		fillGradient(modelMatrix, bufferBuilder, innerX - 3, innerY + innerH + 2, innerX + innerW + 3,
				innerY + innerH + 3, z, main, main);

		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		matrices.pop();
	}

	public static void drawLines(TextRenderer textRenderer, MatrixStack matrices, int startX, int startY, int z,
			List<? extends OrderedText> lines) {
		matrices.push();
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider
				.immediate(Tessellator.getInstance().getBuffer());
		Matrix4f modelMatrix = matrices.peek().getModel();
		matrices.translate(0.0D, 0.0D, (double) z);

		int lineX = startX;
		int lineY = startY;

		for (int s = 0; s < lines.size(); ++s) {
			OrderedText line = (OrderedText) lines.get(s);
			if (line != null) {
				textRenderer.draw((OrderedText) line, (float) lineX, (float) lineY, 0xffffffff, true, modelMatrix,
						immediate, false, 0, 0xf000f0);
			}

			if(s == 0)
				lineY += 2;
			
			lineY += 10;
		}
		matrices.pop();
		immediate.draw();
	}

	public static int getWidth(TextRenderer textRenderer, List<? extends OrderedText> lines) {
		int width = lines.stream().mapToInt(text -> (textRenderer.getWidth(text) + 8)).reduce(Math::max).orElse(0);
		return width + 24;
	}

	public static int getHeight(TextRenderer textRenderer, List<? extends OrderedText> lines) {
		int height = 20;
		if (lines.size() > 1) {
			height += (lines.size() - 1) * 10;
		}
		return Math.max(height, 24);
	}

	private static int getPrimaryColor(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}

	private static int getSecondaryColor(int r, int g, int b) {
		return (r / 2) << 16 | (g / 2) << 8 | (b / 2);
	}

	private static int getBackgroundColor(int r, int g, int b) {
		int rr = r == min(r, g, b) ? 0x00 : 0x10;
		int gg = g == min(r, g, b) ? 0x00 : 0x10;
		int bb = b == min(r, g, b) ? 0x00 : 0x10;
		return rr << 16 | gg << 8 | bb;
	}

	private static int min(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	private static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd,
			int yEnd, int z, int colorStart, int colorEnd) {
		float startA = (float) (colorStart >> 24 & 0xff) / 255.0f;
		float startR = (float) (colorStart >> 16 & 0xff) / 255.0f;
		float startG = (float) (colorStart >> 8 & 0xff) / 255.0f;
		float startB = (float) (colorStart & 0xff) / 255.0f;
		float endA = (float) (colorEnd >> 24 & 0xff) / 255.0f;
		float endR = (float) (colorEnd >> 16 & 0xff) / 255.0f;
		float endG = (float) (colorEnd >> 8 & 0xff) / 255.0f;
		float endB = (float) (colorEnd & 0xff) / 255.0f;
		bufferBuilder.vertex(matrix, (float) xEnd, (float) yStart, (float) z).color(startR, startG, startB, startA)
				.next();
		bufferBuilder.vertex(matrix, (float) xStart, (float) yStart, (float) z).color(startR, startG, startB, startA)
				.next();
		bufferBuilder.vertex(matrix, (float) xStart, (float) yEnd, (float) z).color(endR, endG, endB, endA).next();
		bufferBuilder.vertex(matrix, (float) xEnd, (float) yEnd, (float) z).color(endR, endG, endB, endA).next();
	}

}
