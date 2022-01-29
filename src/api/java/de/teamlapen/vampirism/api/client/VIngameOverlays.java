package de.teamlapen.vampirism.api.client;

import net.minecraftforge.client.gui.IIngameOverlay;

/**
 * Holds all Vampirism {@link IIngameOverlay} elements
 */
public class VIngameOverlays {

    /**
     * Blood bar element
     * <br>
     * Is rendered above {@link net.minecraftforge.client.gui.ForgeIngameGui#FOOD_LEVEL_ELEMENT}, but the food rendering is canceled
     */
    public static IIngameOverlay BLOOD_BAR_ELEMENT;

    /**
     * Faction raid bar element
     * <br>
     * Is rendered above {@link net.minecraftforge.client.gui.ForgeIngameGui#BOSS_HEALTH_ELEMENT}
     */
    public static IIngameOverlay FACTION_RAID_BAR_ELEMENT;

    /**
     * Faction level element
     * <br>
     * Is rendered above {@link net.minecraftforge.client.gui.ForgeIngameGui#EXPERIENCE_BAR_ELEMENT}
     */
    public static IIngameOverlay FACTION_LEVEL_ELEMENT;
}
