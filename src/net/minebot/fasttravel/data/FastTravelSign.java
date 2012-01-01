package net.minebot.fasttravel.data;

import java.io.Serializable;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

public class FastTravelSign implements Serializable, Comparable<FastTravelSign> {

	private static final long serialVersionUID = 1L;
	
	private String name, creatorname, worldname;
	private int x, y, z;
	private BlockFace direction;
	
	public FastTravelSign(String name, String creatorname, Block block) {
		this.name = name;
		this.creatorname = creatorname;
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.worldname = block.getWorld().getName().toString();
		Sign s = (Sign)block.getState().getData();
		this.direction = s.getFacing();
	}
	
	public String getName() {
		return name;
	}
	
	public String getCreator() {
		return creatorname;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	
	public String getWorld() {
		return worldname;
	}

	public BlockFace getDirection() {
		return direction;
	}

	public int compareTo(FastTravelSign o) {
		return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
	}
	
}
