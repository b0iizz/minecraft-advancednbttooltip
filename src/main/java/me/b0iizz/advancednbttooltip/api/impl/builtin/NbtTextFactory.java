package me.b0iizz.advancednbttooltip.api.impl.builtin;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

import java.util.List;

/**
 * A factory which parses the {@link Text Texts} serialized at the specified
 * {@link net.minecraft.command.argument.NbtPathArgumentType.NbtPath}.
 *
 * @author B0IIZZ
 */
@TooltipCode("nbt_text")
public class NbtTextFactory implements TooltipFactory {

	/**
	 * The {@link net.minecraft.command.argument.NbtPathArgumentType.NbtPath} to search
	 */
	@Required("tag")
	public TooltipFactory path;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return path.getTooltipText(item, tag, context).stream()
				.<Text>flatMap(path -> NbtPathWrapper.getAll(path.getString(), tag).stream().map(NbtElement::asString)
						.map(Text.Serializer::fromLenientJson)).toList();
	}

}
