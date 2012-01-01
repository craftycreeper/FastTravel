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

package net.minebot.fasttravel.listeners;

import java.util.regex.Pattern;

import net.minebot.fasttravel.FastTravel;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class FastTravelSignListener extends BlockListener {

	public void onSignChange(SignChangeEvent event) {
		
		Block sign = event.getBlock();
		String[] lines = event.getLines();
		
		Player player = event.getPlayer();
		
		if (!FastTravelUtil.isFTSign(lines)) return;
		
		//Check create permission
		if (!player.hasPermission("fasttravel.create")) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
				"You do not have permission to create travel points.");
			return;
		}
		
		//Check for valid name
		Pattern an = Pattern.compile("^[a-zA-Z0-9]+$");
		if (!an.matcher(lines[1]).find()) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
				"Travel point names should only contain numbers and letters (no spaces).");
			return;
		}
		
		//Check for existing sign with this name
		if (FastTravel.db.signExists(lines[1])) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
				"There is already a travel point named " + ChatColor.AQUA
				+ lines[1] + ChatColor.WHITE + ".");
			return;
		}
		
		//Check to make sure above block is air
		Block aboveBlock = sign.getWorld().getBlockAt(sign.getX(), sign.getY() + 1,
				sign.getZ());
		if (aboveBlock.getState().getData().getItemType() != Material.AIR) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
				"Travel signs need at least one block of air above them.");
			return;
		}
		
		else {
			FastTravelSign ftsign = FastTravel.db.addSign(lines[1], sign, player);
			
			FastTravelUtil.sendFTMessage(player, "New travel point " +
					ChatColor.AQUA + lines[1] + ChatColor.WHITE + " created.");
			
			FastTravel.db.giveSignToUser(player.getName(), ftsign);
			
			//Colorize sign
			event.setLine(0, ChatColor.DARK_PURPLE +
					"[FastTravel]");
			event.setLine(1, ChatColor.DARK_BLUE + lines[1]);
		}
		
	}
	
	private void dropSign(Block block) {
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(),
        	new ItemStack(Material.SIGN, 1));
	}
	
}
