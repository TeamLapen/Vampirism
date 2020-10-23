package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SundamageRegistry implements ISundamageRegistry {
    private static final Logger LOGGER = LogManager.getLogger(SundamageRegistry.class);
    private final HashMap<RegistryKey<World>, Boolean> sundamageDims = new HashMap<>();
    private final HashMap<RegistryKey<World>, Boolean> sundamageConfiguredDims = new HashMap<>();
    private final Set<ResourceLocation> noSundamageBiomesIDs = new CopyOnWriteArraySet<>();
    private final Set<ResourceLocation> noSundamageConfiguredBiomesIDs = new CopyOnWriteArraySet<>();

    public SundamageRegistry() {
        sundamageDims.put(World.OVERWORLD, true);
        sundamageDims.put(World.THE_NETHER, false);
        sundamageDims.put(World.THE_END, false);
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
    public boolean getSundamageInDim(RegistryKey<World> dim) {
        Boolean r = sundamageConfiguredDims.get(dim);
        if (r == null) {
            r = sundamageDims.get(dim);
        }
        return r == null ? VampirismConfig.SERVER.sundamageUnknownDimension.get() : r;
    }

    @Override
    public boolean isGettingSundamage(LivingEntity entity, IWorld world) {
        return Helper.gettingSundamge(entity, world, null);
    }


    public void reloadConfiguration() {
        sundamageConfiguredDims.clear();
        List<? extends String> negative = VampirismConfig.SERVER.sundamageDimensionsOverrideNegative.get();
        for (String s : negative) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, id);
            sundamageConfiguredDims.put(key, false);
        }
        List<? extends String> positive = VampirismConfig.SERVER.sundamageDimensionsOverridePositive.get();
        for (String s : positive) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, id);
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
     *
     * @param dimension
     * @param sundamage
     */
    public void specifyConfiguredSundamageForDim(RegistryKey<World> dimension, boolean sundamage) {
        sundamageConfiguredDims.put(dimension, sundamage);
    }

    @Override
    public void specifySundamageForDim(RegistryKey<World> dimension, boolean sundamage) {
        sundamageDims.put(dimension, sundamage);
    }

}
