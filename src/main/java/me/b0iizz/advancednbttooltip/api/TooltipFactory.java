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
package me.b0iizz.advancednbttooltip.api;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * An interface used for providing the actual {@link CustomTooltip custom
 * tooltip} text for an {@link Item}. A lambda implementation is recommended.
 *
 * @author B0IIZZ
 */
@FunctionalInterface
public interface TooltipFactory {

	/**
	 * An empty {@link TooltipFactory}, which means that it always returns an empty
	 * list of text
	 */
	TooltipFactory EMPTY = TooltipFactory.of(Collections::emptyList);

	/**
	 * Creates the tooltip text for the Item.
	 *
	 * @param item    The {@link Item} the tooltip will be added to.
	 * @param tag     The Item's {@link NbtCompound NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return A {@link List} of {@link Text Texts} to be applied to the Item's
	 * tooltip.
	 */
	List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context);

	/**
	 * Creates the tooltip components for the Item. This is a generalization for {@link #getTooltipText(Item, NbtCompound, TooltipContext)}.
	 *
	 * @param item    The {@link Item} the tooltip will be added to.
	 * @param tag     The Item's {@link NbtCompound NBT-tag}.
	 * @param context The current {@link TooltipContext}.
	 * @return A {@link List} of {@link TooltipComponent TooltipComponents} to be applied to the Item's
	 * tooltip.
	 */
	default List<TooltipComponent> getTooltip(Item item, NbtCompound tag, TooltipContext context) {
		return this.getTooltipText(item, tag, context).stream().map(Text::asOrderedText).map(TooltipComponent::of)
				.toList();
	}

	/**
	 * Creates a factory for the given {@link Supplier}. This is useful for
	 * factories not relying on the supplied parameters.
	 *
	 * @param supplier The factory used for {@link TooltipFactory#getTooltipText}
	 * @return a {@link TooltipFactory}
	 */
	static TooltipFactory of(Supplier<List<Text>> supplier) {
		return (i, t, c) -> supplier.get();
	}

	/**
	 * Creates a factory for the given {@link String}. This is useful for
	 * factories not relying on the supplied parameters.
	 *
	 * @param text The text used for {@link TooltipFactory#getTooltipText}
	 * @return a {@link TooltipFactory}
	 */
	static TooltipFactory of(String text) {
		return of(() -> Collections.singletonList(Text.of(text)));
	}
}
