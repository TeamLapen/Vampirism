package de.teamlapen.lib.lib.util;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

/**
 * Handles compatibility for a single mod.
 * Should not load any classes outside of init
 */
public interface IModCompat extends IInitListener {
    String getModID();

    void loadConfigs(Configuration config, ConfigCategory category);
}
