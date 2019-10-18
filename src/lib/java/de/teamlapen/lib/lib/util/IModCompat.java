package de.teamlapen.lib.lib.util;


/**
 * Handles compatibility for a single mod.
 * Should not load any classes outside of init
 */
public interface IModCompat extends IInitListener {
    String getModID();
}
