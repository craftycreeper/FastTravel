/*
 *
 *  * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *  *
 *  * Copyright (c) 2011-2015 craftycreeper, minebot.net, oneill011990
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  * this software and associated documentation files (the "Software"), to deal in
 *  * the Software without restriction, including without limitation the rights to
 *  * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  * of the Software, and to permit persons to whom the Software is furnished to do
 *  * so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
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

/**
 * Created by oneill011990 on 03.09.2014.
 */
//@SuppressWarnings("deprecation")
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

            } else if (signRaw.isAutomatic() || !signRaw.foundBy(plugin.getServer().getPlayer(player).getUniqueId())){
                FastTravelUtil.sendFTMessage(sender, "Sign is automatic or hasn't been found by " + player);
            }

            FastTravelUtil.sendFTMessage(plugin.getServer().getPlayer(player),
                    "You have been removed from FastTravel: " + ChatColor.AQUA + signRaw.getName());

            signRaw.removePlayer(plugin.getServer().getPlayer(player).getUniqueId());
        }

        return false;
    }
}
