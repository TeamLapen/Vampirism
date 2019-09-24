package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.lib.lib.config.BloodValueLoaderDynamic;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BloodValues {

    public static final BloodValueLoaderDynamic ENTITIES;
    public static final BloodValueLoaderDynamic ITEMS;
    public static final BloodValueLoader FLUIDS;

    public static List<BloodValueLoaderDynamic> getDynamicLoader() {
        return BloodValueLoaderDynamic.getDynamicBloodLoader();
    }

    static {
        ENTITIES = new BloodValueLoaderDynamic(REFERENCE.MODID, "entities", (values, multiplier) -> ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(values, multiplier), new ResourceLocation("multiplier"), VampirismEntityRegistry.biteableEntryManager::addCalculated, VampirismEntityRegistry.biteableEntryManager::getValuesToSave);
        ITEMS = new BloodValueLoaderDynamic(REFERENCE.MODID, "items", BloodConversionRegistry::applyNewItemResources, new ResourceLocation("multiplier"), BloodConversionRegistry::applyNewItemCalculated, BloodConversionRegistry::getItemValuesCalculated);
        FLUIDS = new BloodValueLoader("fluids", BloodConversionRegistry::applyNewFluidResources, new ResourceLocation("divider"));
    }
}
