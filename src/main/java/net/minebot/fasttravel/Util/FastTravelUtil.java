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

package net.minebot.fasttravel.Util;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.data.FastTravelSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FastTravelUtil {

    /**
     * Blocks that are safe to be traveled to
     */
    public static Material[] safeBlocks = {Material.AIR, Material.SIGN, Material.SIGN_POST, Material.TORCH, Material.REDSTONE_TORCH_ON,
                                            Material.REDSTONE_TORCH_OFF, Material.REDSTONE, Material.LONG_GRASS, Material.YELLOW_FLOWER,
                                            Material.CROPS, Material.DEAD_BUSH};

    public static Material[] signBlocks = {Material.SIGN_POST, Material.SIGN, Material.WALL_SIGN};

    public static String newVersion;
    public static String curVersion;

    /**
     * Checks if block is a FasTravelSign
     * @param block Block to check
     * @return Is FastTravelSign or not
     */
	public static boolean isFTSign(Block block) {
		if (block == null)
			return false;
		if (!Arrays.asList(signBlocks).contains(block.getType()))
			return false;
		String[] lines = ((Sign) block.getState()).getLines();
		String line1 = ChatColor.stripColor(lines[0]);
		if (line1.equalsIgnoreCase("[fasttravel]") || line1.equalsIgnoreCase("[ft]"))
			return true;
		return false;
	}


    /**
     * Checks if lines belong to a FastTravelSign
     * @param lines Line to check
     * @return Belong to FAstTravelSign or not
     */
	public static boolean isFTSign(String[] lines) {
		String line1 = ChatColor.stripColor(lines[0]);
		if (line1.equalsIgnoreCase("[fasttravel]") || line1.equalsIgnoreCase("[ft]"))
			return true;
		return false;
	}

    /**
     * Send message to player
     *
     * Send a message to a player with FastTravel prefix
     *
     * @param sender Player to send message to
     * @param mess Message to send
     */
	public static void sendFTMessage(CommandSender sender, String mess) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "[FastTravel]" + ChatColor.WHITE + " " + mess);
	}

    public static void sendDebug(boolean enabled, String msg){
        if (enabled){
            System.out.println(msg);
        }
    }

	/**
	 * Prints a list of signs to a player.
	 * @param sender Player the message will be sent to.
	 * @param signs Signs to sent.
	 * @param econ Is economy enabled?
	 */
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

    public static List<String> sendSignNames(List<FastTravelSign> signs) {
        List<String> names = new ArrayList<>();

        for (FastTravelSign sign : signs) {
            names.add(sign.getName());
        }

        return names;
    }

    /**
     * Checks if location is safe.
     * @param loc Location to check.
     * @return Is the location safe?
     */
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
     * Method that checks for plugin updates
     *
     * @param plugin This plugin.
     * @return True if update available, false if not.
     */
	@Deprecated
    public static boolean checkUpdate(FastTravelSignsPlugin plugin){
		Exception exception;
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

            } catch (IOException e) {
                exception = e;
            }
        plugin.getLogger().info("Could not check for Updates: " + exception.getCause());
        return false;
    }

	/**
	 * Converts a list of strings to UUIDs.
	 * @param strings String to convert.
	 * @return List of UUIDs.
	 */
	public static List<UUID> stringToUUID(List<String> strings){
		List<UUID> ids = new ArrayList<UUID>();
		for (String string : strings) {
			ids.add(UUID.fromString(string));
		}
		return ids;
	}

	/**
	 * Convert a list of UUIDs into strings.
	 * @param ids UUIDs to convert.
	 * @return Converted UUIDs.
	 */
	public static List<String> uuidToString(List<UUID> ids){
		List<String> strings = new ArrayList<>();
		for (UUID id : ids) {
			strings.add(id.toString());
		}
		return strings;
	}

	/**
	 * Gets yaw for a blockface.
	 * @param face Face to get yaw from.
	 * @return Yaw of the face.
	 */
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

    public static void formatSign(Sign sign, String name){
        // Colorize sign
        sign.setLine(0, ChatColor.DARK_PURPLE + "[FastTravel]");
        sign.setLine(1, ChatColor.DARK_BLUE + name);

    }

}
