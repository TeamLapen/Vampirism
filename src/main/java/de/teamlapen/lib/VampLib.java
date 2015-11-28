package de.teamlapen.lib;

import de.teamlapen.lib.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * If the package is moved as own mod (probably refractored with a different package name to avoid conflicts) this will be the mod main class.
 */
public class VampLib {
    public final static Logger log=new Logger("TeamLapenLib","de.teamlapen.lib");

    /**
     * Mod id (currently just the vampirism one).
     * Used to access resources etc
     */
    public final static String MODID=REFERENCE.MODID;
}
