package de.teamlapen.lib.lib.util;


import de.teamlapen.lib.lib.config.forge.ConfigCategory;
import de.teamlapen.lib.lib.config.forge.Configuration;

/**
 * Handles compatibility for a single mod.
 * Should not load any classes outside of init
 */
public interface IModCompat extends IInitListener {
    String getModID();

    void loadConfigs(Configuration config, ConfigCategory category);
}
