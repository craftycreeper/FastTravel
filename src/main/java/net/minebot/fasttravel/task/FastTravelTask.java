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

package net.minebot.fasttravel.task;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import net.minebot.fasttravel.FastTravelUtil;
import net.minebot.fasttravel.data.FastTravelSign;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FastTravelTask implements Runnable {

	private FastTravelSignsPlugin plugin;
	private Player player;
	private String name;
	private FastTravelSign sign;

	public FastTravelTask(FastTravelSignsPlugin plugin, Player player, FastTravelSign sign) {
		this.plugin = plugin;
		this.player = player;
		this.name = player.getName();
		this.sign = sign;
	}

    @Override
	public void run() {
		plugin.playersWarmingUp.remove(name);

		// Double check to make sure they didn't log off...
		if (!player.isOnline()) {
			return;
		}

		Location targ = sign.getTPLocation().clone();
		while (!FastTravelUtil.safeLocation(targ)) {
			// Find a safe place - simple "go up" algorithm
			targ.setY(targ.getY() + 1);
		}

		World targworld = sign.getTPLocation().getWorld();
		Chunk targChunk = targworld.getChunkAt(targ);
		if (!targChunk.isLoaded())
			targChunk.load();

		player.teleport(targ);
		FastTravelUtil.sendFTMessage(player, "Travelled to " + ChatColor.AQUA + sign.getName()
				+ ChatColor.WHITE + ".");
	}

}