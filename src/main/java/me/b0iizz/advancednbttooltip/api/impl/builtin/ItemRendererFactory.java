package me.b0iizz.advancednbttooltip.api.impl.builtin;

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
import net.minecraft.registry.Registries;

import java.util.Collections;
import java.util.List;

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
		List<ItemStack> list = items.getTooltipText(item, tag, context).stream().map(Text::getString)
				.map(Identifier::new).map(Registries.ITEM::get).distinct().map(ItemStack::new)
				.toList();
		return Collections.singletonList(new ItemTooltipComponent(list.toArray(ItemStack[]::new), width, scale));
	}

}
