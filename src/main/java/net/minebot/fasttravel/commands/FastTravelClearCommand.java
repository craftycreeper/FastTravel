package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by oneill011990 on 02.09.2014.
 */
public class FastTravelClearCommand implements CommandExecutor {

    private FastTravelSignsPlugin instance;

    public FastTravelClearCommand(FastTravelSignsPlugin instance){
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!((Player) sender).hasPermission("fasttravelsigns.clear")){
            FastTravelUtil.sendFTMessage(sender, "You don't have permission to do this.");
            return false;
        }
        if (args.length == 0){
            FastTravelUtil.sendFTMessage(sender, "You have to specify a sign to clear.");
        } else if (FastTravelSignDB.getSign(args[0]) == null){
            FastTravelUtil.sendFTMessage(sender, "Sign does not exist.");
        } else {
            FastTravelSign sign = FastTravelSignDB.getSign(args[0]);
            List<Player> players = sign.getPlayers();
            for (Player player : players) {
                    FastTravelUtil.sendFTMessage(player, "You have been remove from FastTravel: " + ChatColor.AQUA + sign.getName());
            }
            sign.removeAllPlayers();
            return true;
        }
        return false;
    }
}
