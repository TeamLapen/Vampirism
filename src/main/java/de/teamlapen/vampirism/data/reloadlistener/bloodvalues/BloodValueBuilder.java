package de.teamlapen.vampirism.data.reloadlistener.bloodvalues;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BloodValueBuilder {

    private final List<BuilderEntries> entries = new ArrayList<>();
    private final LegacyBuilderEntries legacyEntries = new LegacyBuilderEntries();

    public BloodValueBuilder() {
        this.entries.add(legacyEntries);
    }

    public void addFromFile(BuilderEntries proxy) {
        this.entries.add(proxy);
    }

    public void addValue(ResourceLocation id, float value, String sourceName) {
        this.legacyEntries.addEntry(new Proxy(new Entry(id, value), sourceName));
    }

    public @NotNull Map<ResourceLocation, Float> build() {
        List<BuilderEntries> entries = this.entries.stream().filter(BuilderEntries::isReplace).collect(Collectors.toList());
        if (entries.isEmpty()) {
            entries = this.entries;
        }
        return entries.stream().flatMap(e -> e.getEntries().stream()).collect(Collectors.toMap(e -> e.entry.id, e -> e.entry.value));
    }

    public static class BuilderEntries {

        protected final List<Proxy> entries;
        private final boolean replace;

        public BuilderEntries(List<Proxy> entries, boolean replace) {
            this.entries = entries;
            this.replace = replace;
        }

        public boolean isReplace() {
            return replace;
        }

        public List<Proxy> getEntries() {
            return entries;
        }
    }

    public static class LegacyBuilderEntries extends BuilderEntries {

        public LegacyBuilderEntries() {
            super(new ArrayList<>(), false);
        }

        public void addEntry(Proxy entry) {
            this.entries.add(entry);
        }
    }

    public static class Proxy {
        private final Entry entry;
        private final String source;

        public Proxy(Entry entry, String source) {
            this.entry = entry;
            this.source = source;
        }

        public Entry getEntry() {
            return entry;
        }

        @Override
        public @NotNull String toString() {
            return this.entry.toString() + " (from " + this.source + ")";
        }
    }

    public static class Entry {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(ResourceLocation.CODEC.fieldOf("id").forGetter(e -> e.id), Codec.FLOAT.fieldOf("value").forGetter(e -> e.value)).apply(instance, Entry::new);
        });
        private final ResourceLocation id;
        private final float value;

        public Entry(ResourceLocation id, float value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public @NotNull String toString() {
            return id + " : " + value;
        }
    }
}
