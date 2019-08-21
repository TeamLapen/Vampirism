package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Handle loading and saving of blood values for conversion of items to impure blood.
 */
public class BloodValueLoaderItems extends BloodValueLoader {
    private static BloodValueLoaderItems INSTANCE = new BloodValueLoaderItems();

    public static BloodValueLoaderItems getInstance() {
        return INSTANCE;
    }

    private int multiplier;

    private BloodValueLoaderItems() {
        super("vampirism_blood_values/items", BloodConversionRegistry::registerItems, new ResourceLocation("multiplier"));
    }

    @Override
    protected void apply(@Nonnull Object splashList, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {
        BloodConversionRegistry.prepareItems();
        super.apply(splashList, resourceManagerIn, profilerIn);
        BloodConversionRegistry.processItems(BloodValueLoaderItems.getInstance().getMultiplier());
    }

    @Override
    protected void handleMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
