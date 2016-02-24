package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.IGarlicBlock;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceGeneral;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;


public class Helper {
    public static void spawnParticlesAroundEntity(EntityLivingBase e, EnumParticleTypes particle, double maxDistance, int amount) {
        //TODO implement
    }

    /**
     * Checks if the entity can get sundamage at it's current position.
     * It is recommend to cache the value for a few ticks.
     *
     * @param entity
     * @return
     */
    public static boolean gettingSundamge(EntityLivingBase entity) {
        if (entity.worldObj != null) {
            entity.worldObj.theProfiler.startSection("vampirism_checkSundamage");
            if (VampirismAPI.getSundamageInDim(entity.worldObj.provider.getDimensionId())) {
                if (!entity.worldObj.isRaining()) {
                    float angle = entity.worldObj.getCelestialAngle(1.0F);
                    //TODO maybe use this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)
                    if (angle > 0.78 || angle < 0.24) {
                        if (entity.worldObj.canBlockSeeSky(entity.getPosition())) {
                            int biomeID = 0;
                            try {
                                biomeID = entity.worldObj.getBiomeGenForCoords(entity.getPosition()).biomeID;
                            } catch (NullPointerException e) {
                                //Strange thing which happen in 1.7.10, not sure about 1.8
                            }
                            if (VampirismAPI.getSundamageInBiome(biomeID)) {
                                entity.worldObj.theProfiler.endSection();
                                return true;
                            }
                        }
                    }

                }
            }
            entity.worldObj.theProfiler.endSection();
        }
        return false;
    }

    public static EnumGarlicStrength getGarlicStrength(BlockPos pos) {
        return EnumGarlicStrength.NONE;//TODO
    }

    public static EnumGarlicStrength gettingGarlicDamage(EntityLivingBase entity) {
        //TODO Check performance
        if (entity.worldObj == null) return EnumGarlicStrength.NONE;
        entity.worldObj.theProfiler.startSection("vampirism_checkGarlic");
        EnumGarlicStrength max = EnumGarlicStrength.NONE;
        BlockPos middle = entity.getPosition();
        int dist = BalanceGeneral.GARLIC_CHECK_RANGE;
        int minX = middle.getX() - dist;
        int minY = middle.getY() - BalanceGeneral.GARLIC_CHECK_VERTICAL_RANGE;
        if (minY < 0) minY = 0;
        int minZ = middle.getZ() - dist;
        int maxX = middle.getX() + dist + 1;
        int maxY = middle.getY() + BalanceGeneral.GARLIC_CHECK_VERTICAL_RANGE + 1 + 1;
        int maxZ = middle.getZ() + dist + 1;
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (entity.worldObj.getBlockState(blockpos.set(x, y, z)).getBlock() instanceof IGarlicBlock) {
                        max = ((IGarlicBlock) entity.worldObj.getBlockState(blockpos).getBlock()).getGarlicStrength(entity.worldObj, blockpos);
                        if (max == EnumGarlicStrength.STRONG) {
                            entity.worldObj.theProfiler.endSection();
                            return max;
                        }
                    }
                }
            }
        }
        entity.worldObj.theProfiler.endSection();
        return max;
    }

    public static boolean canBecomeVampire(EntityPlayer player) {
        return FactionPlayerHandler.get(player).canJoin(VampirismAPI.VAMPIRE_FACTION);
    }

    public static boolean isVampire(EntityPlayer player) {
        return FactionPlayerHandler.get(player).isInFaction(VampirismAPI.VAMPIRE_FACTION);
    }

    public static boolean isHunter(EntityPlayer player) {
        return FactionPlayerHandler.get(player).isInFaction(VampirismAPI.HUNTER_FACTION);
    }
}
