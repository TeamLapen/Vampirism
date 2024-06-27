package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import net.neoforged.neoforge.registries.callback.ClearCallback;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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
        return ModRegistries.FACTIONS.holders().filter(s -> s.value().getTag(Registries.ENTITY_TYPE).flatMap(BuiltInRegistries.ENTITY_TYPE::getTag).filter(tag -> entity.getType().is(tag)).isPresent()).findFirst().orElse(null);
    }

    @Nullable
    public Holder<? extends IFaction<?>> getFaction(Player player) {
        return FactionPlayerHandler.get(player).getFaction();
    }

    @Override
    public Collection<Holder<? extends IFaction<?>>> factions() {
        return ModRegistries.FACTIONS.holders().collect(Collectors.toList());
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    @Override
    public List<Holder<? extends IPlayableFaction<?>>> playableFactions() {
        return ModRegistries.FACTIONS.holders().filter(s -> s.value() instanceof IPlayableFaction<?>).map(s -> ((Holder<? extends IPlayableFaction<?>>) (Object) s)).collect(Collectors.toList());
    }

    public Predicate<LivingEntity> getPredicate(@NotNull Holder<? extends IFaction<?>> thisFaction, boolean ignoreDisguise) {
        return getPredicate(thisFaction, true, true, true, ignoreDisguise, null);
    }

    @Override
    public Predicate<LivingEntity> getPredicate(@NotNull Holder<? extends IFaction<?>> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable Holder<? extends IFaction<?>> otherFaction) {
        var hash = Objects.hash(thisFaction.unwrapKey().orElseThrow(), player, mob, neutralPlayer, ignoreDisguise, otherFaction == null ? null : otherFaction.unwrapKey().orElseThrow());
        return predicateMap.computeIfAbsent(hash, (k) -> new FactionPredicate.Builder(thisFaction).player().nonPlayer(mob).neutral(neutralPlayer).ignoreDisguise(ignoreDisguise).targetFaction(otherFaction).build());
    }

    @Override
    public @Nullable IMinionEntry<?,?> getMinion(ResourceLocation minionId) {
        return ModRegistries.MINIONS.get(minionId);
    }

    @Override
    public @NotNull Collection<IMinionEntry<?,?>> getMinions() {
        return ModRegistries.MINIONS.stream().toList();
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
        return this.minionEntryCallbacks.entries;
    }

    @ApiStatus.Internal
    public RegistryCallback<IMinionEntry<?, ?>> getMinionCallback() {
        return this.minionEntryCallbacks;
    }

    private static class MinionEntryCallbacks implements AddCallback<IMinionEntry<?, ?>>, ClearCallback<IMinionEntry<?, ?>>, BakeCallback<IMinionEntry<?, ?>> {

        private final Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?,?>>,IMinionEntry<?, ?>>>> tmpEntries = new HashMap<>();
        private Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?,?>>, IMinionEntry<?, ?>>>> entries = ImmutableMap.of();

        @Override
        public void onAdd(Registry<IMinionEntry<?, ?>> registry, int id, ResourceKey<IMinionEntry<?, ?>> key, IMinionEntry<?, ?> value) {
            this.tmpEntries.computeIfAbsent(value.faction(), k -> new ArrayList<>()).add(Pair.of(key, value));
        }

        @Override
        public void onBake(Registry<IMinionEntry<?, ?>> registry) {
            ImmutableMap.Builder<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?,?>>,IMinionEntry<?, ?>>>> builder =ImmutableMap.builder();
            for (Map.Entry<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?,?>>,IMinionEntry<?, ?>>>> holderListEntry : this.tmpEntries.entrySet()) {
                builder.put(holderListEntry.getKey(), List.copyOf(holderListEntry.getValue()));
            }
            this.entries = builder.build();
        }

        @Override
        public void onClear(Registry<IMinionEntry<?, ?>> registry, boolean full) {
            if (full) {
                this.tmpEntries.clear();
                this.entries = ImmutableMap.of();
            }
        }
    }

}
