/*
 * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 * 
 * Copyright (c) 2011-2012 craftycreeper, minebot.net
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
import net.minebot.fasttravel.data.FastTravelDB;
import net.minebot.fasttravel.data.FastTravelSign;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class FastTravelPlayerListener implements Listener {

	private HashMap<String, Long> interactLast = new HashMap<String, Long>();
    private FastTravelSignsPlugin plugin;

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		Action action = event.getAction();

		if (!FastTravelUtil.isFTSign(block)
				|| (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK))
			return;

		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		String line1 = ChatColor.stripColor(lines[1]);

		FastTravelSign ftsign = FastTravelDB.getSign(line1);
		if (ftsign == null)
			return;

		if (!player.hasPermission("fasttravelsigns.use")) {
			FastTravelUtil.sendFTMessage(player, "You don't have permission to use fast travel.");
			return;
		}

		long curTime = System.currentTimeMillis() / 1000;
		Long lastTime = interactLast.get(player.getName());
		if (lastTime != null && curTime - lastTime.longValue() <= 8) {
			// Wait 8 seconds before triggering this again to prevent
			// spamming someone removing a sign
			return;
		}
		interactLast.put(player.getName(), curTime);
		// Now that the checks are done - see if the user has the sign, and
		// if not, add it.

		if (ftsign.foundBy(player.getName())) {
			FastTravelUtil.sendFTMessage(player, "You have already added travel point "
					+ ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE + ".");
		} else {
			ftsign.addPlayer(player.getName());
			FastTravelUtil.sendFTMessage(player,
					"Travel point " + ChatColor.AQUA + ftsign.getName() + ChatColor.WHITE
							+ " added!");
		}

	}

    /*@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event){
        if (event.isCancelled() || plugin.getConfig().getInt("warmup") == 0 ||
            plugin.getConfig().getBoolean("interrupt on move") ||
            event.getPlayer().hasPermission("fasttravelsigns.overrides.interrupt")){
            return;
        }

        if (plugin.playersWarmingUp.contains(event.getPlayer().getName()));{
            plugin.playersWarmingUp.remove(event.getPlayer().getName());
            FastTravelUtil.sendFTMessage(event.getPlayer(), "Teleport aborted, don't move next time!");
        }
    }*/

}
