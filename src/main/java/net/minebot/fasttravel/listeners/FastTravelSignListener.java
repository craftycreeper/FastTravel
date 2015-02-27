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

package net.minebot.fasttravel.listeners;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.Util.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Pattern;

public class FastTravelSignListener implements Listener {

	private FastTravelSignsPlugin plugin;

	public FastTravelSignListener(FastTravelSignsPlugin instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {

		Block sign = event.getBlock();
		String[] lines = event.getLines();

		Player player = event.getPlayer();

		if (!FastTravelUtil.isFTSign(lines))
			return;

		// Check create permission
		if (!player.hasPermission("fasttravelsigns.create")) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
					"You do not have permission to create travel points.");
			return;
		}

		// Check for valid name
		Pattern an = Pattern.compile("^[a-zA-Z0-9_-]+$");
		if (!an.matcher(lines[1]).find()) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
					"Travel point names should only contain numbers and letters (no spaces).");
			return;
		}

		// Check for existing sign with this name
		if (FastTravelSignDB.getSign(lines[1]) != null) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player, "There is already a travel point named "
					+ ChatColor.AQUA + lines[1] + ChatColor.WHITE + ".");
			return;
		}

		// Check to make sure above block is air
		Block aboveBlock = sign.getWorld().getBlockAt(sign.getX(), sign.getY() + 1, sign.getZ());
		if (aboveBlock.getState().getData().getItemType() != Material.AIR) {
			dropSign(sign);
			FastTravelUtil.sendFTMessage(player,
					"Travel signs need at least one block of air above them.");
			return;
		}

		else {
			FastTravelSign newFTSign = new FastTravelSign(lines[1], player.getUniqueId(), sign);

			// Economy support - set default price
			if (plugin.getEconomy() != null) {
				double defPrice = plugin.getConfig().getDouble("economy.default-price");
				if (defPrice > 0)
					event.setLine(2, "Price: " + defPrice);
				newFTSign.setPrice(defPrice);
			}

			FastTravelSignDB.addSign(newFTSign);

			FastTravelUtil.sendFTMessage(player, "New travel point " + ChatColor.AQUA + lines[1]
					+ ChatColor.WHITE + " created.");

			newFTSign.addPlayer(player.getUniqueId());

            // Colorize sign
            event.setLine(0, ChatColor.DARK_PURPLE + "[FastTravel]");
            event.setLine(1, ChatColor.DARK_BLUE + lines[1]);

		}

	}

	private void dropSign(Block block) {
		block.setType(Material.AIR);
		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
	}

}
