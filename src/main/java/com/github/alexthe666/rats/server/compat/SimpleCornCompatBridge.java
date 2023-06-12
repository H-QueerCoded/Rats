package com.github.alexthe666.rats.server.compat;

import com.github.alexthe666.rats.server.compat.crops.ModdedCrops;
import com.github.alexthe666.rats.server.compat.simplecorn.CornHandler;
import com.github.alexthe666.rats.server.compat.simplecorn.CornMidHandler;

import net.minecraftforge.fml.common.Loader;

public class SimpleCornCompatBridge {

	private static final String CORN_MOD_ID = "simplecorn";

	public static void loadCornCompat() {
	    if (Loader.isModLoaded(CORN_MOD_ID)) {
	    	ModdedCrops.cropList.add(new CornHandler());
	    	ModdedCrops.cropList.add(new CornMidHandler());
	    }
	}

}
