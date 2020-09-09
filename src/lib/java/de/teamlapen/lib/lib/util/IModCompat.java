package de.teamlapen.lib.lib.util;


import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

/**
 * Handles compatibility for a single mod.
 * Should not load any classes outside of init
 * Updated copy of {@link de.teamlapen.lib.lib.util.IModCompat}
 */
public interface IModCompat extends IInitListener {
    void buildConfig(ForgeConfigSpec.Builder builder);

    String getModID();

    /**
     * Can be null if all versions are accepted
     * {@link net.minecraftforge.fml.common.versioning.VersionRange} String
     */
    @Nullable
    default String getAcceptedVersionRange() {
        return null;
    }
}