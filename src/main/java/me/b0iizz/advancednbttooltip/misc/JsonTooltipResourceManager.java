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

import static me.b0iizz.advancednbttooltip.AdvancedNBTTooltips.id;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonSyntaxException;

import me.b0iizz.advancednbttooltip.api.CustomTooltip;
import me.b0iizz.advancednbttooltip.api.JsonTooltips;
import me.b0iizz.advancednbttooltip.api.TooltipCondition;
import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/**
 * A simple class responsible for notifying the mod that the resources are being
 * reloaded, which should reload all tooltips.
 * 
 * @author B0IIZZ
 *
 */
public class JsonTooltipResourceManager implements SimpleSynchronousResourceReloadListener {

	/**
	 * The length of the searched file suffix
	 */
	public static final int FILE_SUFFIX_LENGTH = ".json".length();

	private static final Logger RES_LOGGER = LogManager.getLogger("AdvancedNbtTooltip Resource Loader");


	final Map<Identifier, CustomTooltip> tooltips;
	
	/**
	 * @param tooltips The map containing all registered Tooltips
	 * 
	 */
	public JsonTooltipResourceManager(Map<Identifier, CustomTooltip> tooltips) {
		this.tooltips = tooltips;
	}

	@Override
	public Identifier getFabricId() {
		return id("resource_listener");
	}

	@Override
	public void reload(ResourceManager manager) {
		tooltips.clear();

		manager.findResources("tooltip", path -> path.endsWith(".json")).forEach(id0 -> {
			String path = id0.getPath();
			Identifier id = new Identifier(id0.getNamespace(), path.substring(0, path.length() - FILE_SUFFIX_LENGTH));

			RES_LOGGER.debug("Trying to load Tooltip {} from {} ", id, id0);

			try (Resource resource = manager.getResource(id0)) {
				try (InputStream is = resource.getInputStream()) {
					try (Reader reader = new BufferedReader(new InputStreamReader(is))) {
						CustomTooltip tooltip = JsonTooltips.getInstance().getGson().fromJson(reader, CustomTooltip.class);
						tooltips.put(id, tooltip);
					}
				}
			} catch (Throwable t) {
				processTooltipError(id, id0, t);
				return;
			}
			RES_LOGGER.debug("Finished loading Tooltip {} from {} ", id, id0);
		});

		tooltips.forEach((id, tooltip) -> tooltip.addCondition(TooltipCondition.of(() -> ConfigManager.isEnabled(id))));

	}

	private void processTooltipError(Identifier id, Identifier resource, Throwable error) {
		String message = "";
		String indent = "\t\t+->";
		Throwable cause = error;
		while(cause instanceof JsonSyntaxException) {
			message += indent + cause.getMessage() + '\n';
			indent = '\t' + indent;
			cause = cause.getCause();
		}
		message = message.replaceAll("\\R$", "");
		RES_LOGGER.warn("Couldn't parse tooltip {} from {} \n {}", id, resource, message, cause);
	}
	
}
