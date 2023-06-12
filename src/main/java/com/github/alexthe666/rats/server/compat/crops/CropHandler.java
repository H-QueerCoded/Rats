package com.github.alexthe666.rats.server.compat.crops;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class CropHandler {
	public abstract Block getBlock();
	public boolean isSpecialReplantCase() {return false;}
	public abstract boolean isFullyGrown(World world, BlockPos pos, IBlockState blockState);
	public void replant(World world, BlockPos pos, IBlockState blockState, NonNullList<ItemStack> drops) {};
}