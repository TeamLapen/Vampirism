package de.teamlapen.vampirism.api.client;

import de.teamlapen.vampirism.api.util.VResourceLocation;
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
    public static final Identifier BLOOD_BAR_ID = VResourceLocation.mod("blood_bar");


    /**
     * Action duration element
     * <br>
     * Is rendered in the lower right corner
     */
    public static final Identifier RAGE = VResourceLocation.mod("rage");
    public static final Identifier BAT = VResourceLocation.mod("bat");
    public static final Identifier DISGUISE = VResourceLocation.mod("disguise");
    public static final Identifier SUN = VResourceLocation.mod("sun");
    public static final Identifier FULL_SCREEN = VResourceLocation.mod("fullscreen");
}
