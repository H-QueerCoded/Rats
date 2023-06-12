package com.github.alexthe666.rats.server.compat;

import com.github.alexthe666.rats.server.compat.crops.ModdedCrops;
import com.github.alexthe666.rats.server.compat.immersiveengineering.HempHandler;

import net.minecraftforge.fml.common.Loader;

public class ImmersiveEngineeringCompatBridge {
	private static final String IE_MOD_ID = "immersiveengineering";

	public static void loadImmersiveEngineeringCompat() {
	    if (Loader.isModLoaded(IE_MOD_ID)) {
	    	ModdedCrops.cropList.add(new HempHandler());
	    }
	}
}
