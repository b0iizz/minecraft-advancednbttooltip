package me.b0iizz.advancednbttooltip.api.impl.builtin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

@TooltipCode("item_renderer")
public class ItemRendererFactory implements TooltipFactory {

	@Required
	public TooltipFactory items;

	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return Collections.emptyList();
	}

	@Override
	public List<TooltipComponent> getTooltip(Item item, NbtCompound tag, TooltipContext context) {
		DefaultedList<ItemStack> list = items.getTooltipText(item, tag, context).stream().map(Text::asString)
				.map(Identifier::new).map(Registry.ITEM::get).distinct().map(ItemStack::new)
				.collect(Collectors.toCollection(DefaultedList::of));
		return Collections.singletonList(new BundleTooltipComponent(new BundleTooltipData(list, 0)));
	}

}
