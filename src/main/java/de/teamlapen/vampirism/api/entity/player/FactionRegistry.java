package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.Faction;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Faction registry.
 * Register all extended properties that extend {@link IFactionPlayer} here
 * Currently only used for managing IPlayerEventListeners.
 */
public class FactionRegistry {
    private static List<Faction> temp = new ArrayList<Faction>();
    private static Faction[] allFactions;
    private static PlayableFaction[] playableFactions;


    /**
     * Registers a faction
     * Call before PostInit
     */
    public static void addFaction(Faction faction) {
        if (temp == null) {
            FMLLog.severe("[VampirismApi] You have to register factions BEFORE post init. (" + faction.name + ")");
        } else {
            temp.add(faction);
        }
    }

    /**
     * Returns the fraction player in which the player has a level above zero which means he is part of that faction.
     *
     * @param player
     * @return Can be null if the player does not belong to any faction
     */
    @Nullable
    public static IFactionPlayer getActiveFactionPlayer(EntityPlayer player) {
        for (PlayableFaction f : playableFactions) {
            IFactionPlayer player1 = f.getProp(player);
            if (player1.getLevel() > 0) return player1;
        }
        return null;
    }

    /**
     * Returns the fraction in which the player has a level above zero which means he is part of that faction.
     *
     * @param player
     * @return Can be null if the player does not belong to any faction
     */
    public static PlayableFaction getActiveFaction(EntityPlayer player) {
        for (PlayableFaction f : playableFactions) {
            IFactionPlayer player1 = f.getProp(player);
            if (player1.getLevel() > 0) return f;
        }
        return null;
    }

    /**
     * Finishes registrations during post init.
     * FOR INTERNAL USAGE ONLY
     */
    public static void finish() {
        allFactions = temp.toArray(new Faction[temp.size()]);
        temp = null;
        List<PlayableFaction> temp2 = new ArrayList<>();
        for (int i = 0; i < allFactions.length; i++) {
            if (allFactions[i] instanceof PlayableFaction) {
                temp2.add((PlayableFaction) allFactions[i]);
            }
        }
        playableFactions = temp2.toArray(new PlayableFaction[temp2.size()]);
    }

    public static Faction[] getFactions() {
        return allFactions;
    }

    public static PlayableFaction[] getPlayableFactions() {
        return playableFactions;
    }


}
