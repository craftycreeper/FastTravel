/*
 *
 *  * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *  *
 *  * Copyright (c) 2011-2014 craftycreeper, minebot.net, oneill011990
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

package net.minebot.fasttravel.data;

import net.minebot.fasttravel.FastTravelUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.List;


public class FastTravelSign implements Comparable<FastTravelSign> {

	private String name;
    private Player creator;
	private double price;
    private int range;
	private Location location;
	private Location tploc;
	private boolean automatic; // Is this sign "always on"?
    private List<Player> players;

    /**
     * Constructor for sign without price
     * @param name name of sign
     * @param creatorname name of creator
     * @param block location of sign
     */
	public FastTravelSign(String name, Player creatorname, Block block) {
		this.name = name;
		this.creator = creatorname;

		price = 0;
        range = -1;
		players = new ArrayList<Player>();
		setAutomatic(false);

		location = block.getLocation();
		Sign s = (Sign) block.getState().getData();
		location.setYaw((float) FastTravelUtil.getYawForFace(s.getFacing()));
		tploc = location.clone();
	}

    /**
     * Constructor for sign with price
     * @param name name of sign
     * @param creatorname name of creator
     * @param price price for teleport
     * @param location location of sign
     * @param tpLoc teleport destination
     * @param automatic accessible for all players
     * @param players players that can use this sign
     */
	public FastTravelSign(String name, Player creatorname, double price, Location location, Location tpLoc,
			boolean automatic,int range, List<Player> players) {
		this.name = name;
		this.creator = creatorname;
		this.price = price;
        this.range= range;
		this.players = players;
		this.setAutomatic(automatic);
		this.location = location;
		this.tploc = tpLoc;
	}

    /**
     * Gets name of the sign
     * @return Name
     */
	public String getName() {
		return name;
	}

    /**
     * Gets name of the creator
     * @return creator
     */
	public Player getCreator() {
		return creator;
	}

    /**
     * Gets the price for teleport
     * @return price
     */
	public double getPrice() {
		return price;
	}

    /**
     * Sets price for teleport
     * @param price price
     */
	public void setPrice(double price) {
		this.price = price;
		FastTravelSignDB.save();
	}

    /**
     * Gets the location of the sign     *
     * @return Location
     */
	public Location getSignLocation() {
		return location;
	}

    /**
     * Stets location of the sign
     * @param newSignLoc Location of sign
     */
	public void setSignLocation(Location newSignLoc) {
		location = newSignLoc.clone();
		FastTravelSignDB.save();
	}

    /**
     * Sets destination for teleport
     * @param newTPPoint Destination for teleport
     */
	public void setTPLocation(Location newTPPoint) {
		tploc = newTPPoint.clone();
		FastTravelSignDB.save();
	}

    /**
     * Gets destination for teleport
     * @return Location where the player is teleported to
     */
	public Location getTPLocation() {
		return tploc;
	}

    /**
     * Add player to sign
     * @param player player to add
     */
	public void addPlayer(Player player) {
		if (!players.contains(player))
			players.add(player);
		FastTravelSignDB.save();
	}

    /**
     * Remove player from sign
     * @param player player to remove
     */
	public void removePlayer(Player player) {
		if (players.contains(player))
			players.remove(player);
		FastTravelSignDB.save();
	}

    /**
     * Remove all players from sign
     */
	public void removeAllPlayers() {
		players.clear();
		FastTravelSignDB.save();
	}

    /**
     * Gets all players that can use this sign
     * @return Players that are allowed to use this sign
     */
	public List<Player> getPlayers() {
		return players;
	}

    /**
     * Is sign already found by player
     * @param player player to check
     * @return found by player
     */
	public boolean foundBy(Player player) {
		return players.contains(player.getUniqueId());
	}

	public int compareTo(FastTravelSign o) {
		return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
	}

    /**
     * Is sign usable for all player by default?
     * @return
     */
	public boolean isAutomatic() {
		return automatic;
	}

    /**
     * Sets sign automatic/manuel
     * @param automatic is automatic
     */
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
		FastTravelSignDB.save();
	}

    public void setRange(int range){
        this.range = range;
    }

    public int getRange(){
        return this.range;
    }
}
