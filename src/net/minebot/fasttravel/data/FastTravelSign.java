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
