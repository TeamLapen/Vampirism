package de.teamlapen.factions.api.event;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import de.teamlapen.factions.api.factions.IFaction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Objects;

public class AddFactionTagEvent extends Event implements IModBusEvent {


    private final ImmutableTable.Builder<Holder<? extends IFaction<?>>, ResourceKey<?>, TagKey<?>> tags = ImmutableTable.builder();

    private void addTag(Holder<? extends IFaction<?>> faction, ResourceKey<?> key, TagKey<?> tag) {
        this.tags.put(faction, key, tag);
    }

    public Builder faction(Holder<? extends IFaction<?>> faction) {
        return new Builder(faction);
    }

    public Table<Holder<? extends IFaction<?>>, ResourceKey<?>, TagKey<?>> create() {
        return this.tags.build();
    }

    public class Builder {

        private final Holder<? extends IFaction<?>> faction;

        public Builder(Holder<? extends IFaction<?>> faction) {
            this.faction = faction;
        }

        public <T> Builder addCustom(ResourceKey<T> key, TagKey<T> tag) {
            AddFactionTagEvent.this.addTag(faction, key, tag);
            return this;
        }

        public <T> Builder add(ResourceKey<? extends Registry<T>> key, TagKey<T> tag) {
            AddFactionTagEvent.this.addTag(faction, key, tag);
            return this;
        }
    }
}
