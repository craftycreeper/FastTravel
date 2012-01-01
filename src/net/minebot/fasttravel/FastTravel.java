/*
 * FastTravel - The Exploration and RPG-Friendly Teleportation Plugin
 * 
 * Copyright (c) 2011 craftycreeper, minebot.net
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would
 *    be appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not
 *    be misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source
 *    distribution.
 */

package net.minebot.fasttravel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import net.minebot.fasttravel.commands.FastTravelCommand;
import net.minebot.fasttravel.data.FastTravelDB;
import net.minebot.fasttravel.listeners.*;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Event;

public class FastTravel extends JavaPlugin {

	public static Logger log = Logger.getLogger("Minecraft");
	public static File dataDir = new File("plugins/FastTravel");
	public static FastTravelDB db;
	
	
	public void onEnable() {
		log.info("[FastTravel] Version " + getDescription().getVersion() +
			" enabled.");
		
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
		
		//Load 'database'
		db = new FastTravelDB();
		
		PluginManager pm = getServer().getPluginManager();
		
		//Events
		FastTravelBlockListener bl = new FastTravelBlockListener();
		pm.registerEvent(Event.Type.SIGN_CHANGE, new FastTravelSignListener(),
			Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, bl, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_PHYSICS, bl, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, new FastTravelPlayerListener(),
			Event.Priority.High, this);
		
		//command
		getCommand("ft").setExecutor(new FastTravelCommand(this));
	}

	public void onDisable() {
		log.info("[FastTravel] Disabled.");
	}

}
