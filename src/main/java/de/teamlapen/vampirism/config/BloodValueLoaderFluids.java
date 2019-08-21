package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import net.minecraft.util.ResourceLocation;

/**
 * Handle loading and saving of blood values for conversion of fluid to blood.
 */
public class BloodValueLoaderFluids extends BloodValueLoader {
    private static BloodValueLoaderFluids INSTANCE = new BloodValueLoaderFluids();

    public static BloodValueLoaderFluids getInstance() {
        return INSTANCE;
    }

    private BloodValueLoaderFluids() {
        super("vampirism_blood_values/fluids", BloodConversionRegistry::applyNewFluidResources, new ResourceLocation("divider"));
    }
}
