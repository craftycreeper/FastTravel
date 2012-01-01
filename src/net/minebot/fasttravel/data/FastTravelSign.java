/*
 * FastTravel - The Exploration and RPG-Friendly Teleportation Plugin
 * 
 * Copyright (c) 2011 craftycreeper, minebot.net
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would
 *    be appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not
 *    be misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source
 *    distribution.
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
