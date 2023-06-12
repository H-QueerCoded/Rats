package com.github.alexthe666.rats.server.compat.immersiveengineering;

import com.github.alexthe666.rats.server.compat.crops.CropHandler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class HempHandler extends CropHandler {

	@ObjectHolder("immersiveengineering:hemp")
	public static final Block HEMP = null;
	
	@Override
	public boolean isFullyGrown(World world, BlockPos pos, IBlockState blockState) {
		return world.getBlockState(pos.up()).getBlock() == HEMP
                && blockState.getBlock().getMetaFromState(blockState) == 4;
	}

	@Override
	public Block getBlock() {
		return HEMP;
	}
}
