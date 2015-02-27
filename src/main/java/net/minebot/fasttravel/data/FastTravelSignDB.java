/*
 * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *
 * Copyright (c) 2011-2015 craftycreeper, minebot.net, oneill011990
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.minebot.fasttravel.data;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class FastTravelSignDB {

	private static FastTravelSignsPlugin plugin;

	private static Map<String, FastTravelSign> signs;

	private static String saveFile;

    public static void init(FastTravelSignsPlugin plugin, String saveFile, boolean load) {
		FastTravelSignDB.plugin = plugin;
		FastTravelSignDB.saveFile = saveFile;

		signs = new HashMap<>();

        if (load)
		    load();
	}

    public static void init(FastTravelSignsPlugin plugin, boolean load){
        FastTravelSignDB.plugin = plugin;

        signs = new HashMap<>();

        if (load)
            load();
    }

    public static void load(){

        if (FastTravelSignsPlugin.getDBHandler() == DBType.File){
            FileDBHandler.load(saveFile);
        }else if (FastTravelSignsPlugin.getDBHandler() == DBType.SQLite) {
            try {
                SQLDBHandler.load();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void save(){

        if (FastTravelSignsPlugin.getDBHandler() == DBType.File){
            FileDBHandler.save(plugin.getDataDir() + "/signs.yml");
        } else if (FastTravelSignsPlugin.getDBHandler() == DBType.SQLite) {
            SQLDBHandler.save();
        }

    }

	public static void removeSign(String name) {
		if (signs.containsKey(name.toLowerCase()))
			signs.remove(name.toLowerCase());

        if (FastTravelSignsPlugin.getDBHandler() == DBType.File){
            save();
        } else if (FastTravelSignsPlugin.getDBHandler() == DBType.SQLite){
            Database.getDatabaseBySystem(DBType.SQLite).update("DELETE * FROM FastTravelSigns WHERE name = '" + name + "';");
        }
	}

	public static FastTravelSign getSign(String name) {
		if (signs.containsKey(name.toLowerCase()))
			return signs.get(name.toLowerCase());
		else
			return null;
	}

	public static List<FastTravelSign> getSignsFor(Player player) {
		List<FastTravelSign> playerSigns = new ArrayList<>();
		for (FastTravelSign sign : signs.values()) {
			if (sign.isAutomatic() || sign.foundBy(player.getUniqueId()))
				playerSigns.add(sign);
		}
		Collections.sort(playerSigns);
		return playerSigns;
	}

	public static List<FastTravelSign> getAllSigns() {
		List<FastTravelSign> allSigns = new ArrayList<>();
		allSigns.addAll(signs.values());
		Collections.sort(allSigns);
		return allSigns;
	}

    public static Map<String, FastTravelSign> getSignMap(){
        return signs;
    }

	public static void addSign(FastTravelSign sign) {
		if (!signs.containsKey(sign.getName().toLowerCase()))
			signs.put(sign.getName().toLowerCase(), sign);
		save();
	}
}
