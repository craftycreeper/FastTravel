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

package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelDB;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.task.FastTravelTask;
import net.minebot.fasttravel.task.FastTravelTaskExecutor;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class FastTravelCommand implements CommandExecutor {

	private HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	private FastTravelSignsPlugin plugin;

    private FastTravelTaskExecutor executor;

	public FastTravelCommand(FastTravelSignsPlugin instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		if (!player.hasPermission("fasttravelsigns.use")) {
			FastTravelUtil.sendFTMessage(player, "You don't have permission to use fast travel.");
			return true;
		}

		if (plugin.playersWarmingUp.contains(player.getName())) {
			FastTravelUtil.sendFTMessage(player, "You are already preparing to travel.");
			return true;
		}

		if (args.length == 0) {
			// Send a list
			FastTravelUtil.sendFTMessage(player, "Your travel points:");
			List<FastTravelSign> usigns = FastTravelDB.getSignsFor(player.getName());
			if (usigns == null || usigns.size() == 0) {
				FastTravelUtil.sendFTMessage(player,
						"None. Find [FastTravel] signs and right click them to activate.");
			} else
				FastTravelUtil.sendFTSignList(player, usigns, (plugin.getEconomy() != null));
		}

		else if (args.length == 1) {
			// Check cooldown
			int cooldownLength = plugin.getConfig().getInt("cooldown");
			long curTime = System.currentTimeMillis() / 1000;
			if (cooldownLength > 0 && !player.hasPermission("fasttravelsigns.overrides.cooldown")) {
				Long cd = cooldowns.get(player.getName());
				if (cd != null && (curTime - cd) < cooldownLength) {
					long timeRemaining = (cooldownLength - (curTime - cd));
					String strRemain = timeRemaining + " seconds";
					if (timeRemaining > 59) {
						int min = (int) (timeRemaining / 60);
						if (min == 1)
							strRemain = "1 minute";
						else
							strRemain = min + " minutes";
					}
					FastTravelUtil.sendFTMessage(player, "You must wait " + strRemain
							+ " before travelling again.");
					return true;
				}
			}

			// Time to travel. Check if the requested sign exists.
			FastTravelSign ftsign = FastTravelDB.getSign(args[0]);

			if (ftsign == null) {
				FastTravelUtil.sendFTMessage(player, "That travel point does not exist.");
				return true;
			}

			boolean allPoints = player.hasPermission("fasttravelsigns.overrides.allpoints");
			if (!(ftsign.isAutomatic() || ftsign.foundBy(player.getName())) && !allPoints) {
				FastTravelUtil.sendFTMessage(player, "You haven't found that travel point yet.");
				return true;
			}
			// Check if world exists
			World targworld = ftsign.getTPLocation().getWorld();
			if (!allPoints && !targworld.equals(player.getWorld())
					&& !player.hasPermission("fasttravelsigns.multiworld")) {
				FastTravelUtil
						.sendFTMessage(player, "You may not fast travel to different worlds.");
				return true;
			}

			// Check for economy support, and make sure player has money
			if (plugin.getEconomy() != null && ftsign.getPrice() > 0
					&& !player.hasPermission("fasttravelsigns.overrides.price")) {
				if (!plugin.getEconomy().has(player.getName(), ftsign.getPrice())) {
					FastTravelUtil.sendFTMessage(player,
							"You lack the money to travel there (Would cost "
									+ plugin.getEconomy().format(ftsign.getPrice()) + ")");
					return true;
				} else {
					// Charge player
					plugin.getEconomy().withdrawPlayer(player.getName(), ftsign.getPrice());
					FastTravelUtil.sendFTMessage(player, "You have been charged "
							+ plugin.getEconomy().format(ftsign.getPrice()));
				}
			}

			// Create travel task
			FastTravelTask traveltask = new FastTravelTask(plugin, player, ftsign);

			// Handle warmup time if needed
			int warmup = plugin.getConfig().getInt("warmup");
			if (warmup > 0 && !player.hasPermission("fasttravelsigns.overrides.warmup")) {
				FastTravelUtil.sendFTMessage(player,
						"Travelling to " + ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE
								+ " in " + warmup + " seconds.");
				plugin.playersWarmingUp.add(player.getName());
				plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, traveltask, warmup * 20);
			} else {
				//traveltask.run();
                executor.getExecutor().execute(traveltask);
			}

			if (cooldownLength > 0)
				cooldowns.put(player.getName(), curTime);
		}
		return true;
	}

}
