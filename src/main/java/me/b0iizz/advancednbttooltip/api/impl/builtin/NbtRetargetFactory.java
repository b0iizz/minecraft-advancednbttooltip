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

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.util.NbtPath;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

/**
 * A factory which uses the child factory on every {@link NbtElement} at a
 * {@link NbtPath}
 * 
 * @author B0IIZZ
 */
@TooltipCode("nbt_retarget")
public class NbtRetargetFactory implements TooltipFactory {

	/**
	 * The {@link NbtPath} to search
	 */
	@Required("tag")
	public TooltipFactory path;

	/**
	 * The {@link TooltipFactory} to use
	 */
	@Required
	public TooltipFactory text;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return path.getTooltipText(item, tag, context).stream()
				.flatMap(path -> NbtPathWrapper.getAll(path.asString(), tag).stream()
						.filter(t -> t.getType() == NbtType.COMPOUND)
						.map(t -> (NbtCompound) t).flatMap(t -> this.text.getTooltipText(item, t, context).stream()))
				.toList();
	}

	@Override
	public List<TooltipComponent> getTooltip(Item item, NbtCompound tag, TooltipContext context) {
		return path.getTooltipText(item, tag, context).stream()
				.flatMap(path -> NbtPathWrapper.getAll(path.asString(), tag).stream()
						.filter(t -> t.getType() == NbtType.COMPOUND)
						.map(t -> (NbtCompound) t).flatMap(t -> this.text.getTooltip(item, t, context).stream()))
				.toList();
	}

}
