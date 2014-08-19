package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelDB;
import net.minebot.fasttravel.data.FastTravelSign;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelPriceCommand implements CommandExecutor {

	private FastTravelSignsPlugin plugin;

	public FastTravelPriceCommand(FastTravelSignsPlugin instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (!((Player) sender).hasPermission("fasttravelsigns.price")) {
			return false;
		}
		if (plugin.getEconomy() == null) {
			FastTravelUtil.sendFTMessage(sender, "Economy support is disabled!");
			return true;
		}
		if (args.length == 0) {
			FastTravelUtil.sendFTMessage(sender,
					"You need to specify a fast travel sign to set the price for.");
			return true;
		}

		FastTravelSign sign = FastTravelDB.getSign(args[0]);
		if (sign == null) {
			FastTravelUtil.sendFTMessage(sender, "No fast travel sign exists with that name.");
		} else if (args.length == 1) {
			FastTravelUtil.sendFTMessage(sender, "You must provide a price (0 to charge nothing).");
		} else if (args.length == 2) {
			double newPrice;
			try {
				newPrice = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				FastTravelUtil.sendFTMessage(sender, "Invalid price given; expecting a number.");
				return true;
			}
			sign.setPrice(newPrice);
			FastTravelUtil.sendFTMessage(sender, ChatColor.AQUA + sign.getName() + ChatColor.WHITE
					+ " now costs " + plugin.getEconomy().format(newPrice));

			// Try to set the price on the actual sign if possible
			Block block = sign.getSignLocation().getBlock();
			if (FastTravelUtil.isFTSign(block)) {
				Sign realSign = (Sign) block.getState();
				if (newPrice > 0)
					realSign.setLine(2, "Price: " + newPrice);
				else
					realSign.setLine(2, "");
				realSign.update();
			}
		}
		return true;
	}

}
