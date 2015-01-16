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

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelDeleteCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player && !((Player) sender).hasPermission("fasttravelsigns.delete")) {
			return false;
		}

		if (args.length == 0) {
			FastTravelUtil.sendFTMessage(sender,
					"You need to specify a fast travel sign to delete.");
		} else if (FastTravelSignDB.getSign(args[0]) == null) {
			FastTravelUtil.sendFTMessage(sender, "No fast travel sign exists with that name.");
		} else {
			FastTravelSign sign = FastTravelSignDB.getSign(args[0]);
			Block block = sign.getSignLocation().getBlock();
			// Attempt to nuke the sign
			if (FastTravelUtil.isFTSign(block)) {
				block.setType(Material.AIR);
			}
			FastTravelSignDB.removeSign(args[0]);
			FastTravelUtil.sendFTMessage(sender, ChatColor.AQUA + sign.getName() + ChatColor.WHITE
					+ " has been deleted.");
		}

		return true;
	}

}
