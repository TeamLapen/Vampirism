package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.resources.Identifier;

public class VReference {
    public static final String MODID = "vampirism";

    public static final Identifier VAMPIRE_FACTION_ID = VIdentifier.mod("vampire");
    public static final Identifier HUNTER_FACTION_ID = VIdentifier.mod("hunter");

    /**
     * One blood in the players blood stats represents this amount of mB fluid blood
     */
    public static final int FOOD_TO_FLUID_BLOOD = 100;

}
