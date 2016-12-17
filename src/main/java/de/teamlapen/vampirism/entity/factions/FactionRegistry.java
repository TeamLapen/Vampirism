package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FactionRegistry implements IFactionRegistry {
    private final static String TAG = "FactionRegistry";
    private List<Faction> temp = new ArrayList<Faction>();
    private Faction[] allFactions;
    private PlayableFaction[] playableFactions;
    private Map<Integer, Predicate<Entity>> predicateMap = new HashMap<>();

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
    public
    @Nullable
    IFaction getFaction(Entity entity) {
        if (entity instanceof IFactionEntity) {
            return ((IFactionEntity) entity).getFaction();
        } else if (entity instanceof EntityPlayer) {
            return VampirismAPI.getFactionPlayerHandler(((EntityPlayer) entity)).getCurrentFaction();
        }
        return null;
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
    public Predicate<Entity> getPredicate(IFaction thisFaction, boolean ignoreDisguise) {

        return getPredicate(thisFaction, true, true, true, ignoreDisguise, null);
    }

    @Override
    public Predicate<Entity> getPredicate(IFaction thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, IFaction otherFaction) {
        int key = 0;
        if (otherFaction != null) {
            int id = ((Faction) thisFaction).getId();
            if (id > 63) {
                VampirismMod.log.w(TAG, "Faction id over 64, predicates won't work");
            }
            key |= ((id & 63) << 10);
        }
        if (neutralPlayer) {
            key |= (1 << 9);
        }
        if (mob) {
            key |= (1 << 8);
        }
        if (neutralPlayer) {
            key |= (1 << 7);
        }
        if (ignoreDisguise) {
            key |= (1 << 6);
        }
        int id = ((Faction) thisFaction).getId();
        if (id > 64) {
            VampirismMod.log.w(TAG, "Faction id over 64, predicates won't work");
        }
        key |= id & 63;
        Predicate<Entity> predicate;
        if (predicateMap.containsKey(key)) {
            predicate = predicateMap.get(key);
        } else {
            predicate = new PredicateFaction(thisFaction, player, mob, neutralPlayer, ignoreDisguise, otherFaction);
            predicateMap.put(key, predicate);
        }
//        VampirismMod.log.t("%s,%b,%b,%b,%s", thisFaction, player, mob, neutralPlayer, otherFaction);
//        VampirismMod.log.t("%s", k);
//        VampirismMod.log.t("%s", predicate);
        return predicate;
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
    public <T extends IFactionPlayer> IPlayableFaction registerPlayableFaction(String name, Class<T> entityInterface, int color, ResourceLocation key, Capability<T> playerCapabiltiy, int highestLevel) {
        if (!UtilLib.isNonNull(name, entityInterface, playerCapabiltiy)) {
            throw new IllegalArgumentException("[Vampirism]Parameter for faction cannot be null");
        }
        PlayableFaction<T> f = new PlayableFaction<>(name, entityInterface, color, key, playerCapabiltiy, highestLevel);
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
