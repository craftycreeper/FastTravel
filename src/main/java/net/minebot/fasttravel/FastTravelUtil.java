/*
 *
 *  * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *  *
 *  * Copyright (c) 2011-2014 craftycreeper, minebot.net, oneill011990
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  * this software and associated documentation files (the "Software"), to deal in
 *  * the Software without restriction, including without limitation the rights to
 *  * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  * of the Software, and to permit persons to whom the Software is furnished to do
 *  * so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package net.minebot.fasttravel;

import net.minebot.fasttravel.data.FastTravelSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FastTravelUtil {

    private static Material[] safeBlocks = {Material.AIR, Material.SIGN, Material.SIGN_POST, Material.TORCH, Material.REDSTONE_TORCH_ON,
                                            Material.REDSTONE_TORCH_OFF, Material.REDSTONE, Material.LONG_GRASS, Material.YELLOW_FLOWER,
                                            Material.CROPS, Material.DEAD_BUSH};

    public static String newVersion;
    public static String curVersion;

	public static boolean isFTSign(Block block) {
		if (block == null)
			return false;
		if (block.getType() != Material.SIGN && block.getType() != Material.SIGN_POST)
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

	public static void sendFTSignList(CommandSender sender, List<FastTravelSign> signs, boolean econ) {
		int counter = 0;
		String pointstr = "";
		for (FastTravelSign sign : signs) {
			counter++;
			if (counter != 1)
				pointstr = pointstr + ", ";
			if (sign.isAutomatic())
				// Special coloring for automatic signs
				pointstr += ChatColor.GREEN;
			else
				pointstr += ChatColor.AQUA;
			pointstr += sign.getName() + ChatColor.WHITE;
			if (econ && sign.getPrice() > 0)
				pointstr += " (" + sign.getPrice() + ")";
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
		Material mat1 = block1.getType();
		Material mat2 = block2.getType();
		if ((Arrays.asList(safeBlocks).contains(mat1)) && (Arrays.asList(safeBlocks).contains(mat2)))
			return true;
		return false;
	}

    /**
     *
     * @param plugin This plugin
     * @return true if update, available false if not
     */
    public static boolean checkUpdate(FastTravelSignsPlugin plugin){
            try {
                URL verfile = new URL("http://germanspacebuild.de/FastTravelSigns_Version.txt");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(verfile.openStream()));
                String newVersion = in.readLine();
                in.close();
                String oldVersion = plugin.getDescription().getVersion();

                FastTravelUtil.newVersion = newVersion;
                FastTravelUtil.curVersion = oldVersion;

                if (newVersion == oldVersion){
                    return false;
                } else {
                    return true;
                }

            } catch (IOException ex) {
                // Ignore any problems that may happen
            }
        plugin.getLogger().info("Could not check for Updates");
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
