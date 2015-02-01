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

package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import net.minebot.fasttravel.event.FastTravelEvent;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FastTravelCommand implements CommandExecutor {

	private HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>();
	private FastTravelSignsPlugin plugin;

	public FastTravelCommand(FastTravelSignsPlugin instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Uh. somebody sent a command, let's see" +
                " what's gonna happen next.");

		if (!(sender instanceof Player)) {
            FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "You are not human you may not" +
                    " command me.");
            return false;
        }

		Player player = (Player) sender;

		if (!player.hasPermission("fasttravelsigns.use")) {
            FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Ups a player tried to travel without" +
                    " a ticket. Someone needs a punishment");
			FastTravelUtil.sendFTMessage(player, "You don't have permission to use fast travel.");
			return true;
		}

        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "At least you are allowed to do this.");

		if (args.length == 0) {

            FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Can't you even remember a few names of" +
                    " points you found out there in the world?");

			// Send a list
			FastTravelUtil.sendFTMessage(player, "Your travel points:");
			List<FastTravelSign> usigns = FastTravelSignDB.getSignsFor(player);
			if (usigns == null || usigns.size() == 0) {
				FastTravelUtil.sendFTMessage(player,
						"None. Find [FastTravel] signs and right click them to activate.");
			} else
				FastTravelUtil.sendFTSignList(player, usigns, (plugin.getEconomy() != null));
		}

		else if (args.length == 1) {

			// Time to travel. Check if the requested sign exists.
			FastTravelSign ftsign = FastTravelSignDB.getSign(args[0]);

			if (ftsign == null) {
                FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Traveling to an non existing" +
                        " location doesn't sound like a good idea does it?");
				FastTravelUtil.sendFTMessage(player, "That travel point does not exist.");
				return true;
			}

			boolean allPoints = player.hasPermission("fasttravelsigns.overrides.allpoints");
			if (!(ftsign.isAutomatic() || ftsign.foundBy(player.getUniqueId())) && !allPoints) {
                FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "You don't understand the rules" +
                        " don't you? First find a point then travel to it.");
				FastTravelUtil.sendFTMessage(player, "You haven't found that travel point yet.");
				return true;
			}
			// Check if world exists
			World targworld = ftsign.getTPLocation().getWorld();
			if (!allPoints && !targworld.equals(player.getWorld())
					&& !player.hasPermission("fasttravelsigns.multiworld")) {
                FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Traveling between different worlds?" +
                        " Are you Dr. Who or what?");
				FastTravelUtil
						.sendFTMessage(player, "You may not fast travel to different worlds.");
				return true;
			}

            if (plugin.getEconomy() == null  || !plugin.getConfig().getBoolean("economy.enabled")) {
                FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Somebody was lacking the" +
                        " intelligence to setup economy properly.");
                plugin.getServer().getPluginManager().callEvent(new FastTravelEvent(player, ftsign));
                return true;
            }
		}
		return true;
	}

}
