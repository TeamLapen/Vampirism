package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraftforge.fml.common.FMLLog;

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
