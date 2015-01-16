/*
 * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *
 * Copyright (c) 2011-2015 craftycreeper, minebot.net, oneill011990
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

/**
 * Created by oneill011990 on 26.12.2014.
 */
public class FastTravelMoveCommand implements CommandExecutor{

    FastTravelSignsPlugin plugin;

    public FastTravelMoveCommand(FastTravelSignsPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("fasttravelsigns.move")) {
            FastTravelUtil.sendFTMessage(sender, "You don't have permission to do that!");
            return false;
        } else if (!(sender instanceof Player)){
            plugin.getLogger().info("Command must be sent by a player.");
            return false;
        } else if (args.length < 1 || args[0] == null) {
            FastTravelUtil.sendFTMessage(sender, "Invalid arguments!");
            return false;
        }

        FastTravelSign sign = FastTravelSignDB.getSign(args[0]);
        Player player = (Player) sender;
        if (sign == null) {
            FastTravelUtil.sendFTMessage(sender, "Sign " + ChatColor.AQUA + args[0] + ChatColor.WHITE + " not found!");
            return false;
        }
        FastTravelUtil.sendFTMessage(sender, "Right-click new sign with a stick for this FastTravel.");
        plugin.getEditors().put(player, sign);


        return true;
    }
}
