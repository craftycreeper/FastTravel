package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by oneill011990 on 03.09.2014.
 */
@SuppressWarnings("deprecation")
public class FastTravelRemoveCommand implements CommandExecutor {

    private FastTravelSignsPlugin plugin;

    public FastTravelRemoveCommand(FastTravelSignsPlugin plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("fasttravelsigns.remove")){
            FastTravelUtil.sendFTMessage(sender, "You don't have permission to do that");
        } else if (args.length == 0 || args[0] == null || args[1] == null) {
            FastTravelUtil.sendFTMessage(sender, "Invalid arguments.");
        } else {
            String sign = args[0];
            String player = args[1];

            FastTravelSign signRaw = FastTravelSignDB.getSign(sign);

            if (signRaw == null){
                FastTravelUtil.sendFTMessage(sender, "Sign not found");
            } else if (plugin.getServer().getPlayer(player) == null) {
                FastTravelUtil.sendFTMessage(sender, "Player not found.");

            } else if (signRaw.isAutomatic() || !signRaw.foundBy(plugin.getServer().getPlayer(player))){
                FastTravelUtil.sendFTMessage(sender, "Sign is automatic or hasn't been found by " + player);
            }

            FastTravelUtil.sendFTMessage(plugin.getServer().getPlayer(player),
                    "You have been removed from FastTravel: " + ChatColor.AQUA + signRaw.getName());

            signRaw.removePlayer(plugin.getServer().getPlayer(player));
        }

        return false;
    }
}
