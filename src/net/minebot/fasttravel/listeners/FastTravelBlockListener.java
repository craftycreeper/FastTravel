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

package net.minebot.fasttravel.listeners;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
//import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.material.Attachable;
//import org.bukkit.material.MaterialData;
import org.bukkit.block.Sign;

public class FastTravelBlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		Sign sign = null;
		
		if (FastTravelUtil.isFTSign(block))
			sign = (Sign)block.getState();
		else return;
		
		//Now we see if a sign exists with the name on this one,
		//and if so, check whether they have permissions to remove it
		if (sign == null) return;
		String[] lines = sign.getLines();
		String signName = ChatColor.stripColor(lines[1]);
		
		if (!FastTravelSignsPlugin.db.signExists(signName))
			return;
		
		FastTravelSign ftsign = 
			FastTravelSignsPlugin.db.getSign(signName);
		
		boolean allowed = false;
		
		if (player.hasPermission("fasttravelsigns.remove.all"))
			allowed = true;
		else if (player.hasPermission("fasttravelsigns.remove.own") &&
			ftsign.getCreator().equals(player.getName()))
			allowed = true;
			
		if (!allowed) {
			event.setCancelled(true);
			FastTravelUtil.sendFTMessage(player, "You may not remove this travel point.");
			return;
		}
		
		//Now we do the removal.
		FastTravelSignsPlugin.db.removeSign(ftsign.getName());
		FastTravelSignsPlugin.db.takeSignFromAllUsers(ftsign);
		
		FastTravelUtil.sendFTMessage(player, "Travel point " + ChatColor.AQUA +
			ftsign.getName() + ChatColor.WHITE + " has been removed.");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		if (FastTravelUtil.isFTSign(block)) {
			event.setCancelled(true);
		}
	}
	
	/* Outdated: we no longer worry about block placement. Kept here
	 * in case I change my mind. */
	
	/*
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		Sign sign = attachedSign(block, placeFaces);
		
		//Check above too... slow, but necessary
		//TODO: Make this configurable?
		if (sign == null) {
			Block bbelow = block.getWorld().getBlockAt(block.getX(), 
					block.getY() - 1, block.getZ());
			sign = attachedSign(bbelow, placeFaces);
		}
		if (sign == null) return;
		
		String[] lines = sign.getLines();
		
		if (!FastTravel.db.signExists(lines[1]))
			return;
		
		//No placing around signs
		FastTravelUtil.sendFTMessage(player, "You can't place blocks around an existing travel point.");
		
		event.setCancelled(true);
	}
	*/

	/*
	private static final BlockFace[] breakFaces = {BlockFace.UP, BlockFace.EAST,
		BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.SELF};
	private static final BlockFace[] placeFaces = {BlockFace.UP, BlockFace.EAST,
		BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH,
	BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST,
		BlockFace.SOUTH_WEST, BlockFace.SELF};
	 */
	
	/*
	private Sign attachedSign(Block block, BlockFace[] faceList) {
        for (BlockFace bf : faceList) {
            Block face = block.getRelative(bf);
            if (FastTravelUtil.isFTSign(face)) {
                Sign sign = (Sign)face.getState();
                return sign;
            }
        }
        return null;
    }
    */

}
