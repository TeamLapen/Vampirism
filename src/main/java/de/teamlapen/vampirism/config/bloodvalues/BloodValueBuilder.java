package de.teamlapen.vampirism.config.bloodvalues;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

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

    public void addFromJson(JsonObject json, String sourceName) {
        JsonObject values = json.getAsJsonObject("values");
        this.entries.add( new BuilderEntries(values.entrySet().stream().map(e -> {
            ResourceLocation loc = new ResourceLocation(e.getKey());
            float value = e.getValue().getAsFloat();
            return new Proxy(new Entry(loc, value), sourceName);
        }).collect(Collectors.toList()), JSONUtils.getAsBoolean(json, "replace", false)));
    }

    public void addValue(ResourceLocation id, float value, String sourceName) {
        this.legacyEntries.addEntry(new Proxy(new Entry(id, value), sourceName));
    }

    public Map<ResourceLocation, Float> build() {
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
        public String toString() {
            return this.entry.toString() + " (from " + this.source + ")";
        }
    }

    public static class Entry {
        private final ResourceLocation id;
        private final float value;

        public Entry(ResourceLocation id, float value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public String toString() {
            return id + " : " + value;
        }
    }
}
