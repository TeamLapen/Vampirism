package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.Faction;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;

import java.util.ArrayList;
import java.util.List;


public class FactionRegistry implements IFactionRegistry {
    private final static String TAG = "FactionRegistry";
    private List<Faction> temp = new ArrayList<Faction>();
    private Faction[] allFactions;
    private PlayableFaction[] playableFactions;

    @Override
    public void addFaction(Faction faction) {
        if (temp == null) {
            VampirismMod.log.e(TAG, "You have to register factions BEFORE post init. (%s)", faction.name);
        } else {
            temp.add(faction);
        }
    }


    /**
     * Finishes registrations during post init.
     */
    public void finish() {
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

    @Override
    public Faction[] getFactions() {
        return allFactions;
    }

    @Override
    public PlayableFaction[] getPlayableFactions() {
        return playableFactions;
    }


}
