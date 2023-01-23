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

import com.google.common.collect.ImmutableList;
import me.b0iizz.advancednbttooltip.AdvancedNBTTooltips;
import me.b0iizz.advancednbttooltip.api.CustomTooltip;
import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A Screen where all Tooltips are shown and can be toggled
 *
 * @author B0IIZZ
 */
public class TooltipsScreen extends Screen {

	private TooltipListWidget tooltipList;

	/**
	 *
	 */
	public TooltipsScreen() {
		super(Text.translatable("text." + AdvancedNBTTooltips.modid + ".tooltips.title"));
	}

	@Override
	protected void init() {
		ConfigManager.readToggles();
		initWidgets();
	}

	/**
	 * Initializes all widgets and buttons
	 */
	public void initWidgets() {
		this.addDrawableChild(
			new ButtonWidget.Builder(Text.translatable("menu.returnToGame"), widget -> {
				save();
				this.client.setScreen(null);
				this.client.mouse.lockCursor();
			})
					.dimensions(width / 3, this.height - 27, this.width / 3, 20)
					.build());

		this.addDrawableChild(
			new ButtonWidget.Builder(Text.translatable("text.autoconfig.advancednbttooltip.title"), widget -> {
				save();
				this.client.setScreen(ConfigManager.getConfigScreen(this).get());
			})
					.dimensions(width * 9 / 12, this.height - 27, this.width / 6, 20)
					.build());

		this.tooltipList = this.addDrawableChild(new TooltipListWidget(this.client, this, this.width, this.height, 40, this.height - 48, 20));
	}

	@Override
	public void close() {
		super.close();
		save();
	}


	private void save() {
		ConfigManager.writeToggles();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

		this.renderBackground(matrices);

		this.tooltipList.render(matrices, mouseX, mouseY, delta);

		drawCenteredText(matrices, textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

		super.render(matrices, mouseX, mouseY, delta);
	}

	private static class TooltipListWidget extends ElementListWidget<TooltipListWidget.Entry> {

		final Screen screen;

		public TooltipListWidget(MinecraftClient minecraftClient, Screen screen, int width, int height, int top, int bottom, int itemHeight) {
			super(minecraftClient, width, height, top, bottom, itemHeight);
			this.screen = screen;
			AdvancedNBTTooltips.getRegisteredTooltips().stream()
					.sorted(Comparator.comparing(a -> a.getKey().toString()))
					.map(e -> new Entry(this, e.getKey(), e.getValue())).forEachOrdered(this::addEntry);
			this.setRenderBackground(false);
			this.setRenderHorizontalShadows(false);

		}

		public static class Entry extends ElementListWidget.Entry<Entry> {

			private static final int MAX_NAME_LENGTH = 35;

			final TooltipListWidget widget;
			final Text displayName;
			final Text tooltip;
			final ButtonWidget toggleButton;

			public Entry(TooltipListWidget parent, Identifier id, CustomTooltip tooltip) {
				this.widget = parent;
				this.displayName = createDisplayName(id);
				this.tooltip = createTooltip(id);
				this.toggleButton = new ButtonWidget.Builder(getText(ConfigManager.isEnabled(id)), button -> {
					button.setMessage(getText(ConfigManager.toggle(id)));
				})
						.dimensions(0, 0, 35, 20)
						.build();
			}

			private Text createDisplayName(Identifier id) {
				if (isOfMod(id))
					return Text.translatable("text.advancednbttooltip.toggle." + id.getPath().split("/")[1]);
				return isNameShortened(id) ? Text.literal(id.toString()
						.substring(0, MAX_NAME_LENGTH) + "...") : Text.literal(id.toString());
			}

			private Text createTooltip(Identifier id) {
				if (isOfMod(id)) return Text.translatable("text.advancednbttooltip.toggle." + id.getPath()
						.split("/")[1] + ".tooltip");
				return isNameShortened(id) ? Text.literal(id.toString()) : null;
			}

			private boolean isOfMod(Identifier id) {
				return id.getNamespace().equals(AdvancedNBTTooltips.modid);
			}

			private boolean isNameShortened(Identifier id) {
				return id.toString().length() > MAX_NAME_LENGTH;
			}

			private Text getText(boolean toggle) {
				return ScreenTexts.onOrOff(toggle).copy()
						.formatted(toggle ? Formatting.GREEN : Formatting.DARK_RED, Formatting.BOLD);
			}

			@Override
			public List<? extends Element> children() {
				return ImmutableList.of(this.toggleButton);
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

				widget.client.textRenderer.draw(matrices, displayName, x, y + 5, 0xFFFFFF);

				this.toggleButton.setX(x + 190);
				this.toggleButton.setY(y);

				this.toggleButton.render(matrices, mouseX, mouseY, tickDelta);

				if (hovered && this.tooltip != null) {
					widget.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
				}
			}

			@Override
			public List<? extends Selectable> selectableChildren() {
				return Collections.singletonList(toggleButton);
			}

		}

	}
}
