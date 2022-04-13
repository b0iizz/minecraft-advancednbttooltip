package me.b0iizz.advancednbttooltip.api.impl.builtin;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.Suggested;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.gui.component.ItemTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@TooltipCode("render_item")
public class ItemRendererFactory implements TooltipFactory {

	@Required
	public TooltipFactory items;

	@Suggested
	public int width = 0;
	
	@Suggested
	public float scale = 1.0f;
	
	@Override
	public List<Text> getTooltipText(Item item, NbtCompound tag, TooltipContext context) {
		return Collections.emptyList();
	}

	@Override
	public List<TooltipComponent> getTooltip(Item item, NbtCompound tag, TooltipContext context) {
		List<ItemStack> list = items.getTooltipText(item, tag, context).stream().map(Text::asString)
				.map(Identifier::new).map(Registry.ITEM::get).filter(Objects::nonNull).distinct().map(ItemStack::new).toList();
//		DefaultedList<ItemStack> dlist = DefaultedList.copyOf(ItemStack.EMPTY, list.toArray(ItemStack[]::new));
//		return Collections.singletonList(new BundleTooltipComponent(new BundleTooltipData(dlist, 65)));
		return Collections.singletonList(new ItemTooltipComponent(list.toArray(ItemStack[]::new), width, scale));
	}

}
