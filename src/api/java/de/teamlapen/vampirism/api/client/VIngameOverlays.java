package de.teamlapen.vampirism.api.client;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

/**
 * Holds all Vampirism {@link LayeredDraw.Layer} elements
 */
public class VIngameOverlays {

    /**
     * Blood bar element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.VanillaGuiLayers#FOOD_LEVEL}, but the food rendering is canceled
     */
    public static LayeredDraw.Layer BLOOD_BAR_ELEMENT;
    public static final ResourceLocation BLOOD_BAR_ID = VResourceLocation.mod("blood_bar");


    /**
     * Faction raid bar element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.VanillaGuiLayers#BOSS_OVERLAY}
     */
    public static LayeredDraw.Layer FACTION_RAID_BAR_ELEMENT;
    public static final ResourceLocation FACTION_RAID_BAR_ID = VResourceLocation.mod("raid_bar");


    /**
     * Faction level element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.VanillaGuiLayers#EXPERIENCE_BAR}
     */
    public static LayeredDraw.Layer FACTION_LEVEL_ELEMENT;
    public static final ResourceLocation FACTION_LEVEL_ID = VResourceLocation.mod("faction_level");

    /**
     * Action cooldown element
     * <br>
     * Is rendered in the lower left corner
     */
    public static LayeredDraw.Layer ACTION_COOLDOWN_ELEMENT;
    public static final ResourceLocation ACTION_COOLDOWN_ID = VResourceLocation.mod("action_cooldown");

    /**
     * Action duration element
     * <br>
     * Is rendered in the lower right corner
     */
    public static LayeredDraw.Layer ACTION_DURATION_ELEMENT;
    public static final ResourceLocation ACTION_DURATION_ID = VResourceLocation.mod("action_duration");
}
