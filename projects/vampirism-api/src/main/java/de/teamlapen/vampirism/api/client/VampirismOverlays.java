package de.teamlapen.vampirism.api.client;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Holds keys to all vampirism ingame overlays
 */
public class VampirismOverlays {

    /**
     * Blood bar element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.VanillaGuiLayers#FOOD_LEVEL}, but the food rendering is canceled
     */
    public static final ResourceLocation BLOOD_BAR_ID = VResourceLocation.mod("blood_bar");


    /**
     * Action duration element
     * <br>
     * Is rendered in the lower right corner
     */
    public static final ResourceLocation RAGE = VResourceLocation.mod("rage");
    public static final ResourceLocation BAT = VResourceLocation.mod("bat");
    public static final ResourceLocation DISGUISE = VResourceLocation.mod("disguise");
    public static final ResourceLocation SUN = VResourceLocation.mod("sun");
}
