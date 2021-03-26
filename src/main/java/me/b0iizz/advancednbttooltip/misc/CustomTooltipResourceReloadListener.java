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
package me.b0iizz.advancednbttooltip.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import static me.b0iizz.advancednbttooltip.AdvancedNBTTooltips.*;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import me.b0iizz.advancednbttooltip.tooltip.api.AbstractCustomTooltip;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * A simple class responsible for notifying the mod that the resources are being
 * reloaded, which should reload all tooltips.
 * 
 * @author B0IIZZ
 *
 */
public class CustomTooltipResourceReloadListener implements SimpleSynchronousResourceReloadListener {

	/**
	 * The length of the searched file suffix
	 */
	public static final int FILE_SUFFIX_LENGTH = ".json".length();

	private static final Logger RES_LOGGER = LogManager.getLogger("AdvancedNbtTooltip Resource Loader");

	private final Gson gson;

	/**
	 * 
	 */
	public CustomTooltipResourceReloadListener() {
		gson = new GsonBuilder().create();
	}

	@Override
	public Identifier getFabricId() {
		return id("resource_listener");
	}

	@Override
	public void apply(ResourceManager manager) {
		TOOLTIPS.clear();

		manager.findResources("tooltip", path -> path.endsWith(".json")).forEach(id0 -> {
			String path = id0.getPath();
			Identifier id = new Identifier(id0.getNamespace(), path.substring(0, path.length() - FILE_SUFFIX_LENGTH));

			RES_LOGGER.info("Loading Tooltip {} from {} ", id, id0);

			try (Resource resource = manager.getResource(id0)) {
				try (InputStream is = resource.getInputStream()) {
					try (Reader reader = new BufferedReader(new InputStreamReader(is))) {
						JsonElement json = JsonHelper.deserialize(this.gson, reader, JsonElement.class);
						if (json != null) {
							if (!json.isJsonObject())
								throw new IllegalStateException(
										"Wrong json element type " + json.getClass().getSimpleName());
							try {
								AbstractCustomTooltip tooltip = AbstractCustomTooltip.LOADER
										.load(json.getAsJsonObject());
								TOOLTIPS.put(id, tooltip);
							} catch (Throwable t) {
								throw new IllegalStateException("Error while loading tooltip.", t);
							}
						} else {
							RES_LOGGER.error("Couldn't load tooltip file {} from {} as it's null or empty");
						}
					}
				}
			} catch (Throwable t) {
				RES_LOGGER.warn("Couldn't parse tooltip {} from {} ", id, id0, t);
			}

		});

		Optional.ofNullable(TOOLTIPS.get(id("tooltip/suspicious_stew")))
				.ifPresent(t -> t.addCondition(ConfigManager::getSuspiciousStewToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/compass")))
		.ifPresent(t -> t.addCondition(ConfigManager::getCompassToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/book")))
		.ifPresent(t -> t.addCondition(ConfigManager::getBookToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/custom_model_data")))
		.ifPresent(t -> t.addCondition(ConfigManager::getCustomModelDataToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/repair_cost")))
		.ifPresent(t -> t.addCondition(ConfigManager::getRepairCostToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/bee_nest")))
		.ifPresent(t -> t.addCondition(ConfigManager::getBeeToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/spawn_egg")))
		.ifPresent(t -> t.addCondition(ConfigManager::getSpawnEggToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/sign")))
		.ifPresent(t -> t.addCondition(ConfigManager::getSignsToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/command_block")))
		.ifPresent(t -> t.addCondition(ConfigManager::getCommandBlocksToggle));
		Optional.ofNullable(TOOLTIPS.get(id("tooltip/hideflags")))
		.ifPresent(t -> t.addCondition(ConfigManager::getHideFlagsToggle));

	}

}
