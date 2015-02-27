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

package net.minebot.fasttravel.task;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.Util.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import org.bukkit.*;

import java.util.UUID;

public class FastTravelTask implements Runnable {

	private FastTravelSignsPlugin plugin;
	private UUID player;
	private FastTravelSign sign;

	public FastTravelTask(FastTravelSignsPlugin plugin, UUID player, FastTravelSign sign) {
		this.plugin = plugin;
		this.player = player;
		this.sign = sign;
	}

    @Override
	public void run() {

        if (!plugin.getServer().getPlayer(player).isOnline())
            return;


        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Ok we are in the Runnable now");

        if (plugin.getConfig().getBoolean("economy.enabled") || plugin.getEconomy() != null) {

            FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Economy is enabled and loaded");

            if (!plugin.getServer().getPlayer(player).hasPermission("fasttravelsigns.overrides.price")){
                if (!plugin.getEconomy().has(plugin.getServer().getPlayer(player), sign.getPrice())) {

                    FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "You seem to be a poor man." +
                            " But it's your problem so no traveling for you!");

                    FastTravelUtil.sendFTMessage(plugin.getServer().getPlayer(player),
                            "You lack the money to travel there (Would cost "
                                    + plugin.getEconomy().format(sign.getPrice()) + ")");
                    return;
                } else {

                    FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Now let's take some money");

                    // Charge player
                    boolean success = plugin.getEconomy().withdrawPlayer(plugin.getServer().getPlayer(player),
                            sign.getPrice()).transactionSuccess();
                    if (success){

                        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Yes, I took the money");

                        FastTravelUtil.sendFTMessage(plugin.getServer().getPlayer(player), "You have been charged "
                                + plugin.getEconomy().format(sign.getPrice()));
                    } else {

                        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"),
                                "Oh no, I couldn't take the money but I have to let you go anyway :(");

                        FastTravelUtil.sendFTMessage(plugin.getServer().getPlayer(player),
                                "Economy seems to be broken, but today is your lucky day," +
                                        " you might travel anyway");
                    }
                }
            }
        }


		Location targ = sign.getTPLocation();
		while (!FastTravelUtil.safeLocation(targ)) {
			// Find a safe place - simple "go up" algorithm
			targ.setY(targ.getY() + 1);
		}

		World targworld = sign.getTPLocation().getWorld();
		Chunk targChunk = targworld.getChunkAt(targ);
		if (!targChunk.isLoaded())
			targChunk.load();


        FastTravelUtil.sendDebug(plugin.getConfig().getBoolean("DevMode"), "Ok now let's Travel");

        plugin.getServer().getPlayer(player).getWorld().playSound(plugin.getServer().getPlayer(player).getLocation(),
                Sound.CHICKEN_EGG_POP, 15, 1);
        plugin.getServer().getPlayer(player).getWorld().playEffect(plugin.getServer().getPlayer(player).getLocation(),
                Effect.SMOKE, 1);

        plugin.getServer().getPlayer(player).teleport(targ);
		FastTravelUtil.sendFTMessage(plugin.getServer().getPlayer(player), "Travelled to " + ChatColor.AQUA + sign.getName()
                + ChatColor.WHITE + ".");
	}

}