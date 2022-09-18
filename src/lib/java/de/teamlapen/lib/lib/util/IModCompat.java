package de.teamlapen.lib.lib.util;


import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

/**
 * Handles compatibility for a single mod.
 * Should not load any classes outside of init
 * Updated copy of {@link de.teamlapen.lib.lib.util.IModCompat}
 * TODO remove 1.20
 */
@Deprecated(forRemoval = true)
public interface IModCompat extends IInitListener {
    void buildConfig(ForgeConfigSpec.Builder builder);

    /**
     * Can be null if all versions are accepted
     * {@link org.apache.maven.artifact.versioning.VersionRange} String
     */
    @SuppressWarnings("SameReturnValue")
    @Nullable
    default String getAcceptedVersionRange() {
        return null;
    }

    String getModID();
}