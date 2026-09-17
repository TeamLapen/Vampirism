package de.teamlapen.faction.common.factions;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import de.teamlapen.faction.api.event.AddFactionTagEvent;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionSpecificTags;
import de.teamlapen.faction.api.util.SafeCast;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;

public class FactionSpecificTags implements IFactionSpecificTags {

    @UnknownNullability
    private Table<Holder<? extends IFaction<?>>, ResourceKey<?>, TagKey<?>> tags;

    @Override
    public <T> Optional<TagKey<T>> getCustom(Holder<? extends IFaction<?>> faction, ResourceKey<T> key) {
        return SafeCast.cast(Optional.ofNullable(this.tags.get(faction.getDelegate(), key)));
    }

    @Override
    public <T> TagKey<T> getCustom(Holder<? extends IFaction<?>> faction, ResourceKey<T> key, TagKey<T> fallback) {
        var tag = this.tags.get(faction.getDelegate(), key);
        if (tag == null) return fallback;
        return SafeCast.cast(tag);
    }

    @Override
    public <T> Optional<TagKey<T>> get(Holder<? extends IFaction<?>> faction, ResourceKey<? extends Registry<T>> key) {
        return SafeCast.cast(Optional.ofNullable(this.tags.get(faction.getDelegate(), key)));
    }

    @Override
    public <T> TagKey<T> get(Holder<? extends IFaction<?>> faction, ResourceKey<? extends Registry<T>> key, TagKey<T> fallback) {
        var tag = this.tags.get(faction.getDelegate(), key);
        if (tag == null) return fallback;
        return SafeCast.cast(tag);
    }

    @Unmodifiable
    @Override
    public <T> Map<Holder<? extends IFaction<?>>, TagKey<T>> allCustom(ResourceKey<T> key) {
        return SafeCast.cast(this.tags.column(key));
    }

    @Unmodifiable
    @Override
    public <T> Map<Holder<? extends IFaction<?>>, TagKey<T>> all(ResourceKey<? extends Registry<T>> key) {
        return SafeCast.cast(this.tags.column(key));
    }

    @ApiStatus.Internal
    public void collectTags() {
        // registries are frozen at this point. Key the table by the registry holders, as DeferredHolder and Holder.Reference have different hashCodes
        ImmutableTable.Builder<Holder<? extends IFaction<?>>, ResourceKey<?>, TagKey<?>> builder = ImmutableTable.builder();
        ModLoader.postEventWithReturn(new AddFactionTagEvent()).create().cellSet().forEach(cell -> builder.put(cell.getRowKey().getDelegate(), cell.getColumnKey(), cell.getValue()));
        this.tags = builder.build();
    }

}
