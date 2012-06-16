package net.minebot.fasttravel.listeners;

import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelDB;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class FastTravelEntityListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled() || event.blockList().isEmpty())
			return;

		// Prevent signs from being exploded
		for (FastTravelSign sign : FastTravelDB.getAllSigns()) {
			if (event.blockList().contains(sign.getSignLocation().getBlock())) {
				event.blockList().clear();
				return;
			}
		}
	}

}
