package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by oneill011990 on 03.09.2014.
 */
public class FastTravelSetRangeCommand implements CommandExecutor {

    private static int range;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)){
            return false;
        } else if (!sender.hasPermission("fasttravelsigns.range")){
            FastTravelUtil.sendFTMessage(sender, "You don't have permission to do this");
            return false;
        } else if (args.length == 0){
            FastTravelUtil.sendFTMessage(sender, "Invalid arguments.");
            return false;
        }

        FastTravelSign sign = FastTravelSignDB.getSign(args[0]);
        if (sign == null) {
            FastTravelUtil.sendFTMessage(sender, "No fast travel sign exists with that name.");
        } else if (args.length == 2){
            try {
                range = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
            sign.setRange(range);
            FastTravelUtil.sendFTMessage(sender, "Set range of " + ChatColor.AQUA + sign.getName() + ChatColor.WHITE +" to: " + range);

            return true;
        }

        return true;
    }

}
