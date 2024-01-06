package de.teamlapen.vampirism.api.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

/**
 * Holds all Vampirism {@link net.neoforged.neoforge.client.gui.overlay.IGuiOverlay} elements
 */
public class VIngameOverlays {

    /**
     * Blood bar element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay#FOOD_LEVEL}, but the food rendering is canceled
     */
    public static IGuiOverlay BLOOD_BAR_ELEMENT;
    public static final ResourceLocation BLOOD_BAR_ID = new ResourceLocation("vampirism", "blood_bar");


    /**
     * Faction raid bar element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay#BOSS_EVENT_PROGRESS}
     */
    public static IGuiOverlay FACTION_RAID_BAR_ELEMENT;
    public static final ResourceLocation FACTION_RAID_BAR_ID = new ResourceLocation("vampirism", "raid_bar");


    /**
     * Faction level element
     * <br>
     * Is rendered above {@link net.neoforged.neoforge.client.overlay.VanillaGuiOverlay#EXPERIENCE_BAR}
     */
    public static IGuiOverlay FACTION_LEVEL_ELEMENT;
    public static final ResourceLocation FACTION_LEVEL_ID = new ResourceLocation("vampirism", "faction_level");

    /**
     * Action cooldown element
     * <br>
     * Is rendered in the lower left corner
     */
    public static IGuiOverlay ACTION_COOLDOWN_ELEMENT;
    public static final ResourceLocation ACTION_COOLDOWN_ID = new ResourceLocation("vampirism", "action_cooldown");

    /**
     * Action duration element
     * <br>
     * Is rendered in the lower right corner
     */
    public static IGuiOverlay ACTION_DURATION_ELEMENT;
    public static final ResourceLocation ACTION_DURATION_ID = new ResourceLocation("vampirism", "action_duration");
}
