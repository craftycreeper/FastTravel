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

import java.util.List;

import net.minebot.fasttravel.data.FTSign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;

public class FastTravelUtil {

	public static boolean isFTSign(Block block) {
		if (block == null)
			return false;
		if (block.getTypeId() != 63 && block.getTypeId() != 68)
			return false;
		String[] lines = ((Sign) block.getState()).getLines();
		String line1 = ChatColor.stripColor(lines[0]);
		if (line1.equalsIgnoreCase("[fasttravel]"))
			return true;
		return false;
	}

	public static boolean isFTSign(String[] lines) {
		String line1 = ChatColor.stripColor(lines[0]);
		if (line1.equalsIgnoreCase("[fasttravel]") || line1.equalsIgnoreCase("[ft]"))
			return true;
		return false;
	}

	public static void sendFTMessage(CommandSender sender, String mess) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "[FastTravel]" + ChatColor.WHITE + " " + mess);
	}

	public static void sendFTSignList(CommandSender sender, List<FTSign> signs, boolean econ) {
		int counter = 0;
		String pointstr = "";
		for (FTSign sign : signs) {
			counter++;
			if (counter != 1)
				pointstr = pointstr + ", ";
			pointstr = pointstr + ChatColor.AQUA + sign.getName() + ChatColor.WHITE;
			if (econ && sign.getPrice() > 0)
				pointstr = pointstr + " (" + sign.getPrice() + ")";
			if (counter == 4) {
				sendFTMessage(sender, pointstr);
				counter = 0;
				pointstr = "";
			}
		}
		if (counter != 0)
			sendFTMessage(sender, pointstr);
	}

	public static boolean safeLocation(Location loc) {
		double y = loc.getY();
		loc.setY(y + 1);
		Block block1 = loc.getWorld().getBlockAt(loc);
		loc.setY(y + 2);
		Block block2 = loc.getWorld().getBlockAt(loc);
		loc.setY(y);
		int id1 = block1.getTypeId();
		int id2 = block2.getTypeId();
		if ((id1 == 0 || id1 == 63 || id1 == 68) && (id2 == 0 || id2 == 63 || id2 == 68))
			return true;
		return false;
	}

	public static int getYawForFace(BlockFace face) {
		int dir;
		switch (face) {
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
		return dir + 90;
	}

}
