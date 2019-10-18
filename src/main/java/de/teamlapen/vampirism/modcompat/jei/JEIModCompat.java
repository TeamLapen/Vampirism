package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.IModCompat;

/**
 * JEI automatically detects the plugin class so nothing to do here
 */
public class JEIModCompat implements IModCompat {
    @Override
    public String getModID() {
        return "jei";
    }

}
