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

import java.util.ArrayList;
import java.util.HashMap;

import net.minebot.fasttravel.FastTravelSignsPlugin;
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
	private FastTravelSignsPlugin plugin;
	
	public FastTravelCommand(FastTravelSignsPlugin instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		
		if (!player.hasPermission("fasttravelsigns.use")) {
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
			if (!FastTravelSignsPlugin.db.signExists(args[0])) {
				FastTravelUtil.sendFTMessage(player,
					"That travel point does not exist.");
				return true;
			}
			
			FastTravelSign ftsign = FastTravelSignsPlugin.db.getSign(args[0]);
			if (!FastTravelSignsPlugin.db.userHasSign(player.getName(), ftsign)) {
				FastTravelUtil.sendFTMessage(player,
					"You haven't found that travel point yet.");
				return true;
			}
			//Check if world exists
			World targworld = plugin.getServer().getWorld(ftsign.getWorld());
			if (targworld == null) {
				FastTravelUtil.sendFTMessage(player,
					"The world containing that location no longer exists. Oops!");
				FastTravelSignsPlugin.db.removeSign(args[0]);
				FastTravelSignsPlugin.db.takeSignFromAllUsers(ftsign);
				return true;
			}
			
			if (targworld != player.getWorld() &&
					!player.hasPermission("fasttravelsigns.multiworld")) {
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
		ArrayList<FastTravelSign> usigns = FastTravelSignsPlugin.db.getUserSigns(player.getName());
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
