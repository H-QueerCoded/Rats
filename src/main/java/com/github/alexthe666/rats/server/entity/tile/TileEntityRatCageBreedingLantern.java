package com.github.alexthe666.rats.server.entity.tile;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.EntityRat;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityRatCageBreedingLantern extends TileEntityRatCageDecorated implements ITickable {

    public int breedingCooldown = 0;
    private Random random;

    public TileEntityRatCageBreedingLantern() {
        random = new Random();
    }

    public ItemStack getContainedItem() {
        return new ItemStack(RatsItemRegistry.RAT_BREEDING_LANTERN);
    }

    public void setContainedItem(ItemStack stack) {
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("BreedingCooldown", breedingCooldown);
        return super.writeToNBT(compound);
    }

    public void readFromNBT(NBTTagCompound compound) {
        breedingCooldown = compound.getInteger("BreedingCooldown");
        super.readFromNBT(compound);
    }


    @Override
    public void update() {
        float f = (24000.0F - breedingCooldown) / 24000.0F;
        float f1 = f * 0.6F + 0.4F;
        float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
        float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
        float i = this.getPos().getX() + 0.5F;
        float j = this.getPos().getY() + 0.5F;
        float k = this.getPos().getZ() + 0.5F;
        if (breedingCooldown <= 0) {
            double d0 = 1.5F;
            List<EntityRat> rats = world.getEntitiesWithinAABB(EntityRat.class, new AxisAlignedBB((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0));
            if (rats.size() < RatsMod.CONFIG_OPTIONS.ratCageCramming && rats.size() > 0) {
                List<EntityRat> breedableRats = new ArrayList<>();
                for (EntityRat rat : rats) {
                    if (!rat.isChild() && rat.isInCage() && rat.breedCooldown == 0) {
                    	breedableRats.add(rat);
                    }
                }
                if (breedableRats.size() > 1) {
                    EntityRat parent1 = breedableRats.get(random.nextInt(breedableRats.size() - 1));
                    breedableRats.remove(parent1);
                    EntityRat parent2 = breedableRats.get(random.nextInt(breedableRats.size() - 1));
                    parent1.world.setEntityState(parent1, (byte) 83);
                    parent2.world.setEntityState(parent2, (byte) 83);
                    parent2.createBabiesFrom(parent1, parent2);
                    breedingCooldown = 24000;
                    parent1.breedCooldown = 24000;
                    parent2.breedCooldown = 24000;
                }
            }
        } else {
            breedingCooldown--;
        }
        world.spawnParticle(EnumParticleTypes.REDSTONE, i + random.nextDouble() - 0.5D, j + random.nextDouble() - 0.5D, k + random.nextDouble() - 0.5D, f1, f2, f3);
    }
}
