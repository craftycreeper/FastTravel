package net.minebot.fasttravel.listeners;

import java.util.HashMap;

import net.minebot.fasttravel.FastTravel;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class FastTravelPlayerListener extends PlayerListener {
	
	private HashMap<String, Long> interactLast = new HashMap<String, Long>();
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		if (!FastTravelUtil.isFTSign(block) || 
				(action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK))
			return;
		
		Sign sign = (Sign)block.getState();
		String[] lines = sign.getLines();
		String line1 = ChatColor.stripColor(lines[1]);
		
		if (!FastTravel.db.signExists(line1))
			return;
		
		if (!player.hasPermission("fasttravel.use")) {
			FastTravelUtil.sendFTMessage(player, "You don't have permission to use fast travel.");
			return;
		}
		
		long curTime = System.currentTimeMillis()/1000;
		Long lastTime = interactLast.get(player.getName());
		if (lastTime != null && curTime - lastTime.longValue() <= 8){
			//Wait 8 seconds before triggering this again to prevent spamming someone removing a sign
			return;
		}
		interactLast.put(player.getName(), curTime);
		//Now that the checks are done - see if the user has the sign, and
		//if not, add it.
		FastTravelSign ftsign = FastTravel.db.getSign(line1);
		
		if (FastTravel.db.userHasSign(player.getName(), ftsign)) {
			FastTravelUtil.sendFTMessage(player, "You have already added travel point " +
				ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE + ".");
		}
		else {
			FastTravel.db.giveSignToUser(player.getName(), ftsign);
			FastTravelUtil.sendFTMessage(player, "Travel point " +
				ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE + 
				" added!");
		}
		
	}

}
