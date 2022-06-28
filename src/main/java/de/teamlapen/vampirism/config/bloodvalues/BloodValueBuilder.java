package de.teamlapen.vampirism.config.bloodvalues;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodValueBuilder {

    private final List<Proxy> entries = new ArrayList<>();

    public BloodValueBuilder addFromJson(JsonObject json, String sourceName) {
        if(JSONUtils.getAsBoolean(json, "replace", false)) {
            this.entries.clear();
        }
        JsonObject values = json.getAsJsonObject("values");
        values.entrySet().stream().map(e -> {
            ResourceLocation loc = new ResourceLocation(e.getKey());
            float value = e.getValue().getAsFloat();
            return new Proxy(new Entry(loc, value), sourceName);
        }).forEach(this.entries::add);
        return this;
    }

    public BloodValueBuilder addValue(ResourceLocation id, float value, String sourceName) {
        this.entries.add(new Proxy(new Entry(id, value), sourceName));
        return this;
    }

    public Map<ResourceLocation, Float> build() {
        Map<ResourceLocation, Float> map = new HashMap<>();
        this.entries.forEach(a -> map.put(a.entry.id, a.entry.value));
        return map;
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
