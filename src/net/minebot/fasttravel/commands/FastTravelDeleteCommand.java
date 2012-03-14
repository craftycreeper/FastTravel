package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FTSign;
import net.minebot.fasttravel.data.FastTravelDB;

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
		} else if (FastTravelDB.getSign(args[0]) == null) {
			FastTravelUtil.sendFTMessage(sender, "No fast travel sign exists with that name.");
		} else {
			FTSign sign = FastTravelDB.getSign(args[0]);
			Block block = sign.getSignLocation().getBlock();
			// Attempt to nuke the sign
			if (FastTravelUtil.isFTSign(block)) {
				block.setType(Material.AIR);
			}
			FastTravelDB.removeSign(args[0]);
			FastTravelUtil.sendFTMessage(sender, ChatColor.AQUA + sign.getName() + ChatColor.WHITE
					+ " has been deleted.");
		}

		return true;
	}

}
