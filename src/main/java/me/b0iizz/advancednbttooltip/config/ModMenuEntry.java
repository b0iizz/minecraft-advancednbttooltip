package me.b0iizz.advancednbttooltip.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;

public class ModMenuEntry implements ModMenuApi {
	
	/**
	 * @return This mod's modid.
	 */
	@Override
	public String getModId() {
		return "advancednbttooltip";
	}
	/**
	 * @return This mod's config's {@link ConfigScreenFactory} 
	 */
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
		};
	
	}
}
