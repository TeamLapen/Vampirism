package de.teamlapen.vampirism.api.client;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.resources.Identifier;

/**
 * Holds keys to all vampirism ingame overlays
 */
public class VampirismOverlays {

    /**
     * Blood bar element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.VanillaGuiLayers#FOOD_LEVEL}, but the food rendering is canceled
     */
    public static final Identifier BLOOD_BAR_ID = VIdentifier.mod("blood_bar");


    /**
     * Action duration element
     * <br>
     * Is rendered in the lower right corner
     */
    public static final Identifier RAGE = VIdentifier.mod("rage");
    public static final Identifier BAT = VIdentifier.mod("bat");
    public static final Identifier DISGUISE = VIdentifier.mod("disguise");
    public static final Identifier SUN = VIdentifier.mod("sun");
    public static final Identifier FULL_SCREEN = VIdentifier.mod("fullscreen");
    public static final Identifier NEARBY_VAMPIRE = VIdentifier.mod("nearby_vampire");
    public static final Identifier ITEM_CHARGE = VIdentifier.mod("item_charge");
    public static final Identifier DRACULA_EVENT = VIdentifier.mod("dracula_event");
}
