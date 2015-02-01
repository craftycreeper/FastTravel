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

package net.minebot.fasttravel.listeners;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import net.minebot.fasttravel.event.FastTravelEvent;
import net.minebot.fasttravel.task.FastTravelTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created by oneill011990 on 15.09.2014.
 */
public class FastTravelListener implements Listener {

    private FastTravelSignsPlugin plugin;

    public FastTravelListener(FastTravelSignsPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onFastTravel(FastTravelEvent event) {

        Player p = event.getPlayer();
        FastTravelSign sign = event.getSign();

        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Uh, someone wants to Travel");

        if (p.hasPermission("fasttravelsigns.overrides.warmup")){
            FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Oh, someone is a cheater." +
                    " Tar and feather him.");
            plugin.getServer().getScheduler().runTask(plugin, new FastTravelTask(plugin, p.getUniqueId(), sign));
        } else {
            FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "You deserve a cookie.");
            plugin.getServer().getScheduler().runTaskLater(plugin, new FastTravelTask(plugin, p.getUniqueId(), sign),
                    plugin.getConfig().getLong("warmup") * 20);
        }

    }

}
