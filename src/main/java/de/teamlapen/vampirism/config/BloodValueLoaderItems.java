package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import net.minecraft.util.ResourceLocation;

/**
 * Handle loading and saving of blood values for conversion of items to impure blood.
 */
public class BloodValueLoaderItems extends BloodValueLoader {
    private static BloodValueLoaderItems INSTANCE = new BloodValueLoaderItems();

    public static BloodValueLoaderItems getInstance() {
        return INSTANCE;
    }

    private BloodValueLoaderItems() {
        super("vampirism_blood_values/items", BloodConversionRegistry::applyNewItemResources, new ResourceLocation("multiplier"));
    }
}
