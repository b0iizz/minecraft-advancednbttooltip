package me.b0iizz.advancednbttooltip.api.impl.builtin;

import me.b0iizz.advancednbttooltip.api.JsonTooltips.Required;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.Suggested;
import me.b0iizz.advancednbttooltip.api.JsonTooltips.TooltipCode;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.api.TooltipFactory;
import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

@TooltipCode("section_visible")
public class SectionVisibleCondition implements TooltipCondition {

	@Required
	public TooltipFactory sections;

	@Suggested
	public TooltipCondition enable_overrides = TooltipCondition.TRUE;

	@Override
	public boolean isEnabled(Item item, NbtCompound tag, TooltipContext context) {
		int flags = tag.getInt("HideFlags")
				& (enable_overrides.isEnabled(item, tag, context) ? ConfigManager.getHideflagOverrideBitmask() : 0x7f);
		return sections.getTooltipText(item, tag, context).stream().map(Text::getString).map(String::toUpperCase)
				.map(TooltipSection::valueOf).allMatch(section -> (flags & section.getFlag()) != 0);
	}

}
