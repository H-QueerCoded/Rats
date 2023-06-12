package com.github.alexthe666.rats.server.entity.ai;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.compat.crops.CropHandler;
import com.github.alexthe666.rats.server.compat.crops.ModdedCrops;
import com.github.alexthe666.rats.server.entity.EntityRat;
import com.github.alexthe666.rats.server.entity.RatCommand;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RatAIHarvestCrops extends EntityAIBase {
    private final EntityRat entity;
    private final BlockSorter targetSorter;
    private BlockPos targetBlock = null;
    private int feedingTicks;

    public RatAIHarvestCrops(EntityRat entity) {
        super();
        this.entity = entity;
        this.targetSorter = new BlockSorter(entity);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.canMove() || !this.entity.isTamed() || this.entity.getCommand() != RatCommand.HARVEST || this.entity.isInCage()) {
            return false;
        }
        if (!this.entity.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            return false;
        }
        resetTarget();
        return targetBlock != null;
    }

    private void resetTarget() {
        List<BlockPos> allBlocks = new ArrayList<>();
        int RADIUS = this.entity.getSearchRadius();
        cropSearch:
        for (BlockPos pos : BlockPos.getAllInBox(this.entity.getSearchCenter().add(-RADIUS, -RADIUS, -RADIUS), this.entity.getSearchCenter().add(RADIUS, RADIUS, RADIUS))) {
            IBlockState block = this.entity.world.getBlockState(pos);
            if ((block.getBlock() instanceof BlockCrops && ((BlockCrops) block.getBlock()).isMaxAge(block) || !(block.getBlock() instanceof BlockCrops) && block.getBlock() instanceof BlockBush || block.getMaterial() == Material.GOURD) && !(block.getBlock() instanceof BlockStem)) {
            	for (CropHandler c : ModdedCrops.cropList) {
					if(block.getBlock() == c.getBlock() && !c.isFullyGrown(this.entity.world, pos, block))
						continue cropSearch;
				}
                Item item = block.getBlock().getItemDropped(block, entity.getRNG(), 0);
                if(entity.canRatPickupItem(new ItemStack(item))){
                    allBlocks.add(pos);

                }
            }
        }
        if (!allBlocks.isEmpty()) {
            allBlocks.sort(this.targetSorter);
            this.targetBlock = allBlocks.get(0);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return targetBlock != null && this.entity.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && (this.entity.world.getBlockState(targetBlock).getBlock() instanceof BlockBush || this.entity.world.getBlockState(targetBlock).getMaterial() == Material.GOURD);
    }

    public void resetTask() {
        this.entity.getNavigator().clearPath();
        resetTarget();
    }

    @Override
    public void updateTask() {
        if (this.targetBlock != null) {
            IBlockState block = this.entity.world.getBlockState(this.targetBlock);
            this.entity.getNavigator().tryMoveToXYZ(this.targetBlock.getX() + 0.5D, this.targetBlock.getY(), this.targetBlock.getZ() + 0.5D, 1D);
            if (block.getBlock() instanceof BlockBush || block.getMaterial() == Material.GOURD) {
                if (block.getBlock() instanceof BlockCrops && !((BlockCrops) block.getBlock()).isMaxAge(block)) {
                    this.targetBlock = null;
                    this.resetTask();
                    return;
                }
                double distance = this.entity.getDistance(this.targetBlock.getX(), this.targetBlock.getY(), this.targetBlock.getZ());
                if (distance < 1.5F) {
                    NonNullList<ItemStack> drops = NonNullList.create();
                    block.getBlock().getDrops(drops, this.entity.world, targetBlock, block, 0);

                    this.entity.world.destroyBlock(targetBlock, false);
                    if ((!RatsMod.CONFIG_OPTIONS.ratsBreakBlockOnHarvest || entity.hasUpgrade(RatsItemRegistry.RAT_UPGRADE_REPLANTER)) && block.getBlock() instanceof BlockCrops || ModdedCrops.cropList.stream().anyMatch( c -> c.getBlock()==block.getBlock() )) {
                        for (int i = 0; i < drops.size(); ++i) {
                            if (isPlantabe(drops.get(i).getItem())) {
                                if (drops.get(i).getCount() == 1)
                                    drops.remove(i);
                                else
                                    drops.get(i).setCount(drops.get(i).getCount() - 1);
                                this.entity.world.setBlockState(targetBlock, block.getBlock().getDefaultState());
                            }
                        }
                        ModdedCrops.cropList.stream().filter( c -> c.getBlock()==block.getBlock() && c.isSpecialReplantCase()).forEach( r -> r.replant(this.entity.world, targetBlock, block, drops));
                    }

                    if (!drops.isEmpty() && entity.canRatPickupItem(drops.get(0))) {

                        ItemStack duplicate = drops.get(0).copy();
                        drops.remove(0);
                        if (!this.entity.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && !this.entity.world.isRemote) {
                            this.entity.entityDropItem(this.entity.getHeldItem(EnumHand.MAIN_HAND), 0.0F);
                        }
                        this.entity.setHeldItem(EnumHand.MAIN_HAND, duplicate);
                        for (ItemStack drop : drops) {
                            this.entity.entityDropItem(drop, 0);
                        }
                        this.entity.fleePos = this.targetBlock;
                    }
                    this.targetBlock = null;
                    this.resetTask();
                }
            }

        }
    }

    private static boolean isPlantabe(Item item) {
        if (item instanceof IPlantable) {
            return true;
        } else if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            if (block instanceof IPlantable)
                return true;
        }
        return false;
    }

    public class BlockSorter implements Comparator<BlockPos> {
        private final Entity entity;

        public BlockSorter(Entity entity) {
            this.entity = entity;
        }

        @Override
        public int compare(BlockPos pos1, BlockPos pos2) {
            double distance1 = this.getDistance(pos1);
            double distance2 = this.getDistance(pos2);
            return Double.compare(distance1, distance2);
        }

        private double getDistance(BlockPos pos) {
            double deltaX = this.entity.posX - (pos.getX() + 0.5);
            double deltaY = this.entity.posY + this.entity.getEyeHeight() - (pos.getY() + 0.5);
            double deltaZ = this.entity.posZ - (pos.getZ() + 0.5);
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        }
    }
}
