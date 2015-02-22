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
import net.minebot.fasttravel.Util.DBType;
import net.minebot.fasttravel.Util.FastTravelUtil;
import net.minebot.fasttravel.data.Database;
import net.minebot.fasttravel.data.FileDBHandler;
import net.minebot.fasttravel.data.SQLDBHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by oneill011990 on 20.02.2015.
 */
public class FastTravelConvertCommand implements CommandExecutor {

    private FastTravelSignsPlugin plugin;
    private Database db;

    public FastTravelConvertCommand(FastTravelSignsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("fasttravelsigns.convert")) {
            FastTravelUtil.sendFTMessage(sender, "You don't have permissions to do that.");
            return false;
        }

        if (plugin.getDbHandler() == DBType.File){
            db = Database.getDatabaseBySystem("SQLite");
            FastTravelUtil.sendFTMessage(sender, "Converting database to SQLite.");
            db.init();
            SQLDBHandler.save();
            db.shutdown();
            return true;
        } else if (plugin.getDbHandler() == DBType.SQLite) {
            FastTravelUtil.sendFTMessage(sender, "Converting database to YAML-File.");
            FileDBHandler.load(plugin.getDataDir() + "/signs.yml");
            FileDBHandler.save(plugin.getDataDir() + "/signs.yml");
            return true;
        }

        return false;
    }
}
