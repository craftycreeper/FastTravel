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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minebot.fasttravel.FastTravel;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FastTravelDB {

	private HashMap<String,FastTravelSign> signs;
	private HashMap<String,ArrayList<FastTravelSign>> userSigns;
	
	private String savePath = FastTravel.dataDir + "/FastTravel.db";
	
	public FastTravelDB() {
		File savefile = new File(savePath);
		if (savefile.exists()) {
			load();
			FastTravel.log.info("[FastTravel] Loaded " + signs.size() + " travel signs.");
		}
		
		else {
			signs = new HashMap<String,FastTravelSign>();
			userSigns = new HashMap<String,ArrayList<FastTravelSign>>();
			FastTravel.log.info("[FastTravel] Creating new database.");
			
			save();
		}
	}
	
	public boolean signExists(String name) {
		return signs.containsKey(name.toLowerCase());
	}
	
	public boolean signAt(int x, int y, int z) {
		for (FastTravelSign sign : signs.values()) {
			if (sign.getX() == x && sign.getY() == y && sign.getZ() == z)
				return true;
		}
		return false;
	}
	
	public FastTravelSign getSign(String name) {
		return signs.get(name.toLowerCase());
	}
	
	public boolean userHasSign(String username, FastTravelSign sign) {
		if (!userSigns.containsKey(username)) return false;
		else return userSigns.get(username).contains(sign);
	}
	
	public ArrayList<FastTravelSign> getUserSigns(String username) {
		if (!userSigns.containsKey(username)) return null;
		else return userSigns.get(username);
	}
	
	//Add/remove signs
	public FastTravelSign addSign(String name, Block block, Player p) {
		FastTravelSign ftsign = new FastTravelSign(name, p.getName(), block);
		signs.put(name.toLowerCase(), ftsign);
		save();
		return ftsign;
	}
	
	public void removeSign(String name) {
		signs.remove(name.toLowerCase());
		save();
	}
	
	//Which users have which sign access
	public void giveSignToUser(String username, FastTravelSign sign) {
		if (!userSigns.containsKey(username))
			userSigns.put(username, new ArrayList<FastTravelSign>());
		
		ArrayList<FastTravelSign> usigns = userSigns.get(username);
		
		if (!usigns.contains(sign)) {
			usigns.add(sign);
			Collections.sort(usigns);
			save();
		}
	}
	
	public void takeSignFromUser(String username, FastTravelSign sign) {
		if (!userSigns.containsKey(username)) return;
		ArrayList<FastTravelSign> usigns = userSigns.get(username);
		if (usigns.contains(sign)) {
			usigns.remove(sign);
			save();
		}
	}
	
	public void takeSignFromAllUsers(FastTravelSign sign) {
		for (ArrayList<FastTravelSign> usigns : userSigns.values()) {
			if (usigns.contains(sign))
				usigns.remove(sign);
		}
		save();
	}
	
	public void clearUserSigns(String username) {
		userSigns.remove(username);
		save();
	}
	
	//Load/save
	private void load() {
		try {
			File savefile = new File(savePath);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
			Object readobj = ois.readObject();
			ois.close();
			
			FastTravelDBSave saveData = (FastTravelDBSave)readobj;
			this.signs = saveData.signs;
			this.userSigns = saveData.userSigns;
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void save() {
		FastTravelDBSave saveData = new FastTravelDBSave(signs, userSigns);
		try {
			File savefile = new File(savePath);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile));
			oos.writeObject(saveData);
			oos.flush();
			oos.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
