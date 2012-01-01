package net.minebot.fasttravel.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FastTravelDBSave implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public HashMap<String,FastTravelSign> signs;
	public HashMap<String,ArrayList<FastTravelSign>> userSigns;
	
	public FastTravelDBSave(HashMap<String,FastTravelSign> signs,
			HashMap<String,ArrayList<FastTravelSign>> userSigns) {
		this.signs = signs;
		this.userSigns = userSigns;
	}
}