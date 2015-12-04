package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.profiler.Profiler;
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
                VampirismMod.log.t("Angle %s",angle);
                if(angle>0.3&&angle<0.9){//TODO adjust
                    if(entity.worldObj.canBlockSeeSky(entity.getPosition())){
                        if(VampirismAPI.getSundamageInBiome( entity.worldObj.getBiomeGenForCoords(entity.getPosition()).biomeID)){
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
}
