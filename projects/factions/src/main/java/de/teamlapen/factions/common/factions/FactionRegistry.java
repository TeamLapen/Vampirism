package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.IFactionRegistry;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.core.DefaultFactions;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.minions.MinionEntryCallbacks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class FactionRegistry implements IFactionRegistry {
    private final Map<Integer, Predicate<LivingEntity>> predicateMap = new HashMap<>();

    private final MinionEntryCallbacks minionEntryCallbacks = new MinionEntryCallbacks();

    @Override
    public Holder<? extends IFaction<?>> getFaction(Entity entity) {
        if (entity instanceof Player player) {
            return getFaction(player);
        } else if (entity instanceof IFactionEntity factionEntity) {
            return factionEntity.getFaction();
        }
        return ModRegistries.FACTIONS.listElements().map(s -> (Holder<IFaction<?>>)s).filter(s -> s.value().getTag(Registries.ENTITY_TYPE).flatMap(BuiltInRegistries.ENTITY_TYPE::get).filter(tag -> entity.getType().is(tag)).isPresent()).findFirst().orElse((Holder<IFaction<?>>) (Object) DefaultFactions.NEUTRAL);
    }

    public Holder<? extends IPlayableFaction<?>> getFaction(Player player) {
        return FactionPlayerHandler.get(player).getFaction();
    }

    @Override
    public Collection<Holder<? extends IFaction<?>>> factions() {
        return ModRegistries.FACTIONS.listElements().collect(Collectors.toList());
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    @Override
    public List<Holder<? extends IPlayableFaction<?>>> playableFactions() {
        return ModRegistries.FACTIONS.listElements().filter(s -> s.value() instanceof IPlayableFaction<?>).map(s -> ((Holder<? extends IPlayableFaction<?>>) (Object) s)).collect(Collectors.toList());
    }

    public Predicate<LivingEntity> getPredicate(Holder<? extends IFaction<?>> thisFaction, boolean ignoreDisguise) {
        return getPredicate(thisFaction, true, true, true, ignoreDisguise, null);
    }

    @Override
    public Predicate<LivingEntity> getPredicate(Holder<? extends IFaction<?>> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable Holder<? extends IFaction<?>> otherFaction) {
        var hash = Objects.hash(thisFaction.unwrapKey().orElseThrow(), player, mob, neutralPlayer, ignoreDisguise, otherFaction == null ? null : otherFaction.unwrapKey().orElseThrow());
        return predicateMap.computeIfAbsent(hash, (k) -> new FactionPredicate.Builder(thisFaction).player().nonPlayer(mob).neutral(neutralPlayer).ignoreDisguise(ignoreDisguise).targetFaction(otherFaction).build());
    }

    @Override
    public boolean isEntityOfFaction(Entity entity, Holder<? extends IFaction<?>> faction) {
        return IFaction.is(getFaction(entity), faction);
    }

    @Override
    public boolean isEntityOfFaction(Player entity, Holder<? extends IFaction<?>> faction) {
        return IFaction.is(getFaction(entity), faction);
    }

    @Override
    public Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> getFactionMinionEntries() {
        return this.minionEntryCallbacks.getEntries();
    }

}
