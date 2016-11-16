package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SundamageRegistry implements ISundamageRegistry {
    private static final String TAG = "SundamageRegistry";
    private HashMap<Integer, Boolean> sundamageDims = new HashMap<>();
    private HashMap<Integer, Boolean> sundamageConfiguredDims = new HashMap<>();
    private Set<ResourceLocation> noSundamageBiomes = new CopyOnWriteArraySet<>();
    private boolean defaultSundamage = false;

    public SundamageRegistry() {
        sundamageDims.put(0, true);
        sundamageDims.put(-1, false);
        sundamageDims.put(1, false);
    }

    @Override
    public void addNoSundamageBiome(ResourceLocation registryName) {
        noSundamageBiomes.add(registryName);
    }

    @Override
    public boolean getSundamageInBiome(ResourceLocation registryName) {
        return !noSundamageBiomes.contains(registryName);
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
            sundamage.removeTag("default");
            sundamageConfiguredDims.clear();
            for (String s : sundamage.getKeySet()) {
                try {
                    int dim = Integer.parseInt(s);
                    boolean value = sundamage.getBoolean(s);
                    specifyConfiguredSundamageForDim(dim, value);
                } catch (NumberFormatException e) {
                    VampirismMod.log.e(TAG, "Failed to parse dimension id (%s) in update packet ", s);
                }
            }
        }
    }

    /**
     * Resets the configured sundamage dims. E.G. on configuration reload
     */
    public void resetConfiguredSundamgeDims() {
        sundamageConfiguredDims.clear();
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

    @SideOnly(Side.SERVER)
    public void writeToNBTServer(NBTTagCompound nbt) {
        NBTTagCompound sundamage = new NBTTagCompound();
        for (Map.Entry<Integer, Boolean> entry : sundamageConfiguredDims.entrySet()) {
            sundamage.setBoolean(entry.getKey().toString(), entry.getValue());
        }
        sundamage.setBoolean("default", defaultSundamage);
        nbt.setTag("sundamage", sundamage);
    }
}
