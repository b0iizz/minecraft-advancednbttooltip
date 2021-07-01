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

import java.util.List;
import java.util.stream.Collectors;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipIdentifier;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.util.NbtPath;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtNull;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * A factory which creates simple {@link LiteralText} containing the size of all
 * elements at a specified {@link NbtPath}
 * 
 * @author B0IIZZ
 */
@TooltipIdentifier("nbt_size")
public class NbtSizeFactory implements TooltipFactory {

	/**
	 * The {@link NbtPath} to search
	 */
	@Required("tag")
	public String path;
	
	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return NbtPath.of(path).getAll(tag).stream().map(this::fromTag).map(LiteralText::new).collect(Collectors.toList());
	}

	private String fromTag(NbtElement tag) {
		if (tag instanceof NbtCompound) {
			return ((NbtCompound) tag).getSize() + "";
		} else if (tag instanceof AbstractNbtList) {
			return ((AbstractNbtList<?>) tag).size() + "";
		} else
			return tag instanceof NbtNull ? "0" : "1";
	}
}
