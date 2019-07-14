package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SundamageRegistry implements ISundamageRegistry {
    private static final Logger LOGGER = LogManager.getLogger(SundamageRegistry.class);
    private HashMap<DimensionType, Boolean> sundamageDims = new HashMap<>();
    private HashMap<DimensionType, Boolean> sundamageConfiguredDims = new HashMap<>();
    private Set<ResourceLocation> noSundamageBiomesIDs = new CopyOnWriteArraySet<>();
    private Set<ResourceLocation> noSundamageConfiguredBiomesIDs = new CopyOnWriteArraySet<>();
    private Set<Class> noSundamageBiomes = new CopyOnWriteArraySet<>();

    public SundamageRegistry() {
        sundamageDims.put(DimensionType.OVERWORLD, true);
        sundamageDims.put(DimensionType.THE_NETHER, false);
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
        return r == null ? VampirismConfig.SERVER.sundamageUnknownDimension.get() : r;
    }

    @Override
    public boolean isGettingSundamage(LivingEntity entity) {
        return Helper.gettingSundamge(entity);
    }


    public void reloadConfiguration() {
        sundamageConfiguredDims.clear();
        List<? extends String> negative = VampirismConfig.SERVER.sundamageDimensionsOverrideNegative.get();
        for (String s : negative) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            Optional<DimensionType> opt = Registry.DIMENSION_TYPE.getValue(id);
            opt.ifPresent(type -> sundamageConfiguredDims.put(type, false));
            if (!opt.isPresent()) {
                LOGGER.warn("Could not find configured negative sundamage dimension {}", s);
            }
        }
        List<? extends String> positive = VampirismConfig.SERVER.sundamageDimensionsOverridePositive.get();
        for (String s : positive) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            Optional<DimensionType> opt = Registry.DIMENSION_TYPE.getValue(id);
            opt.ifPresent(type -> sundamageConfiguredDims.put(type, true));
            if (!opt.isPresent()) {
                LOGGER.warn("Could not find configured positive sundamage dimension {}", s);
            }
        }

        noSundamageConfiguredBiomesIDs.clear();
        List<? extends String> biomes = VampirismConfig.SERVER.sundamageDisabledBiomes.get();
        for (String s : biomes) {
            ResourceLocation id = new ResourceLocation(s);
            noSundamageConfiguredBiomesIDs.add(id);
        }

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

}
