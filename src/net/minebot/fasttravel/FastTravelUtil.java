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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class FastTravelUtil {
	
	public static boolean isFTSign(Block block) {
		if (block.getTypeId() != 63 && block.getTypeId() != 68)
			return false;
		String[] lines = ((Sign)block.getState()).getLines();
		String line1 = ChatColor.stripColor(lines[0]);
		if (line1.equalsIgnoreCase("[fasttravel]"))
			return true;
		return false;
	}
	
	public static boolean isFTSign(String[] lines) {
		String line1 = ChatColor.stripColor(lines[0]);
		if (line1.equalsIgnoreCase("[fasttravel]") ||
				line1.equalsIgnoreCase("[ft]"))
			return true;
		return false;
	}
	
	public static void sendFTMessage(Player player, String mess) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + "[FastTravel]" +
					ChatColor.WHITE + " " + mess);
	}
	
	public static boolean safeLocation(World world, Location loc) {
		double y = loc.getY();
		loc.setY(y+1);
		Block block1 = world.getBlockAt(loc);
		loc.setY(y+2);
		Block block2 = world.getBlockAt(loc);
		loc.setY(y);
		int id1 = block1.getTypeId();
		int id2 = block2.getTypeId();
		if ((id1 == 0 || id1 == 63 || id1 == 68) &&
			(id2 == 0 || id2 == 63 || id2 == 68))
			return true;
		return false;
	}
	
	public static int getYawForFace(BlockFace face) {
		int dir;
		switch(face) {
		case NORTH:
			dir = 0;
			break;
		case NORTH_NORTH_EAST:
		case NORTH_EAST:
		case EAST_NORTH_EAST:
			dir = 45;
			break;
		case EAST:
			dir = 90;
			break;
		case EAST_SOUTH_EAST:
		case SOUTH_EAST:
		case SOUTH_SOUTH_EAST:
			dir = 135;
			break;
		case SOUTH:
			dir = 180;
			break;
		case SOUTH_SOUTH_WEST:
		case SOUTH_WEST:
		case WEST_SOUTH_WEST:
			dir = 225;
			break;
		case WEST:
			dir = 270;
			break;
		case WEST_NORTH_WEST:
		case NORTH_WEST:
		case NORTH_NORTH_WEST:
			dir = 315;
			break;
		default:
			dir = 0;
			break;
		}
		return dir + 90; // wut
	}
	
}
