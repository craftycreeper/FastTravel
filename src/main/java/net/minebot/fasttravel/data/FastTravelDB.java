/*
 *
 *  * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *  *
 *  * Copyright (c) 2011-2014 craftycreeper, minebot.net, oneill011990
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  * this software and associated documentation files (the "Software"), to deal in
 *  * the Software without restriction, including without limitation the rights to
 *  * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  * of the Software, and to permit persons to whom the Software is furnished to do
 *  * so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package net.minebot.fasttravel.data;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FastTravelDB {

	private static FastTravelSignsPlugin plugin;

	private static Map<String, FastTravelSign> signs;

	private static String saveFile;

	public static void init(FastTravelSignsPlugin plugin, String saveFile) {
		FastTravelDB.plugin = plugin;
		FastTravelDB.saveFile = saveFile;

		signs = new HashMap<String, FastTravelSign>();

		load();
	}

	private static void load() {
		YamlConfiguration signYAML = new YamlConfiguration();
		try {
			signYAML.load(saveFile);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		for (String signName : signYAML.getKeys(false)) {
			String creator = signYAML.getString(signName + ".creator");
			World locWorld = plugin.getServer().getWorld(
					signYAML.getString(signName + ".signloc.world"));
			World tpLocWorld = plugin.getServer().getWorld(
					signYAML.getString(signName + ".tploc.world"));
			List<String> signPlayers = signYAML.getStringList(signName + ".players");

			if (creator == null || locWorld == null || tpLocWorld == null) {
				plugin.getLogger()
						.warning("Could not load sign '" + signName + "' - missing data!");
				continue;
			}

			double price = signYAML.getDouble(signName + ".price", 0.0);

			Location location = new Location(locWorld, signYAML.getDouble(signName + ".signloc.x"),
					signYAML.getDouble(signName + ".signloc.y"), signYAML.getDouble(signName
							+ ".signloc.z"));
			location.setYaw((float) signYAML.getDouble(signName + ".signloc.yaw"));

			Location tploc = new Location(locWorld, signYAML.getDouble(signName + ".tploc.x"),
					signYAML.getDouble(signName + ".tploc.y"), signYAML.getDouble(signName
							+ ".tploc.z"));
			tploc.setYaw((float) signYAML.getDouble(signName + ".tploc.yaw"));
			
			boolean automatic = signYAML.getBoolean(signName + ".automatic", false);

			signs.put(signName.toLowerCase(), new FastTravelSign(signName, creator, price, location, tploc,
					automatic, signPlayers));
		}

		plugin.getLogger().info("Loaded " + signs.size() + " fast travel signs.");

	}

	public static void save() {
		YamlConfiguration signYAML = new YamlConfiguration();
		for (String signName : signs.keySet()) {
			FastTravelSign sign = signs.get(signName);
			signName = sign.getName();
			signYAML.set(signName + ".creator", sign.getCreator());
			signYAML.set(signName + ".signloc.world", sign.getSignLocation().getWorld().getName());
			signYAML.set(signName + ".signloc.x", sign.getSignLocation().getX());
			signYAML.set(signName + ".signloc.y", sign.getSignLocation().getY());
			signYAML.set(signName + ".signloc.z", sign.getSignLocation().getZ());
			signYAML.set(signName + ".signloc.yaw", (double) sign.getSignLocation().getYaw());
			signYAML.set(signName + ".tploc.world", sign.getTPLocation().getWorld().getName());
			signYAML.set(signName + ".tploc.x", sign.getTPLocation().getX());
			signYAML.set(signName + ".tploc.y", sign.getTPLocation().getY());
			signYAML.set(signName + ".tploc.z", sign.getTPLocation().getZ());
			signYAML.set(signName + ".tploc.yaw", (double) sign.getTPLocation().getYaw());
			signYAML.set(signName + ".automatic", sign.isAutomatic());
			signYAML.set(signName + ".players", sign.getPlayers());
			signYAML.set(signName + ".price", sign.getPrice());
		}

		try {
			signYAML.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeSign(String name) {
		if (signs.containsKey(name.toLowerCase()))
			signs.remove(name.toLowerCase());
		save();
	}

	public static FastTravelSign getSign(String name) {
		if (signs.containsKey(name.toLowerCase()))
			return signs.get(name.toLowerCase());
		else
			return null;
	}

	public static List<FastTravelSign> getSignsFor(String player) {
		List<FastTravelSign> playerSigns = new ArrayList<FastTravelSign>();
		for (FastTravelSign sign : signs.values()) {
			if (sign.isAutomatic() || sign.foundBy(player))
				playerSigns.add(sign);
		}
		Collections.sort(playerSigns);
		return playerSigns;
	}

	public static List<FastTravelSign> getAllSigns() {
		List<FastTravelSign> allSigns = new ArrayList<FastTravelSign>();
		allSigns.addAll(signs.values());
		Collections.sort(allSigns);
		return allSigns;
	}

	public static void addSign(FastTravelSign sign) {
		if (!signs.containsKey(sign.getName().toLowerCase()))
			signs.put(sign.getName().toLowerCase(), sign);
		save();
	}

	// Load/save
	/*
	 * public void load() { try { File savefile = new File(savePath);
	 * ObjectInputStream ois = new ObjectInputStream(new
	 * FileInputStream(savefile)); Object readobj = ois.readObject();
	 * ois.close();
	 * 
	 * FastTravelDBSave saveData = (FastTravelDBSave)readobj; this.signs =
	 * saveData.signs; this.userSigns = saveData.userSigns; } catch(Exception
	 * e){ e.printStackTrace(); } }
	 * 
	 * public void save() { FastTravelDBSave saveData = new
	 * FastTravelDBSave(signs, userSigns); try { File savefile = new
	 * File(savePath); ObjectOutputStream oos = new ObjectOutputStream(new
	 * FileOutputStream(savefile)); oos.writeObject(saveData); oos.flush();
	 * oos.close(); } catch(Exception e){ e.printStackTrace(); } }
	 */

}
