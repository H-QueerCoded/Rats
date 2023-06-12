package com.github.alexthe666.rats.server.compat.simplecorn;

import com.github.alexthe666.rats.server.compat.crops.CropHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import panda.corn.blocks.BlockCorn;

public class CornHandler extends CropHandler {
	
	@ObjectHolder("simplecorn:corn")
	public static final Block CORN = null;

	@Override
	public Block getBlock() {
		return CORN;
	}

	@Override
	public boolean isFullyGrown(World world, BlockPos pos, IBlockState blockState) {
		BlockCorn blockCorn = (BlockCorn) blockState.getBlock();
		return blockState.getValue(BlockCorn.CORNAGE) == blockCorn.getMaxAge()+1;
	}
	
	@Override
	public boolean isSpecialReplantCase() {
		return true;
	}

	@Override
	public void replant(World world, BlockPos pos, IBlockState blockState, NonNullList<ItemStack> drops) {
		world.setBlockState(pos, blockState.getBlock().getDefaultState());
	}

}
