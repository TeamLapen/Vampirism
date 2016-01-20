package de.teamlapen.vampirism.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SunDmgHelper {
    /**
     * This code was copied from the 1.8 branch. In this this field was intended to be used by the api, here is is mostly useless.
     */
    private final static HashMap<Integer, Boolean> sundamageDims = new HashMap<Integer, Boolean>();
    private final static HashMap<Integer, Boolean> sundamageConfiguredDims = new HashMap<Integer, Boolean>();
    private final static Set<Integer> noSundamageBiomes = new CopyOnWriteArraySet<Integer>();
    private static boolean defaultSundamage = false;

    static {
        sundamageDims.put(0, true);
        sundamageDims.put(-1, false);
        sundamageDims.put(1, false);
    }

    /**
     * Specifies the default value for non specified dimensions
     * FOR INTERNAL USAGE ONLY
     *
     * @param val
     */
    public static void setDefaultDimsSundamage(boolean val) {
        defaultSundamage = val;
    }

    /**
     * Adds a biome in which no sundamage is applied
     *
     * @param id
     */
    public static void addNoSundamageBiome(int id) {
        noSundamageBiomes.add(id);
    }

    /**
     * Specifies if vampires should get sundamage in this dimension
     *
     * @param dimensionId
     * @param sundamage
     */
    public static void specifySundamageForDim(int dimensionId, boolean sundamage) {
        sundamageDims.put(dimensionId, sundamage);
    }

    /**
     * Checkd if vampirs can get sundamage in that biome
     *
     * @param id
     * @return
     */
    public static boolean getSundamageInBiome(int id) {
        return !noSundamageBiomes.contains(id);
    }

    /**
     * Resets the configured sundamage dims. E.G. on configuration reload
     * FOR INTERNAL USAGE ONLY
     */
    public static void resetConfiguredSundamgeDims() {
        sundamageConfiguredDims.clear();
    }

    /**
     * Adds settings from Vampirism's config file.
     * FOR INTERNAL USAGE ONLY
     *
     * @param dimensionId
     * @param sundamage
     */
    public static void specifyConfiguredSundamageForDim(int dimensionId, boolean sundamage) {
        sundamageConfiguredDims.put(dimensionId, sundamage);
    }

    /**
     * Checks if vampires can get sundamge in that dimension
     *
     * @param dim
     * @return
     */
    public static boolean getSundamageInDim(int dim) {
        Boolean r = sundamageConfiguredDims.get(dim);
        if (r == null) {
            r = sundamageDims.get(dim);
        }
        return r == null ? defaultSundamage : r;
    }

    /**
     * Checks if the entity can get sundamage at it's current position.
     * It is recommend to cache the value for a few ticks.
     *
     * @param entity
     * @return
     */
    public static boolean gettingSundamge(EntityLivingBase entity) {
        try {
            MinecraftServer.getServer().theProfiler.startSection("vampirism_checkSundamage");
            if (entity.worldObj != null && getSundamageInDim(entity.worldObj.provider.dimensionId)) {
                if (!entity.worldObj.isRaining()) {
                    float angle = entity.worldObj.getCelestialAngle(1.0F);
                    if (angle > 0.78 || angle < 0.24) {
                        if (entity.worldObj.canBlockSeeTheSky(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ))) {
                            if (getSundamageInBiome(entity.worldObj.getBiomeGenForCoords(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ)).biomeID)) {
                                MinecraftServer.getServer().theProfiler.endSection();
                                return true;
                            }
                        }
                    }

                }
            }
            MinecraftServer.getServer().theProfiler.endSection();
        } catch (Exception e) {
            Logger.e("SunDmgHelper", "Really strange exception while trying to check sundamage", e);
            return false;
        }
        return false;
    }
}
