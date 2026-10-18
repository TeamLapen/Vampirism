package de.teamlapen.faction.common.factions;

import com.google.common.base.Suppliers;
import de.teamlapen.faction.api.FactionTagKeys;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPredicates;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.common.core.DefaultFactions;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class FactionPredicates implements IFactionPredicates, FactionPredicate.FactionPredicateLookup {

    private final FactionHelper factionHelper;
    private final FactionSpecificTags factionTags;
    /**
     * Resolved target faction sets. Tag based holder sets stay valid across tag reloads, so they never need to be invalidated
     */
    private final Map<FactionPredicate.TargetKey, Supplier<HolderSet<IFaction<?>>>> targets = new ConcurrentHashMap<>();
    private final Map<ResourceKey<IFaction<?>>, Supplier<Optional<TagKey<EntityType<?>>>>> defaultTargets = new ConcurrentHashMap<>();

    public FactionPredicates(FactionHelper factionHelper, FactionSpecificTags factionTags) {
        this.factionHelper = factionHelper;
        this.factionTags = factionTags;
    }

    @Override
    public FactionPredicate.Builder builder() {
        return new FactionPredicate.Builder(null, this);
    }

    @Override
    public FactionPredicate.Builder builder(Holder<? extends IFaction<?>> ownFaction) {
        return new FactionPredicate.Builder(ownFaction, this);
    }

    @Override
    public Supplier<HolderSet<IFaction<?>>> targetFactions(FactionPredicate.TargetKey key) {
        // resolved lazily, as predicates may be created before the registry is frozen
        return this.targets.computeIfAbsent(key, k -> Suppliers.memoize(() -> resolve(k)));
    }

    @Override
    public Supplier<Optional<TagKey<EntityType<?>>>> defaultTargets(@Nullable Holder<? extends IFaction<?>> faction) {
        if (faction == null) return Optional::empty;
        // resolved lazily, as faction tags are collected during common setup
        ResourceKey<IFaction<?>> key = SafeCast.cast(faction.unwrapKey().orElseThrow(() -> new IllegalArgumentException("Faction must be a registered faction")));
        return this.defaultTargets.computeIfAbsent(key, k -> Suppliers.memoize(() -> this.factionTags.getCustom(ModRegistries.FACTIONS.getOrThrow(k), FactionTagKeys.DEFAULT_TARGETS)));
    }

    @Override
    public Holder<? extends IFaction<?>> getFallbackFaction(LivingEntity livingEntity) {
        return this.factionHelper.getFallbackFaction(livingEntity);
    }

    private static HolderSet<IFaction<?>> resolve(FactionPredicate.TargetKey key) {
        Registry<IFaction<?>> registry = ModRegistries.FACTIONS;

        HolderSet<IFaction<?>> holderSet = key.targetTag() != null
                ? registry.getOrThrow(key.targetTag())
                : HolderSet.direct(key.targetFactions().stream().<Holder<IFaction<?>>>map(registry::getOrThrow).toList());

        if (key.sourceFaction() != null) {
            Holder<IFaction<?>> source = registry.getOrThrow(key.sourceFaction());
            List<Holder<IFaction<?>>> excluded = new ArrayList<>();
            if (!key.allowOwnFaction()) {
                excluded.add(source);
            }
            if (!IFaction.is(source, FactionTags.HOSTILE_TOWARDS_NEUTRAL)) {
                excluded.add(SafeCast.<Holder<IFaction<?>>>cast(DefaultFactions.NEUTRAL).getDelegate());
            }
            if (!excluded.isEmpty()) {
                holderSet = new AndHolderSet<>(holderSet, new NotHolderSet<>(registry, HolderSet.direct(excluded)));
            }
        }

        return holderSet;
    }
}
