package net.minebot.fasttravel.data;

import java.util.ArrayList;
import java.util.List;

import net.minebot.fasttravel.FastTravelUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;

public class FTSign implements Comparable<FTSign> {

	private String name, creator;
	private double price;
	private Location location;
	private Location tploc;
	private List<String> players;

	public FTSign(String name, String creatorname, Block block) {
		this.name = name;
		this.creator = creatorname;

		price = 0;

		players = new ArrayList<String>();

		location = block.getLocation();
		Sign s = (Sign) block.getState().getData();
		location.setYaw((float) FastTravelUtil.getYawForFace(s.getFacing()));
		tploc = location.clone();
	}

	public FTSign(String name, String creatorname, double price, Location location, Location tpLoc,
			List<String> players) {
		this.name = name;
		this.creator = creatorname;

		this.price = price;

		this.players = players;

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

	public int compareTo(FTSign o) {
		return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
	}
}
