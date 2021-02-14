package me.b0iizz.advancednbttooltip.tooltip.hud;

import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

final class TooltipRenderingUtils {

	public static void drawTooltip(TextRenderer textRenderer, MatrixStack matrices, List<? extends OrderedText> lines,
			int x, int y, int width, int height, int color) {
		if (!lines.isEmpty()) {
			int tooltipWidth = getWidth(textRenderer, lines);

			int tooltipX = x;
			int tooltipY = y;
			int tooltipHeight = getHeight(textRenderer, lines);

			if (tooltipX + tooltipWidth > width) {
				tooltipX -= 28 + tooltipWidth;
			}

			if (tooltipY + tooltipHeight + 6 > height) {
				tooltipY = height - tooltipHeight - 6;
			}

			matrices.push();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getModel();

			int alpha = 0x50;
			int backgroundAlpha = 0xf0;
			
			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = (color >> 0) & 0xff;
			
			int primary = alpha << 24 | getPrimaryColor(r, g, b);
			int secondary = alpha << 24 | getSecondaryColor(r, g, b);
			int background = backgroundAlpha << 24 | getBackgroundColor(r, g, b);

			int z = 400;
			
			fillGradient(matrix4f, bufferBuilder, tooltipX - 3, tooltipY - 4, tooltipX + tooltipWidth + 3, tooltipY - 3,
					z, background, background);
			fillGradient(matrix4f, bufferBuilder, tooltipX - 3, tooltipY + tooltipHeight + 3,
					tooltipX + tooltipWidth + 3, tooltipY + tooltipHeight + 4, z, background, background);
			fillGradient(matrix4f, bufferBuilder, tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3,
					tooltipY + tooltipHeight + 3, z, background, background);
			fillGradient(matrix4f, bufferBuilder, tooltipX - 4, tooltipY - 3, tooltipX - 3,
					tooltipY + tooltipHeight + 3, z, background, background);
			fillGradient(matrix4f, bufferBuilder, tooltipX + tooltipWidth + 3, tooltipY - 3,
					tooltipX + tooltipWidth + 4, tooltipY + tooltipHeight + 3, z, background, background);
			fillGradient(matrix4f, bufferBuilder, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1,
					tooltipY + tooltipHeight + 3 - 1, z, primary, secondary);
			fillGradient(matrix4f, bufferBuilder, tooltipX + tooltipWidth + 2, tooltipY - 3 + 1,
					tooltipX + tooltipWidth + 3, tooltipY + tooltipHeight + 3 - 1, z, primary, secondary);
			fillGradient(matrix4f, bufferBuilder, tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3,
					tooltipY - 3 + 1, z, primary, primary);
			fillGradient(matrix4f, bufferBuilder, tooltipX - 3, tooltipY + tooltipHeight + 2,
					tooltipX + tooltipWidth + 3, tooltipY + tooltipHeight + 3, z, secondary, secondary);
			
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
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider
					.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0D, 0.0D, 400.0D);

			for (int s = 0; s < lines.size(); ++s) {
				OrderedText line = (OrderedText) lines.get(s);
				if (line != null) {
					textRenderer.draw((OrderedText) line, (float) tooltipX, (float) tooltipY, 0xffffffff, true, matrix4f,
							immediate, false, 0, 0xf000f0);
				}

				if (s == 0) {
					tooltipY += 2;
				}

				tooltipY += 10;
			}

			immediate.draw();
			matrices.pop();
		}
	}

	public static int getWidth(TextRenderer textRenderer, List<? extends OrderedText> lines) {
		int tooltipWidth = 0;
		Iterator<? extends OrderedText> lineIterator = lines.iterator();

		while (lineIterator.hasNext()) {
			OrderedText orderedText = (OrderedText) lineIterator.next();
			int textWidth = textRenderer.getWidth(orderedText);
			if (textWidth > tooltipWidth) {
				tooltipWidth = textWidth;
			}
		}
		return tooltipWidth;
	}
	
	public static int getHeight(TextRenderer textRenderer, List<? extends OrderedText> lines) {
		int tooltipHeight = 8;
		if (lines.size() > 1) {
			tooltipHeight += 2 + (lines.size() - 1) * 10;
		}
		return tooltipHeight;
	}
	
	private static int getPrimaryColor(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}
	
	private static int getSecondaryColor(int r, int g, int b) {
		return (r/2) << 16 | (g/2) << 8 | (b/2);
	}
	
	private static int getBackgroundColor(int r, int g, int b) {
		int rr = r == min(r,g,b) ? 0x00 : 0x10;
		int gg = g == min(r,g,b) ? 0x00 : 0x10;
		int bb = b == min(r,g,b) ? 0x00 : 0x10;
		return rr << 16 | gg << 8 | bb;
	}
	
	private static int min(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
	
	private static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd,
			int yEnd, int z, int colorStart, int colorEnd) {
		float f = (float) (colorStart >> 24 & 255) / 255.0F;
		float g = (float) (colorStart >> 16 & 255) / 255.0F;
		float h = (float) (colorStart >> 8 & 255) / 255.0F;
		float i = (float) (colorStart & 255) / 255.0F;
		float j = (float) (colorEnd >> 24 & 255) / 255.0F;
		float k = (float) (colorEnd >> 16 & 255) / 255.0F;
		float l = (float) (colorEnd >> 8 & 255) / 255.0F;
		float m = (float) (colorEnd & 255) / 255.0F;
		bufferBuilder.vertex(matrix, (float) xEnd, (float) yStart, (float) z).color(g, h, i, f).next();
		bufferBuilder.vertex(matrix, (float) xStart, (float) yStart, (float) z).color(g, h, i, f).next();
		bufferBuilder.vertex(matrix, (float) xStart, (float) yEnd, (float) z).color(k, l, m, j).next();
		bufferBuilder.vertex(matrix, (float) xEnd, (float) yEnd, (float) z).color(k, l, m, j).next();
	}

}
