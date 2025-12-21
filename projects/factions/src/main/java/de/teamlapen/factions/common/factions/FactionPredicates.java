package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionPredicate;
import de.teamlapen.factions.api.factions.IFactionPredicates;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FactionPredicates implements IFactionPredicates, FactionPredicate.FactionPredicateLookup {

    private final FactionHelper factionRegistry;
    private final Map<Integer, FactionPredicate> predicates = new HashMap<>();

    public FactionPredicates(FactionHelper factionRegistry) {
        this.factionRegistry = factionRegistry;
    }

    public FactionPredicate.Builder builder() {
        return new FactionPredicate.Builder(null, this);
    }

    public FactionPredicate.Builder builder(Holder<? extends IFaction<?>> ownFaction) {
        return new FactionPredicate.Builder(ownFaction, this);
    }

    @Override
    public Registry<IFaction<?>> registry() {
        return ModRegistries.FACTIONS;
    }

    @Override
    public @Nullable IFactionPredicate getExisting(FactionPredicate.Builder builder) {
        return this.predicates.get(builder.hashCode());
    }

    @Override
    public void update(FactionPredicate.Builder builder, FactionPredicate predicate) {
        this.predicates.put(builder.hashCode(), predicate);
    }

    @Override
    public Holder<? extends IFaction<?>> getFallbackFaction(LivingEntity livingEntity) {
        return this.factionRegistry.getFallbackFaction(livingEntity);
    }
}
