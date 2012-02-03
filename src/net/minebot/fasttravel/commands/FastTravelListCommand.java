package net.minebot.fasttravel.commands;

import java.util.List;

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FTSign;
import net.minebot.fasttravel.data.FastTravelDB;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelListCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if (sender instanceof Player && !((Player)sender).hasPermission("fasttravelsigns.list")) {
			return false;
		}
		
		List<FTSign> signs = FastTravelDB.getAllSigns();
		if (signs.size() == 0) {
			FastTravelUtil.sendFTMessage(sender, "The signs database is empty.");
		}
		else {
			FastTravelUtil.sendFTMessage(sender, "List of all fast travel signs:");
			int counter = 0;
			String pointstr = "";
			for (FTSign sign : signs) {
				counter++;
				if (counter != 1) pointstr = pointstr + ", ";
				pointstr = pointstr + ChatColor.AQUA + sign.getName() + ChatColor.WHITE;
				if (counter == 4) {
					FastTravelUtil.sendFTMessage(sender, pointstr);
					counter = 0;
					pointstr = "";
				}
			}
			if (counter != 0)
				FastTravelUtil.sendFTMessage(sender, pointstr);
		}
		
		return true;
	}

}
