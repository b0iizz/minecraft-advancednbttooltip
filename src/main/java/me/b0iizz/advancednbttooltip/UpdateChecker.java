/*	MIT License
	
	Copyright (c) 2020 b0iizz
	
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
package me.b0iizz.advancednbttooltip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.b0iizz.advancednbttooltip.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.util.version.VersionDeserializer;
import net.minecraft.SharedConstants;

/**
 * A class which can be called to find if new updates are available.
 * 
 * @author B0IIZZ
 */
public final class UpdateChecker {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The URL to the file which contains the version of the newest release
	 */
	public static final String UPDATE_URL = "https://www.dropbox.com/s/3o3hbhr94ln8ybh/versions.txt?dl=1";

	/**
	 * The default Text which will be displayed in the Title menu
	 */
	public static final String UPDATE_TEXT = "A new version of AdvancedNbtTooltips is available!";
	
	/**
	 * The Text which will be displayed in the Title menu when an update is mandatory due to a bug
	 */
	public static final String MANDATORY_UPDATE_TEXT = "This version of the mod has a critical error! Please update as soon as possible!";

	private static boolean isLatest = true;
	private static boolean isCritical = false;
	
	private static boolean hasCheckedUpdates = false;

	/**
	 * The main interaction point of the class. Used to find whether the mod is up
	 * to date.
	 * 
	 * @return true if the current version of the mod is the latest release
	 */
	public static boolean isLatest() {
		if (!hasCheckedUpdates)
			checkUpdates();
		return isLatest;
	}

	/**
	 * Used to find whether a critical error has been fixed in the newer version
	 * 
	 * @return true if the latest version of the mod fixes a critical error
	 */
	public static boolean isCriticalErrorFixed() {
		if(!hasCheckedUpdates)
			checkUpdates();
		return isCritical;
	}
	
	/**
	 * Rechecks if the mod is up to date.
	 */
	public static void refreshUpdates() {
		if(!ConfigManager.getMainMenuUpdateNoticeToggle()) return;
		hasCheckedUpdates = false;
		checkUpdates();
	}

	private static void checkUpdates() {
		new Thread(() -> {
			hasCheckedUpdates = true;
			try {
				String currentMinecraftReleaseTarget = SharedConstants.getGameVersion().getReleaseTarget();
				
				SemanticVersion currentPatchVersion = VersionDeserializer.deserializeSemantic(FabricLoader.getInstance().getModContainer(ModMain.modid).get().getMetadata()
						.getVersion().getFriendlyString().split("\\+")[0]);

				URL update = new URL(UPDATE_URL);
				URLConnection connection = update.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					String[] attributes = inputLine.split(":");
					String releaseTarget = attributes[0];
					String modid = attributes[1];
					SemanticVersion newestPatchVersion = VersionDeserializer.deserializeSemantic(attributes[2]);
					boolean criticalError = attributes.length > 3 ? attributes[3].equals("true") : false;
					
					if(currentMinecraftReleaseTarget.equals(releaseTarget) && modid.equals(ModMain.modid) && newestPatchVersion.compareTo(currentPatchVersion) > 0) {
						isLatest = false;
						isCritical = criticalError;
					}
				}
				in.close();
			} catch (Exception e) {
				if (e instanceof NumberFormatException || e instanceof VersionParsingException)
					isLatest = false;
				LOGGER.info(" Error in update checker! Ignore this in a development environment! {}: {}", e.getClass().getCanonicalName(), e.getMessage());
			}
		},"adv-nbt-tool-update").start();
	}

	/**
	 * @return The current appropriate update Text
	 */
	public static String getUpdateText() {
		return isCriticalErrorFixed() ? MANDATORY_UPDATE_TEXT : UPDATE_TEXT;
	}
}
