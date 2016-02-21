package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;


public class Helper {
    public static void spawnParticlesAroundEntity(EntityLivingBase e, EnumParticleTypes particle, double maxDistance, int amount) {
        //TODO implement
    }

    /**
     * Checks if the entity can get sundamage at it's current position.
     * It is recommend to cache the value for a few ticks.
     * @param entity
     * @return
     */
    public static boolean gettingSundamge(EntityLivingBase entity){
        MinecraftServer.getServer().theProfiler.startSection("vampirism_checkSundamage");
        if(entity.worldObj!=null&& VampirismAPI.getSundamageInDim(entity.worldObj.provider.getDimensionId())){
            if(!entity.worldObj.isRaining()){
                float angle=entity.worldObj.getCelestialAngle(1.0F);
                if (angle > 0.78 || angle < 0.24) {
                    if(entity.worldObj.canBlockSeeSky(entity.getPosition())){
                        int biomeID = 0;
                        try {
                            biomeID = entity.worldObj.getBiomeGenForCoords(entity.getPosition()).biomeID;
                        } catch (NullPointerException e) {
                            //Strange thing which happen in 1.7.10, not sure about 1.8
                        }
                        if (VampirismAPI.getSundamageInBiome(biomeID)) {
                            MinecraftServer.getServer().theProfiler.endSection();
                            return true;
                        }
                    }
                }

            }
        }
        MinecraftServer.getServer().theProfiler.endSection();
        return false;
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
