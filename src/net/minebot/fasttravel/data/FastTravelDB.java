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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minebot.fasttravel.FastTravelSignsPlugin;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FastTravelDB {

	private HashMap<String,FastTravelSign> signs;
	private HashMap<String,ArrayList<FastTravelSign>> userSigns;
	
	private String savePath = FastTravelSignsPlugin.dataDir + "/FastTravelSigns.db";
	
	public FastTravelDB(FastTravelSignsPlugin plugin) {
		File savefile = new File(savePath);
		if (savefile.exists()) {
			load();
			plugin.getLogger().info("Loaded " + signs.size() + " travel signs.");
		}
		
		else {
			signs = new HashMap<String,FastTravelSign>();
			userSigns = new HashMap<String,ArrayList<FastTravelSign>>();
			plugin.getLogger().info("Creating new database.");
			
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
	public void load() {
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
	
	public void save() {
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
