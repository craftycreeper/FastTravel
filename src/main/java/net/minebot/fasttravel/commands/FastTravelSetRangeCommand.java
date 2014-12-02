package net.minebot.fasttravel.commands;

import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.data.FastTravelSignDB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by oneill011990 on 03.09.2014.
 */
public class FastTravelSetRangeCommand implements CommandExecutor {

    private static int range;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("fasttravelsigns.range")){
            FastTravelUtil.sendFTMessage(sender, "You don't have permission to do this");
            return false;
        } else if (args.length <= 2 || args[0] == null || args[1] == null){
            FastTravelUtil.sendFTMessage(sender, "Invalid arguments.");
            return false;
        }

        FastTravelSign sign = FastTravelSignDB.getSign(args[0]);
        try {
            range = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        sign.setRange(range);

        return true;
    }
}
