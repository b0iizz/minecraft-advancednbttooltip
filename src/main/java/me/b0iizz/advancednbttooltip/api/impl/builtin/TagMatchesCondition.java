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

import com.google.gson.JsonElement;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;

import java.math.BigDecimal;
import java.util.Map.Entry;

/**
 * A condition which is true when the item has the specified {@link NbtElement
 * Tag} at the specified {@link net.minecraft.command.argument.NbtPathArgumentType.NbtPath}
 *
 * @author B0IIZZ
 */
@TooltipCode("tag_matches")
public class TagMatchesCondition implements TooltipCondition {

	/**
	 * The {@link net.minecraft.command.argument.NbtPathArgumentType.NbtPath} to be searched
	 */
	@Required("tag")
	public TooltipFactory path;

	/**
	 * The expected value.
	 */
	@Required
	public JsonElement value;

	@Override
	public boolean isEnabled(Item item, NbtCompound tag, TooltipContext context) {
		return path.getTooltipText(item, tag, context).stream()
				.flatMap(path -> NbtPathWrapper.getAll(path.asString(), tag).stream())
				.anyMatch(e -> isEqualTo(e, value));
	}

	private boolean isEqualTo(NbtElement tag, JsonElement value) {
		if (tag == null && (value == null || value.isJsonNull()))
			return true;
		if (tag == null || (value == null || value.isJsonNull()))
			return false;
		if (tag instanceof AbstractNbtNumber && value.isJsonPrimitive()) {
			try {
				BigDecimal a = new BigDecimal(tag.asString().replaceAll("[A-Za-z]$", ""));
				BigDecimal b = value.getAsBigDecimal();
				return a.compareTo(b) == 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		if (tag instanceof NbtString && value.isJsonPrimitive()) {
			return tag.asString().equals(value.getAsString());
		}
		if (tag instanceof AbstractNbtList && value.isJsonArray()) {
			for (JsonElement json : value.getAsJsonArray())
				if (!((AbstractNbtList<?>) tag).stream().anyMatch(nbt -> isEqualTo(nbt, json)))
					return false;
			return true;
		}
		if (tag instanceof NbtCompound && value.isJsonObject()) {
			NbtCompound compound = (NbtCompound) tag;
			for (Entry<String, JsonElement> json : value.getAsJsonObject().entrySet()) {
				if (!compound.contains(json.getKey()))
					return false;
				if (!isEqualTo(compound.get(json.getKey()), json.getValue()))
					return false;
			}
			return true;
		}
		return false;
	}

}
