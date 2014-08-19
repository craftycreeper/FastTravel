package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FastTravelReloadCommand implements CommandExecutor {
	private FastTravelSignsPlugin plugin;

	public FastTravelReloadCommand(FastTravelSignsPlugin instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && !((Player) sender).hasPermission("fasttravelsigns.reload")) {
			return false;
		}

		FastTravelUtil.sendFTMessage(sender, "Reloading configuration and sign database.");
		plugin.getLogger().info("Reload requested by " + sender.getName() + ".");
		plugin.dataInit();

		return true;
	}
}