package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Handle loading and saving of blood values for conversion of fluid to blood.
 */
public class BloodValueLoaderFluids extends BloodValueLoader {
    private static BloodValueLoaderFluids INSTANCE = new BloodValueLoaderFluids();

    public static BloodValueLoaderFluids getInstance() {
        return INSTANCE;
    }

    private int divider;

    private BloodValueLoaderFluids() {
        super("vampirism_blood_values/fluids", BloodConversionRegistry::registerFluids, new ResourceLocation("divider"));
    }

    @Override
    protected void apply(@Nonnull Object splashList, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {
        BloodConversionRegistry.prepareFluids();
        super.apply(splashList, resourceManagerIn, profilerIn);
        BloodConversionRegistry.processFluids(divider);
    }

    @Override
    protected void handleMultiplier(int multiplier) {
        divider = multiplier;
    }
}
