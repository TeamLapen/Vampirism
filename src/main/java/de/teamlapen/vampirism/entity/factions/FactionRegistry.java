package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static net.minecraft.world.biome.Biome.LOGGER;


public class FactionRegistry implements IFactionRegistry {
    private List<Faction> temp = new CopyOnWriteArrayList<>(); //Copy on write is costly, but we only expect very few elements anyway
    private Faction[] allFactions;
    private PlayableFaction[] playableFactions;
    private Map<Integer, Predicate<LivingEntity>> predicateMap = new HashMap<>();

    /**
     * Finishes registrations during InterModProcessEvent
     */
    public void finish() {
        allFactions = temp.toArray(new Faction[0]);
        temp = null;
        List<PlayableFaction> temp2 = new ArrayList<>();
        for (Faction allFaction : allFactions) {
            if (allFaction instanceof PlayableFaction) {
                temp2.add((PlayableFaction) allFaction);
            }
        }
        playableFactions = temp2.toArray(new PlayableFaction[0]);
    }

    @Override
    public
    @Nullable
    IFaction getFaction(Entity entity) {
        if (entity instanceof IFactionEntity) {
            return ((IFactionEntity) entity).getFaction();
        } else if (entity instanceof PlayerEntity) {
            return entity.isAlive() ? VampirismAPI.getFactionPlayerHandler(((PlayerEntity) entity)).getCurrentFaction() : null;
        }
        return null;
    }

    @Nullable
    @Override
    public IFaction getFactionByID(ResourceLocation id) {
        if (allFactions == null) {
            return null;
        }
        for (IFaction f : allFactions) {
            if (f.getID().equals(id)) {
                return f;
            }
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
    public Predicate<LivingEntity> getPredicate(IFaction thisFaction, boolean ignoreDisguise) {

        return getPredicate(thisFaction, true, true, true, ignoreDisguise, null);
    }

    @Override
    public Predicate<LivingEntity> getPredicate(IFaction thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, IFaction otherFaction) {
        int key = 0;
        if (otherFaction != null) {
            int id = otherFaction.hashCode();
            if (id > 63) {
                LOGGER.warn("Faction id over 64, predicates won't work");
            }
            key |= ((id & 63) << 10);
        }
        if (player) {
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
        int id = thisFaction.hashCode();
        if (id > 64) {
            LOGGER.warn("Faction id over 64, predicates won't work");
        }
        key |= id & 63;
        Predicate<LivingEntity> predicate;
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
    public <T extends IFactionEntity> IFaction registerFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral) {
        if (!UtilLib.isNonNull(id, entityInterface)) {
            throw new IllegalArgumentException("[Vampirism]Parameter for faction cannot be null");
        }
        Faction<T> f = new Faction<>(id, entityInterface, color, hostileTowardsNeutral);
        addFaction(f);
        return f;
    }

    @Override
    public <T extends IFactionPlayer> IPlayableFaction registerPlayableFaction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, NonNullSupplier<Capability<T>> playerCapabilitySupplier, int highestLevel) {
        if (!UtilLib.isNonNull(id, entityInterface, playerCapabilitySupplier)) {
            throw new IllegalArgumentException("[Vampirism]Parameters for faction cannot be null");
        }

        PlayableFaction<T> f = new PlayableFaction<>(id, entityInterface, color, hostileTowardsNeutral, playerCapabilitySupplier, highestLevel);
        addFaction(f);
        return f;
    }

    @ThreadSafeAPI
    private void addFaction(Faction faction) {
        if (temp == null) {
            throw new IllegalStateException(String.format("[Vampirism]You have to register factions during InterModEnqueueEvent. (%s)", faction.getID()));
        } else {
            temp.add(faction);
        }
    }


}
