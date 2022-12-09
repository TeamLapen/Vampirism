package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SundamageRegistry implements ISundamageRegistry {
    private static final Logger LOGGER = LogManager.getLogger(SundamageRegistry.class);
    private final HashMap<ResourceKey<Level>, Boolean> sundamageDims = new HashMap<>();
    private final HashMap<ResourceKey<Level>, Boolean> sundamageConfiguredDims = new HashMap<>();
    private final Set<ResourceLocation> noSundamageBiomesIDs = new CopyOnWriteArraySet<>();
    private final Set<ResourceLocation> noSundamageConfiguredBiomesIDs = new CopyOnWriteArraySet<>();

    public SundamageRegistry() {
        sundamageDims.put(Level.OVERWORLD, true);
        sundamageDims.put(Level.NETHER, false);
        sundamageDims.put(Level.END, false);
    }


    public void addNoSundamageBiomeConfigured(ResourceLocation id) {
        noSundamageConfiguredBiomesIDs.add(id);
    }

    @Override
    public void addNoSundamageBiomes(ResourceLocation... biomes) {
        noSundamageBiomesIDs.addAll(Arrays.asList(biomes));
    }

    @Deprecated
    @Override
    public boolean getSundamageInBiome(ResourceLocation registryName) {
        return !noSundamageBiomesIDs.contains(registryName) && !noSundamageConfiguredBiomesIDs.contains(registryName);
    }

    @Override
    public boolean getSundamageInDim(ResourceKey<Level> dim) {
        Boolean r = sundamageConfiguredDims.get(dim);
        if (r == null) {
            r = sundamageDims.get(dim);
        }
        return r == null ? VampirismConfig.SERVER.sundamageUnknownDimension.get() : r;
    }

    @Override
    public boolean isGettingSundamage(LivingEntity entity, LevelAccessor world) {
        return Helper.gettingSundamge(entity, world, null);
    }


    public void reloadConfiguration() {
        sundamageConfiguredDims.clear();
        List<? extends String> negative = VampirismConfig.SERVER.sundamageDimensionsOverrideNegative.get();
        for (String s : negative) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, id);
            sundamageConfiguredDims.put(key, false);
        }
        List<? extends String> positive = VampirismConfig.SERVER.sundamageDimensionsOverridePositive.get();
        for (String s : positive) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, id);
            sundamageConfiguredDims.put(key, true);
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
     */
    public void specifyConfiguredSundamageForDim(ResourceKey<Level> dimension, boolean sundamage) {
        sundamageConfiguredDims.put(dimension, sundamage);
    }

    @Override
    public void specifySundamageForDim(ResourceKey<Level> dimension, boolean sundamage) {
        sundamageDims.put(dimension, sundamage);
    }

}
