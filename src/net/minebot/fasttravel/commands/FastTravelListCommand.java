package net.minebot.fasttravel.commands;

import java.util.List;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelDB;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelListCommand implements CommandExecutor {

	private FastTravelSignsPlugin plugin;

	public FastTravelListCommand(FastTravelSignsPlugin instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player && !((Player) sender).hasPermission("fasttravelsigns.list")) {
			return false;
		}

		List<FastTravelSign> signs = FastTravelDB.getAllSigns();
		if (signs.size() == 0) {
			FastTravelUtil.sendFTMessage(sender, "The signs database is empty.");
		} else {
			FastTravelUtil.sendFTMessage(sender, "List of all fast travel signs:");
			FastTravelUtil.sendFTSignList(sender, signs, (plugin.getEconomy() != null));
		}
		return true;
	}

}
