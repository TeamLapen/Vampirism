package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SundamageRegistry implements ISundamageRegistry {
    private static final String TAG = "SundamageRegistry";
    private HashMap<Integer, Boolean> sundamageDims = new HashMap<>();
    private HashMap<Integer, Boolean> sundamageConfiguredDims = new HashMap<>();
    private Set<ResourceLocation> noSundamageBiomesIDs = new CopyOnWriteArraySet<>();
    private Set<ResourceLocation> noSundamageConfiguredBiomesIDs = new CopyOnWriteArraySet<>();
    private Set<Class> noSundamageBiomes = new CopyOnWriteArraySet<>();
    private boolean defaultSundamage = false;

    public SundamageRegistry() {
        sundamageDims.put(0, true);
        sundamageDims.put(-1, false);
        sundamageDims.put(1, false);
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
            addNoSundamageBiome(b.getBiomeClass());
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
            if (clazz.isAssignableFrom(biome.getBiomeClass())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean getSundamageInDim(int dim) {
        Boolean r = sundamageConfiguredDims.get(dim);
        if (r == null) {
            r = sundamageDims.get(dim);
        }
        return r == null ? defaultSundamage : r;
    }

    @SideOnly(Side.CLIENT)
    public void readFromNBTClient(NBTTagCompound nbt) {
        if (nbt.hasKey("sundamage")) {
            NBTTagCompound sundamage = nbt.getCompoundTag("sundamage");
            defaultSundamage = sundamage.getBoolean("default");
            sundamageConfiguredDims.clear();
            NBTTagCompound dimensions = sundamage.getCompoundTag("dimensions");
            for (String s : dimensions.getKeySet()) {
                try {
                    int dim = Integer.parseInt(s);
                    boolean value = sundamage.getBoolean(s);
                    specifyConfiguredSundamageForDim(dim, value);
                } catch (NumberFormatException e) {
                    VampirismMod.log.e(TAG, "Failed to parse dimension id (%s) in update packet ", s);
                }
            }
            noSundamageConfiguredBiomesIDs.clear();
            NBTTagCompound biomes = sundamage.getCompoundTag("biomes");
            for (String s : biomes.getKeySet()) {
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
     * @param dimensionId
     * @param sundamage
     */
    public void specifyConfiguredSundamageForDim(int dimensionId, boolean sundamage) {
        sundamageConfiguredDims.put(dimensionId, sundamage);
    }

    @Override
    public void specifySundamageForDim(int dimensionId, boolean sundamage) {
        sundamageDims.put(dimensionId, sundamage);
    }

    public void writeToNBTServer(NBTTagCompound nbt) {
        NBTTagCompound sundamage = new NBTTagCompound();
        NBTTagCompound dimensions = new NBTTagCompound();
        for (Map.Entry<Integer, Boolean> entry : sundamageConfiguredDims.entrySet()) {
            dimensions.setBoolean(entry.getKey().toString(), entry.getValue());
        }
        sundamage.setTag("dimensions", dimensions);
        NBTTagCompound biomes = new NBTTagCompound();
        for (ResourceLocation s : noSundamageConfiguredBiomesIDs) {
            biomes.setBoolean(s.toString(), true);
        }
        sundamage.setTag("biomes", biomes);
        sundamage.setBoolean("default", defaultSundamage);
        nbt.setTag("sundamage", sundamage);
    }

    @Override
    public boolean isGettingSundamage(EntityLivingBase entity) {
        return Helper.gettingSundamge(entity);
    }
}
