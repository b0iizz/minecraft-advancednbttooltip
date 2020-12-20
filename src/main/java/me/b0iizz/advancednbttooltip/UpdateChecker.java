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

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

/**
 * A class which can be called to find if new updates are available.
 * 
 * @author B0IIZZ
 */
public final class UpdateChecker {

	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 *	The URL to the file which contains the version of the newest release
	 */
	public static final String UPDATE_URL = "https://www.dropbox.com/s/3o3hbhr94ln8ybh/versions.txt?dl=1";

	
	/**
	 * The Text which will be displayed in the Title menu
	 */
	public static final String UPDATE_TEXT = "A new version of AdvancedNbtTooltips is available!";
	
	private static boolean isLatest = true;
	private static boolean hasCheckedUpdates = false;

	/**
	 * The main interaction point of the class. Used to find whether the mod is up to date.
	 * @return true if the current version of the mod is the latest release
	 */
	public static boolean isLatest() {
		if (!hasCheckedUpdates)
			checkUpdates();
		return isLatest;
	}

	
	/**
	 * Rechecks if the mod is up to date.
	 */
	public static void refreshUpdates() {
		hasCheckedUpdates = false;
		checkUpdates();
	}

	private static void checkUpdates() {
		hasCheckedUpdates = true;
		try {
			URL update = new URL(UPDATE_URL);
			URLConnection connection = update.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				String[] split = inputLine.split(":");
				if(split[0].equals(SharedConstants.getGameVersion().getReleaseTarget()) && split[1].equals(ModMain.modid)) {
					String version = FabricLoader.getInstance().getModContainer(ModMain.modid).get().getMetadata().getVersion().getFriendlyString();
					version = version.substring(0, version.contains("+") ? version.indexOf('+') : version.length());
					String[] versionsplit = version.split("\\.");
					for(int i = 0; i < 3; i++) {
						if(Integer.parseInt(versionsplit[i]) < Integer.parseInt(split[i+2])) {
							isLatest = false;
						}
					}
					
				}
			}	
			in.close();
		} catch (Exception e) {
			if(e instanceof NumberFormatException)
				isLatest = false;
			LOGGER.info("(AdvancedNbtTooltip) Error in update checker! Ignore this in a development environment!");
		}
	}
}
