package com.github.alexthe666.rats.server.compat.simplecorn;

import com.github.alexthe666.rats.server.compat.crops.CropHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class CornMidHandler extends CropHandler {
	
	@ObjectHolder("simplecorn:corn_mid")
	public static final Block CORN_MID = null;

	@Override
	public Block getBlock() {
		return CORN_MID;
	}

	@Override
	public boolean isFullyGrown(World world, BlockPos pos, IBlockState blockState) {
		return false;
	}

}