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

package net.minebot.fasttravel;

import net.milkbowl.vault.economy.Economy;
import net.minebot.fasttravel.commands.*;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import net.minebot.fasttravel.listeners.*;
import net.minebot.fasttravel.menu.TravelMenu;
import net.minebot.fasttravel.task.FastTravelTaskExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FastTravelSignsPlugin extends JavaPlugin {

	private static FastTravelSignsPlugin instance;

	public static File dataDir = new File("plugins/FastTravelSigns");

	private Economy economy = null;

    private static Configuration config;

	private UpdateChecker updateChecker;

	public boolean needUpdate;
	public String newVersion;


	// Players in transit - put here for now. Should find a better place later.
	public ArrayList<UUID> playersWarmingUp;

    //Players and signs for editing.
    public HashMap<Player, FastTravelSign> editors = new HashMap<>();

	//Menus that have been created
	public ArrayList<TravelMenu> menus;

    public void onEnable() {
		// If folder does not exist, create it
		if (!dataDir.isDirectory()) {
			dataDir.mkdir();
		}

		instance = this;

		// Load config and etc
		dataInit();
        FastTravelTaskExecutor.init();
        metricsInit();
        config = getConfig();
		updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/bukkit-plugins/fasttravel/files.rss");

        if (updateChecker.updateFound()){
            getLogger().info("Update found! You are using v" +
                    this.getDescription().getVersion() + ". New version is: v" + updateChecker.getVersion() +
					"\n get it here: " + updateChecker.getLink());
			needUpdate = true;
			newVersion = updateChecker.getLink();
        }

		playersWarmingUp = new ArrayList<UUID>();
		menus = new ArrayList<TravelMenu>();


		// Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FastTravelBlockListener(), this);
		pm.registerEvents(new FastTravelEntityListener(), this);
		pm.registerEvents(new FastTravelSignListener(this), this);
		pm.registerEvents(new FastTravelPlayerListener(this), this);
        pm.registerEvents(new FastTravelListener(this), this);
		pm.registerEvents(new FastTravelInventoryListener(this), this);

		// commands
		getCommand("ft").setExecutor(new FastTravelCommand(this));
		getCommand("ftlist").setExecutor(new FastTravelListCommand(this));
		getCommand("ftprice").setExecutor(new FastTravelPriceCommand(this));
		getCommand("ftdelete").setExecutor(new FastTravelDeleteCommand());
		getCommand("ftsetpoint").setExecutor(new FastTravelSetpointCommand());
		getCommand("ftreload").setExecutor(new FastTravelReloadCommand(this));
		getCommand("ftauto").setExecutor(new FastTravelAutoCommand());
        getCommand("ftclear").setExecutor(new FastTravelClearCommand());
        //not working for now because it uses player names
        //TODO Make it work again
        //getCommand("ftremove").setExecutor(new FastTravelRemoveCommand(this));
        getCommand("ftsetrange").setExecutor(new FastTravelSetRangeCommand());
		getCommand("ftsave").setExecutor(new FastTravelSaveCommand(this));
		getCommand("ftmenu").setExecutor(new FastTravelMenuCommand(this));
        //TODO finish method and fix bugs
        //getCommand("ftmove").setExecutor(new FastTravelMoveCommand(this));

		getLogger().info("Enabled.");
	}

    public void onDisable() {
		FastTravelSignDB.save();

		getLogger().info("Disabled.");
	}

	public void dataInit() {
		// Load config, set defaults
		String confFile = dataDir + "/config.yml";
		try {
			getConfig().load(confFile);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidConfigurationException e) {
		}
		getConfig().addDefault("cooldown", 0);
		getConfig().addDefault("warmup", 0);
        getConfig().addDefault("use range", true);
		getConfig().addDefault("enable menu", true);
		getConfig().addDefault("notify update", true);
		getConfig().addDefault("metrics enabled", true);
		getConfig().addDefault("economy.enabled", false);
		getConfig().addDefault("economy.default-price", 0);
		getConfig().options().copyDefaults(true);
		try {
			getConfig().save(confFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (getConfig().getBoolean("economy.enabled"))
			setupEconomy();
		else
			getLogger().info("Economy support not enabled.");

		// Load signs database
		FastTravelSignDB.init(this, dataDir + "/signs.yml");
	}

	public void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().warning("Could not find Vault! Disabling economy support.");
			return;
		}

		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		if (economy == null) {
			getLogger().warning("Could not find an economy plugin! Disabling economy support.");
			return;
		}
		getLogger().info("Using " + economy.getName() + " for economy support.");
	}

	public void metricsInit(){
		if (getConfig().getBoolean("metrics.enabled")){
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
				// Failed to submit the stats :-(
			}
		}
	}

	public Economy getEconomy() {
		return economy;
	}

    public static Configuration getConfiguration() {
        return config;
    }

	public ArrayList<TravelMenu> getMenus() {
		return menus;
	}

    public HashMap<Player, FastTravelSign> getEditors() {
        return editors;
    }

    public static FastTravelSignsPlugin getInstance() {
		return instance;
	}

}
