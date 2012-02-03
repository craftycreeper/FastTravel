package net.minebot.fasttravel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minebot.fasttravel.data.FastTravelDBSave;
import net.minebot.fasttravel.data.FastTravelSign;

import org.bukkit.configuration.file.YamlConfiguration;

public class FastTravelLegacyDBConverter {

	public static void convert(String newSaveFile, String oldDBFile) {
		Map<String,FastTravelSign> signs;
		Map<String,ArrayList<FastTravelSign>> userSigns;
		
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			File savefile = new File(oldDBFile);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
			Object readobj = ois.readObject();
			ois.close();
				
			FastTravelDBSave saveData = (FastTravelDBSave)readobj;

			signs = saveData.signs;
			userSigns = saveData.userSigns;
		} catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		for(String signName : signs.keySet()) {
			FastTravelSign sign = signs.get(signName);
			signName = sign.getName();
			config.set(signName + ".creator", sign.getCreator());
			config.set(signName + ".signloc.world", sign.getWorld());
			config.set(signName + ".signloc.x", sign.getX());
			config.set(signName + ".signloc.y", sign.getY());
			config.set(signName + ".signloc.z", sign.getZ());
			config.set(signName + ".signloc.yaw", FastTravelUtil.getYawForFace(sign.getDirection()));
			config.set(signName + ".tploc.world", sign.getWorld());
			config.set(signName + ".tploc.x", sign.getX());
			config.set(signName + ".tploc.y", sign.getY());
			config.set(signName + ".tploc.z", sign.getZ());
			config.set(signName + ".tploc.yaw", FastTravelUtil.getYawForFace(sign.getDirection()));
			
			List<String> signPlayers = new ArrayList<String>();
			
			for (String playerName : userSigns.keySet())
				for (FastTravelSign usign : userSigns.get(playerName))
					if (usign.getName().equals(sign.getName())) 
						signPlayers.add(playerName);
			
			config.set(signName + ".players", signPlayers);

		}
		
		try {
			config.save(newSaveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
