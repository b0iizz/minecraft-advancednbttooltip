package me.b0iizz.advancednbttooltip.api.impl.builtin;

import java.util.List;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.util.NbtPath;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

/**
 * A factory which parses the {@link Text Texts} serialized at the specified
 * {@link NbtPath}.
 * 
 * @author B0IIZZ
 */
@TooltipCode("nbt_text")
public class NbtTextFactory implements TooltipFactory {

	/**
	 * The {@link NbtPath} to search
	 */
	@Required("tag")
	public TooltipFactory path;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return path.getTooltipText(item, tag, context).stream().<Text>flatMap(path -> {
			return NbtPath.of(path.asString()).getAll(tag).stream().map(NbtElement::asString).map(Text.Serializer::fromJson);
		}).toList();
	}

}
