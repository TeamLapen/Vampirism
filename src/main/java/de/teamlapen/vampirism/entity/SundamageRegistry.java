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
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
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
    private final Set<Class> noSundamageBiomes = new CopyOnWriteArraySet<>();

    public SundamageRegistry() {
        sundamageDims.put(World.field_234918_g_, true);
        sundamageDims.put(World.field_234919_h_, false);
        sundamageDims.put(World.field_234920_i_, false);
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
            RegistryKey<World> key = RegistryKey.func_240903_a_(Registry.field_239699_ae_, id);
            sundamageConfiguredDims.put(key, false);
        }
        List<? extends String> positive = VampirismConfig.SERVER.sundamageDimensionsOverridePositive.get();
        for (String s : positive) {
            ResourceLocation id = new ResourceLocation(s); //Should be safe because config validates values?
            RegistryKey<World> key = RegistryKey.func_240903_a_(Registry.field_239699_ae_, id);
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
