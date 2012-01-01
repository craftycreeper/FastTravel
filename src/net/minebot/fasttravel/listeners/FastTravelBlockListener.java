package net.minebot.fasttravel.listeners;

import net.minebot.fasttravel.FastTravel;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
//import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.material.Attachable;
//import org.bukkit.material.MaterialData;
import org.bukkit.block.Sign;

public class FastTravelBlockListener extends BlockListener {
	
	//private static final BlockFace[] breakFaces = {BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.SELF};
	//private static final BlockFace[] placeFaces = {BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH,
	//	BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.SELF};
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		Sign sign = null;
		
		if (FastTravelUtil.isFTSign(block))
			sign = (Sign)block.getState();
		else return;
		
		/*
		else {
			//Prevent removal via removing attached block
			sign = attachedSign(block, breakFaces);
			if (sign == null) return;
			Block attached = getAttachedBlock(sign.getBlock());
			//Not the block we need to be worrying about
			if (attached != block) return;
		}
		*/
		
		//Now we see if a sign exists with the name on this one,
		//and if so, check whether they have permissions to remove it
		if (sign == null) return;
		String[] lines = sign.getLines();
		String signName = ChatColor.stripColor(lines[1]);
		
		if (!FastTravel.db.signExists(signName))
			return;
		
		FastTravelSign ftsign = 
			FastTravel.db.getSign(signName);
		
		boolean allowed = false;
		
		if (player.hasPermission("fasttravel.remove.all"))
			allowed = true;
		else if (player.hasPermission("fasttravel.remove.own") &&
			ftsign.getCreator().equals(player.getName()))
			allowed = true;
			
		if (!allowed) {
			event.setCancelled(true);
			FastTravelUtil.sendFTMessage(player, "You may not remove this travel point.");
			return;
		}
		
		//Now we do the removal.
		FastTravel.db.removeSign(ftsign.getName());
		FastTravel.db.takeSignFromAllUsers(ftsign);
		
		FastTravelUtil.sendFTMessage(player, "Travel point " + ChatColor.AQUA +
			ftsign.getName() + ChatColor.WHITE + " has been removed.");
	}
	
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		if (FastTravelUtil.isFTSign(block)) {
			event.setCancelled(true);
		}
	}
	
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
	
	public static Block getAttachedBlock(Block b) {
        MaterialData m = b.getState().getData();
        BlockFace face = BlockFace.DOWN;
        if (m instanceof Attachable) {
            face = ((Attachable) m).getAttachedFace();
        }
        return b.getRelative(face);
    }
    */

}
