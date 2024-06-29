package de.teamlapen.vampirism.api.registries;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class DeferredFaction<Z extends IFactionEntity, L extends IFaction<Z>> extends DeferredHolder<L, L> {

    protected DeferredFaction(ResourceKey<L> key) {
        super(key);
    }

    public <T extends IFaction<?>> boolean match(@Nullable Holder<T> otherFaction) {
        return IFaction.is(this, otherFaction);
    }

    @SuppressWarnings("unchecked")
    public ResourceKey<IFaction<?>> getRawKey() {
        return (ResourceKey<IFaction<?>>) super.getKey();
    }

    public static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> createFaction(ResourceKey<L> key) {
        return new DeferredFaction<>(key);
    }

    @SuppressWarnings("unchecked")
    public static <Z extends IFactionEntity, L extends IFaction<Z>> DeferredFaction<Z, L> createFaction(ResourceLocation key) {
        return createFaction((ResourceKey<L>) ResourceKey.create(VampirismRegistries.Keys.FACTION, key));
    }
}
