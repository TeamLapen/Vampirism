package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SundamageRegistry implements ISundamageRegistry {
    private static final Logger LOGGER = LogManager.getLogger(SundamageRegistry.class);
    private HashMap<DimensionType, Boolean> sundamageDims = new HashMap<>();
    private HashMap<DimensionType, Boolean> sundamageConfiguredDims = new HashMap<>();
    private Set<ResourceLocation> noSundamageBiomesIDs = new CopyOnWriteArraySet<>();
    private Set<ResourceLocation> noSundamageConfiguredBiomesIDs = new CopyOnWriteArraySet<>();
    private Set<Class> noSundamageBiomes = new CopyOnWriteArraySet<>();
    private boolean defaultSundamage = false;

    public SundamageRegistry() {
        sundamageDims.put(DimensionType.OVERWORLD, true);
        sundamageDims.put(DimensionType.NETHER, false);
        sundamageDims.put(DimensionType.THE_END, false);
    }

    @Override
    public void addNoSundamageBiome(ResourceLocation registryName) {
        noSundamageBiomesIDs.add(registryName);
    }

    @Override
    public void addNoSundamageBiome(Class clazz) {
        noSundamageBiomes.add(clazz);
    }

    public void addNoSundamageBiomeConfigured(ResourceLocation id) {
        noSundamageConfiguredBiomesIDs.add(id);
    }

    @Override
    public void addNoSundamageBiomes(Biome... biomes) {
        for (Biome b : biomes) {
            addNoSundamageBiome(b.getClass());
        }
    }

    @Deprecated
    @Override
    public boolean getSundamageInBiome(ResourceLocation registryName) {
        return !noSundamageBiomesIDs.contains(registryName) && !noSundamageConfiguredBiomesIDs.contains(registryName);
    }

    @Override
    public boolean getSundamageInBiome(@Nonnull Biome biome) {
        if (!getSundamageInBiome(biome.getRegistryName())) return false;
        for (Class clazz : noSundamageBiomes) {
            if (clazz.isAssignableFrom(biome.getClass())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean getSundamageInDim(DimensionType dim) {
        Boolean r = sundamageConfiguredDims.get(dim);
        if (r == null) {
            r = sundamageDims.get(dim);
        }
        return r == null ? defaultSundamage : r;
    }

    @Override
    public boolean isGettingSundamage(LivingEntity entity) {
        return Helper.gettingSundamge(entity);
    }

    @OnlyIn(Dist.CLIENT)
    public void readClient(CompoundNBT nbt) {
        if (nbt.contains("sundamage")) {
            CompoundNBT sundamage = nbt.getCompound("sundamage");
            defaultSundamage = sundamage.getBoolean("default");
            sundamageConfiguredDims.clear();
            CompoundNBT dimensions = sundamage.getCompound("dimensions");
            for (String s : dimensions.keySet()) {
                try {
                    ResourceLocation dim = new ResourceLocation(s);
                    boolean value = sundamage.getBoolean(s);
                    specifyConfiguredSundamageForDim(DimensionType.byName(dim), value);
                } catch (NumberFormatException e) {
                    LOGGER.error("Failed to parse dimension id (%s) in update packet ", s);
                }
            }
            noSundamageConfiguredBiomesIDs.clear();
            CompoundNBT biomes = sundamage.getCompound("biomes");
            for (String s : biomes.keySet()) {
                ResourceLocation res = new ResourceLocation(s);
                addNoSundamageBiomeConfigured(res);
            }
        }
    }

    /**
     * Resets the configured sundamage dims. E.G. on configuration reload
     */
    public void resetConfigurations() {
        sundamageConfiguredDims.clear();
        noSundamageConfiguredBiomesIDs.clear();
    }

    /**
     * Specifies the default value for non specified dimensions
     *
     * @param val
     */
    public void setDefaultDimsSundamage(boolean val) {
        defaultSundamage = val;
    }

    /**
     * Adds settings from Vampirism's config file.
     *
     * @param dimension
     * @param sundamage
     */
    public void specifyConfiguredSundamageForDim(DimensionType dimension, boolean sundamage) {
        sundamageConfiguredDims.put(dimension, sundamage);
    }

    @Override
    public void specifySundamageForDim(DimensionType dimension, boolean sundamage) {
        sundamageDims.put(dimension, sundamage);
    }

    public void writeServer(CompoundNBT nbt) {
        CompoundNBT sundamage = new CompoundNBT();
        CompoundNBT dimensions = new CompoundNBT();
        for (Map.Entry<DimensionType, Boolean> entry : sundamageConfiguredDims.entrySet()) {
            dimensions.putBoolean(DimensionType.getKey(entry.getKey()).toString(), entry.getValue());
        }
        sundamage.put("dimensions", dimensions);
        CompoundNBT biomes = new CompoundNBT();
        for (ResourceLocation s : noSundamageConfiguredBiomesIDs) {
            biomes.putBoolean(s.toString(), true);
        }
        sundamage.put("biomes", biomes);
        sundamage.putBoolean("default", defaultSundamage);
        nbt.put("sundamage", sundamage);
    }
}
