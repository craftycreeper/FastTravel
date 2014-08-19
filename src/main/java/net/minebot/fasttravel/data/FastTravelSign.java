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

package net.minebot.fasttravel.data;

import net.minebot.fasttravel.FastTravelUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.List;

public class FastTravelSign implements Comparable<FastTravelSign> {

	private String name, creator;
	private double price;
	private Location location;
	private Location tploc;
	private boolean automatic; // Is this sign "always on"?
	private List<String> players;

	public FastTravelSign(String name, String creatorname, Block block) {
		this.name = name;
		this.creator = creatorname;

		price = 0;
		players = new ArrayList<String>();
		setAutomatic(false);

		location = block.getLocation();
		Sign s = (Sign) block.getState().getData();
		location.setYaw((float) FastTravelUtil.getYawForFace(s.getFacing()));
		tploc = location.clone();
	}

	public FastTravelSign(String name, String creatorname, double price, Location location, Location tpLoc,
			boolean automatic, List<String> players) {
		this.name = name;
		this.creator = creatorname;
		this.price = price;
		this.players = players;
		this.setAutomatic(automatic);
		this.location = location;
		this.tploc = tpLoc;
	}

	public String getName() {
		return name;
	}

	public String getCreator() {
		return creator;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
		FastTravelDB.save();
	}

	public Location getSignLocation() {
		return location;
	}

	public void setSignLocation(Location newSignLoc) {
		location = newSignLoc.clone();
		FastTravelDB.save();
	}

	public void setTPLocation(Location newTPPoint) {
		tploc = newTPPoint.clone();
		FastTravelDB.save();
	}

	public Location getTPLocation() {
		return tploc;
	}

	public void addPlayer(String player) {
		if (!players.contains(player))
			players.add(player);
		FastTravelDB.save();
	}

	public void removePlayer(String player) {
		if (players.contains(player))
			players.remove(player);
		FastTravelDB.save();
	}

	public void removeAllPlayers() {
		players.clear();
		FastTravelDB.save();
	}

	public List<String> getPlayers() {
		return players;
	}

	public boolean foundBy(String player) {
		return players.contains(player);
	}

	public int compareTo(FastTravelSign o) {
		return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
		FastTravelDB.save();
	}
}
