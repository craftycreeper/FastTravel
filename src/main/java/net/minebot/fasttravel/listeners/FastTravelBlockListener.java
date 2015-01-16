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

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

//import org.bukkit.block.BlockFace;
//import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.material.Attachable;
//import org.bukkit.material.MaterialData;

public class FastTravelBlockListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		Player player = event.getPlayer();

		Sign sign = null;

		if (FastTravelUtil.isFTSign(block))
			sign = (Sign) block.getState();
		else
			return;

		// Now we see if a sign exists with the name on this one,
		// and if so, check whether they have permissions to remove it
		if (sign == null)
			return;
		String[] lines = sign.getLines();
		String signName = ChatColor.stripColor(lines[1]);

		FastTravelSign ftsign = FastTravelSignDB.getSign(signName);
		if (ftsign == null)
			return;

		boolean allowed = false;

		if (player.hasPermission("fasttravelsigns.remove.all"))
			allowed = true;
		else if (player.hasPermission("fasttravelsigns.remove.own")
				&& ftsign.getCreator().equals(player.getName()))
			allowed = true;

		if (!allowed) {
			event.setCancelled(true);
			FastTravelUtil.sendFTMessage(player, "You may not remove this travel point.");
			return;
		}

		// Now we do the removal.
		FastTravelSignDB.removeSign(ftsign.getName());

		FastTravelUtil.sendFTMessage(player, ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE
				+ " has been removed.");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (FastTravelUtil.isFTSign(block)) {
			event.setCancelled(true);
		}
	}

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();
        if (FastTravelUtil.isFTSign(block.getWorld().getBlockAt(blockLocation.getBlockX(),
                blockLocation.getBlockY() - 1, blockLocation.getBlockZ()))) {
            FastTravelUtil.sendFTMessage(event.getPlayer(), "You can't place blocks above a FastTravelSign");
            event.setCancelled(true);
        }
    }

}
