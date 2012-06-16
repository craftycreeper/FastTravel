package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelDB;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelSetpointCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		if (!((Player) sender).hasPermission("fasttravelsigns.setpoint")) {
			return false;
		}
		if (args.length == 0) {
			FastTravelUtil.sendFTMessage(sender,
					"You need to specify a fast travel sign to set the point for.");
			return true;
		}

		FastTravelSign sign = FastTravelDB.getSign(args[0]);
		if (sign == null) {
			FastTravelUtil.sendFTMessage(sender, "No fast travel sign exists with that name.");
		} else if (args.length == 1) {
			sign.setTPLocation(((Player) sender).getLocation());
			FastTravelUtil.sendFTMessage(sender, ChatColor.AQUA + sign.getName() + ChatColor.WHITE
					+ " will teleport users to this location now.");
		} else if (args.length == 2 && args[1].equals("clear")) {
			sign.setTPLocation(sign.getSignLocation().clone());
			FastTravelUtil.sendFTMessage(sender, ChatColor.AQUA + sign.getName() + ChatColor.WHITE
					+ " has had its alternate teleport location cleared.");
		}

		return true;
	}

}