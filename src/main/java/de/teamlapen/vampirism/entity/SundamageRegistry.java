package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SundamageRegistry implements ISundamageRegistry {
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
}
