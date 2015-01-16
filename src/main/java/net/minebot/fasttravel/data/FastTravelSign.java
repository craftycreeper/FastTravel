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

package net.minebot.fasttravel.data;

import net.minebot.fasttravel.FastTravelUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FastTravelSign implements Comparable<FastTravelSign> {

	private String name;
	private UUID creator;
	private double price;
	private int range;
	private Location location;
	private Location tploc;
	private boolean automatic; // Is this sign "always on"?
	private List<UUID> players;

	/**
	 * Constructor for sign without price.
	 *
	 * @param name    Name of sign.
	 * @param creator Lame of creator.
	 * @param block   Location of sign.
	 */
	public FastTravelSign(String name, UUID creator, Block block) {
		this.name = name;
		this.creator = creator;

		this.price = 0;
		this.range = -1;
		this.players = new ArrayList<UUID>();
		this.setAutomatic(false);

		this.location = block.getLocation();
		Sign s = (Sign) block.getState().getData();
		this.location.setYaw((float) FastTravelUtil.getYawForFace(s.getFacing()));
		this.tploc = location.clone();
	}

	/**
	 * Constructor for sign with price.
	 *
	 * @param name      Name of sign.
	 * @param creator   Name of creator.
	 * @param price     Price for travel.
	 * @param location  Location of sign.
	 * @param tpLoc     Travel destination.
	 * @param automatic Accessible for all players?
	 * @param players   Players that can use this sign.
	 */
	public FastTravelSign(String name, UUID creator, double price, Location location, Location tpLoc,
						  boolean automatic, int range, List<UUID> players) {
		this.name = name;
		this.creator = creator;
		this.price = price;
		this.range = range;
		this.players = players;
		this.setAutomatic(automatic);
		this.location = location;
		this.tploc = tpLoc;
	}

	/**
	 * Gets name of the sign.
	 *
	 * @return Name of the sign.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets name of the creator.
	 *
	 * @return Creator of the sign.
	 */
	public UUID getCreator() {
		return creator;
	}

	/**
	 * Gets the price for travel.
	 *
	 * @return Price for a travel.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets price for travel.
	 *
	 * @param price Price for a travel.
	 */
	public void setPrice(double price) {
		this.price = price;
		FastTravelSignDB.save();
	}

	/**
	 * Gets the location of the sign.
	 *
	 * @return Location of the sign.
	 */
	public Location getSignLocation() {
		return location;
	}

	/**
	 * Stets location of the sign.
	 *
	 * @param newSignLoc Location of sign.
	 */
	public void setSignLocation(Location newSignLoc) {
		location = newSignLoc.clone();
		FastTravelSignDB.save();
	}

	/**
	 * Sets destination for travel.
	 *
	 * @param newTPPoint Destination for travel.
	 */
	public void setTPLocation(Location newTPPoint) {
		tploc = newTPPoint.clone();
		FastTravelSignDB.save();
	}

	/**
	 * Gets destination for travel.
	 *
	 * @return Location where the player will travel to.
	 */
	public Location getTPLocation() {
		return tploc;
	}

	/**
	 * Add player to sign.
	 *
	 * @param player player to add.
	 */
	public void addPlayer(UUID player) {
		if (!players.contains(player))
			players.add(player);
		FastTravelSignDB.save();
	}

	/**
	 * Remove player from sign.
	 *
	 * @param player Player to remove.
	 */
	public void removePlayer(UUID player) {
		if (players.contains(player))
			players.remove(player);
		FastTravelSignDB.save();
	}

	/**
	 * Remove all players from sign.
	 */
	public void removeAllPlayers() {
		players.clear();
		FastTravelSignDB.save();
	}

	/**
	 * Gets all players that can use this sign.
	 *
	 * @return UUIDs of players that are allowed to use this sign.
	 */
	public List<UUID> getPlayers() {
		return players;
	}

	/**
	 * Is sign already found by player?
	 *
	 * @param player Player to check.
	 * @return Was it found by player or not?
	 */
	public boolean foundBy(UUID player) {
		return players.contains(player);
	}

	public int compareTo(FastTravelSign o) {
		return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
	}

	/**
	 * Is sign usable for all player by default?
	 *
	 * @return Is it automatic
	 */
	public boolean isAutomatic() {
		return automatic;
	}

	/**
	 * Sets sign automatic/manuel.
	 *
	 * @param automatic Sets it to manual/automatic.
	 */
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
		FastTravelSignDB.save();
	}

	/**
	 * Sets the range of the sign.
	 *
	 * @param range Range the sign will be set to.
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * Gets range of the sign.
	 *
	 * @return Range of the sign.
	 */
	public int getRange() {
		return this.range;
	}
}
