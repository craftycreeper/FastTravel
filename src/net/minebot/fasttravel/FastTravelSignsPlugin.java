/*
 * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 * 
 * Copyright (c) 2011-2012 craftycreeper, minebot.net
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

package net.minebot.fasttravel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.minebot.fasttravel.commands.FastTravelCommand;
import net.minebot.fasttravel.commands.FastTravelDeleteCommand;
import net.minebot.fasttravel.commands.FastTravelListCommand;
import net.minebot.fasttravel.commands.FastTravelSetpointCommand;
import net.minebot.fasttravel.data.FastTravelDB;
import net.minebot.fasttravel.listeners.*;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.InvalidConfigurationException;

public class FastTravelSignsPlugin extends JavaPlugin {

	public static File dataDir = new File("plugins/FastTravelSigns");
	
	public void onEnable() {
		//If folder does not exist, create it
		if (!dataDir.isDirectory()) {
			dataDir.mkdir();
		}
		
		//Load config, set defaults
		String confFile = dataDir + "/config.yml";
		try {
			getConfig().load(confFile);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidConfigurationException e) {
		}
		getConfig().addDefault("cooldown", 0);
		getConfig().options().copyDefaults(true);
		try {
			getConfig().save(confFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Legacy database? Convert!
		if (new File(dataDir + "/FastTravelSigns.db").exists() &&
				!(new File(dataDir + "/signs.yml").exists())) {
			getLogger().info("Old-style database found. Converting to new YAML format...");
			FastTravelLegacyDBConverter.convert(dataDir + "/signs.yml", dataDir + "/FastTravelSigns.db");
		}
		
		//Load signs database
		FastTravelDB.init(this, dataDir + "/signs.yml");
		
		//Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FastTravelBlockListener(), this);
		pm.registerEvents(new FastTravelEntityListener(), this);
		pm.registerEvents(new FastTravelSignListener(), this);
		pm.registerEvents(new FastTravelPlayerListener(), this);
		
		//commands
		getCommand("ft").setExecutor(new FastTravelCommand(this));
		getCommand("ftlist").setExecutor(new FastTravelListCommand());
		getCommand("ftdelete").setExecutor(new FastTravelDeleteCommand());
		getCommand("ftsetpoint").setExecutor(new FastTravelSetpointCommand());
		
		getLogger().info("Enabled.");
	}

	public void onDisable() {
		//FastTravelDB.save();
		
		getLogger().info("Disabled.");
	}

}
