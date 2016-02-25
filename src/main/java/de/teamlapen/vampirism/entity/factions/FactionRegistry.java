package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FactionRegistry implements IFactionRegistry {
    private final static String TAG = "FactionRegistry";
    private List<Faction> temp = new ArrayList<Faction>();
    private Faction[] allFactions;
    private PlayableFaction[] playableFactions;
    private Map<Integer, Predicate<EntityLivingBase>> predicateMap = new HashMap<>();

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

    @Override
    public Predicate<EntityLivingBase> getPredicate(IFaction thisFaction, boolean player, boolean mob, boolean neutralPlayer, IFaction otherFaction) {
        int key = 0;
        if (otherFaction != null) {
            int id = ((Faction) thisFaction).getId();
            if (id > 63) {
                VampirismMod.log.w(TAG, "Faction id over 64, predicates won't work");
            }
            key |= ((id & 63) << 9);
        }
        if (neutralPlayer) {
            key |= (1 << 8);
        }
        if (mob) {
            key |= (1 << 7);
        }
        if (neutralPlayer) {
            key |= (1 << 6);
        }
        int id = ((Faction) thisFaction).getId();
        if (id > 64) {
            VampirismMod.log.w(TAG, "Faction id over 64, predicates won't work");
        }
        key |= id & 63;
        Integer k = Integer.valueOf(key);
        Predicate<EntityLivingBase> predicate;
        if (predicateMap.containsKey(k)) {
            predicate = predicateMap.get(k);
        } else {
            predicate = new PredicateFaction(thisFaction, player, mob, neutralPlayer, otherFaction);
            predicateMap.put(k, predicate);
        }
        VampirismMod.log.t("%s,%b,%b,%b,%s", thisFaction, player, mob, neutralPlayer, otherFaction);
        VampirismMod.log.t("%s", k);
        VampirismMod.log.t("%s", predicate);
        return predicate;
    }

    @Override
    public Predicate<EntityLivingBase> getPredicate(IFaction thisFaction) {

        return getPredicate(thisFaction, true, true, true, null);
    }

    @Override
    public <T extends IFactionEntity> IFaction registerFaction(String name, Class<T> entityInterface, int color) {
        if (!UtilLib.isNonNull(name, entityInterface)) {
            throw new IllegalArgumentException("[Vampirism]Parameter for faction cannot be null");
        }
        Faction<T> f = new Faction<>(name, entityInterface, color);
        addFaction(f);
        return f;
    }

    @Override
    public <T extends IFactionPlayer> IPlayableFaction registerPlayableFaction(String name, Class<T> entityInterface, int color, String playerProp, int highestLevel) {
        if (!UtilLib.isNonNull(name, entityInterface, playerProp)) {
            throw new IllegalArgumentException("[Vampirism]Parameter for faction cannot be null");
        }
        PlayableFaction<T> f = new PlayableFaction<>(name, entityInterface, color, playerProp, highestLevel);
        addFaction(f);
        return f;
    }

    private void addFaction(Faction faction) {
        if (temp == null) {
            throw new IllegalStateException(String.format("[Vampirism]You have to register factions BEFORE post init. (%s)", faction.name));
        } else {
            temp.add(faction);
        }
    }




}
