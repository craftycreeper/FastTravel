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

package net.minebot.fasttravel.commands;

import java.util.ArrayList;
import java.util.HashMap;

import net.minebot.fasttravel.FastTravel;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelCommand implements CommandExecutor {
	
	private HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	private FastTravel plugin;
	
	public FastTravelCommand(FastTravel instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		
		if (!player.hasPermission("fasttravel.use")) {
			FastTravelUtil.sendFTMessage(player,
				"You don't have permission to use fast travel.");
			return true;
		}
		
		if (args.length == 0) {
			//Send a list
			sendList(player);
		}
		
		else if (args.length == 1) {
			//Check cooldown
			int cooldownLength = plugin.getConfig().getInt("cooldown");
			long curTime = System.currentTimeMillis()/1000;
			if (cooldownLength > 0) {
				Long cd = cooldowns.get(player.getName());
				if (cd != null && (curTime - cd) < cooldownLength) {
					long timeRemaining = (cooldownLength - (curTime - cd));
					String strRemain = timeRemaining + " seconds";
					if (timeRemaining > 59) {
						int min = (int)(timeRemaining / 60);
						if (min == 1) strRemain = "1 minute";
						else strRemain = min + " minutes";
					}
					FastTravelUtil.sendFTMessage(player, "You must wait " + 
						strRemain + " before travelling again.");
					return true;
				}
			}
			
			//Time to travel. Check if the requested sign exists.
			if (!FastTravel.db.signExists(args[0])) {
				FastTravelUtil.sendFTMessage(player,
					"That travel point does not exist.");
				return true;
			}
			
			FastTravelSign ftsign = FastTravel.db.getSign(args[0]);
			if (!FastTravel.db.userHasSign(player.getName(), ftsign)) {
				FastTravelUtil.sendFTMessage(player,
					"You haven't found that travel point yet.");
				return true;
			}
			//Check if world exists
			World targworld = plugin.getServer().getWorld(ftsign.getWorld());
			if (targworld == null) {
				FastTravelUtil.sendFTMessage(player,
					"The world containing that location no longer exists. Oops!");
				FastTravel.db.removeSign(args[0]);
				FastTravel.db.takeSignFromAllUsers(ftsign);
				return true;
			}
			
			if (targworld != player.getWorld() &&
					!player.hasPermission("fasttravel.multiworld")) {
				FastTravelUtil.sendFTMessage(player,
					"You may not fast travel to different worlds.");
				return true;
			}
			
			//Go!
			float yaw = (float)FastTravelUtil.getYawForFace(ftsign.getDirection());
			Location target = new Location(targworld, ftsign.getX(), 
					ftsign.getY(), ftsign.getZ(), yaw, 0);
			while (!FastTravelUtil.safeLocation(targworld, target)) {
				//Find a safe place
				target.setY(target.getY() + 1);
			}
			player.teleport(target);
			FastTravelUtil.sendFTMessage(player, "Travelling to " + 
					ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE + ".");
			
			if (cooldownLength > 0) cooldowns.put(player.getName(), curTime);
		}
		
		return true;
	}
	
	private void sendList(Player player) {
		FastTravelUtil.sendFTMessage(player, "Your travel points:");
		ArrayList<FastTravelSign> usigns = FastTravel.db.getUserSigns(player.getName());
		if (usigns == null || usigns.size() == 0) {
			FastTravelUtil.sendFTMessage(player,
				"None. Find [FastTravel] signs and right click them to activate.");
		}
		else {
			int counter = 0;
			String pointstr = "";
			for (FastTravelSign sign : usigns) {
				counter++;
				if (counter != 1) pointstr = pointstr + ", ";
				pointstr = pointstr + ChatColor.AQUA + sign.getName() + ChatColor.WHITE;
				if (counter == 4) {
					FastTravelUtil.sendFTMessage(player, pointstr);
					counter = 0;
					pointstr = "";
				}
			}
			if (counter != 0)
				FastTravelUtil.sendFTMessage(player, pointstr);
		}
	}
	
	

}
