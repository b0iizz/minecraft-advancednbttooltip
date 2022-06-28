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
package me.b0iizz.advancednbttooltip.api.impl.builtin;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.Suggested;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A factory which creates a simple {@link LiteralText} containing the value of
 * a specified {@link net.minecraft.command.argument.NbtPathArgumentType.NbtPath}
 *
 * @author B0IIZZ
 */
@TooltipCode("nbt_value")
public class NbtValueFactory implements TooltipFactory {

	/**
	 * The {@link net.minecraft.command.argument.NbtPathArgumentType.NbtPath} to search
	 */
	@Required("tag")
	public TooltipFactory path;
	/**
	 * Whether {@link NbtCompound compounds} should be further inspected
	 */
	@Suggested("go_into_compounds")
	public boolean traverseCompound = false;
	/**
	 * Whether {@link AbstractNbtList lists} should be further inspected
	 */
	@Suggested("go_into_lists")
	public boolean traverseList = false;
	/**
	 * Whether the result should color primitive values yellow.
	 */
	@Suggested
	public boolean colored = false;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return path.getTooltipText(item, tag, context).stream()
				.flatMap(path -> NbtPathWrapper.getAll(path.asString(), tag).stream().map(this::fromTag)
						.flatMap(List::stream)).toList();
	}

	private List<Text> fromTag(NbtElement tag) {
		if (tag instanceof NbtCompound) {
			if (!traverseCompound)
				return Arrays
						.asList(new LiteralText("{...}").formatted(colored ? Formatting.YELLOW : Formatting.RESET));
			return ((NbtCompound) tag).getKeys().stream().flatMap(key -> Stream.concat(
					Stream.of(new LiteralText(key + ": ").formatted(colored ? Formatting.GRAY : Formatting.RESET)),
					fromTag(((NbtCompound) tag).get(key)).stream().map(this::indent))).collect(Collectors.toList());
		} else if (tag instanceof AbstractNbtList) {
			if (!traverseList)
				return Arrays
						.asList(new LiteralText("[...]").formatted(colored ? Formatting.YELLOW : Formatting.RESET));
			return ((AbstractNbtList<NbtElement>) tag).stream()
					.flatMap(e -> Stream.concat(fromTag(e).stream(), Stream.of(new LiteralText("")))).map(this::indent)
					.collect(Collectors.toList());
		} else
			return Arrays
					.asList(new LiteralText(tag.asString()).formatted(colored ? Formatting.YELLOW : Formatting.RESET));
	}

	private MutableText indent(Text text) {
		return new LiteralText(" ").append(text);
	}

}
