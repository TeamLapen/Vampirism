package de.teamlapen.factions.api.factions;

import com.google.gson.Gson;
import de.teamlapen.factions.api.Factions;
import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record LevelingChange(Map<Key<?>, Change<?>> values, boolean notifyFactions) {

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        return (T) values.get(key);
    }

    public LevelingChange(Map<Key<?>, Change<?>> values) {
        this(values, true);
    }
    public LevelingChange(List<Change<?>> changes) {
        this(changes.stream().collect(Collectors.toMap(Change::key, Function.identity())));
    }

    public Holder<? extends IPlayableFaction<?>> getNewFaction() {
        return getNewFaction(Factions.NEUTRAL);
    }

    public Holder<? extends IPlayableFaction<?>> getNewFaction(Holder<? extends IPlayableFaction<?>> fallback) {
        FactionChange factionChange = get(FactionChange.KEY);
        return factionChange == null ? fallback : factionChange.newFaction();
    }

    public boolean hasFactionChange() {
        return get(FactionChange.KEY) != null;
    }

    public int getNewLevel() {
        return getNewLevel(0);
    }

    public int getNewLevel(int fallback) {
        LevelChange factionChange = get(LevelChange.KEY);
        return factionChange == null ? fallback : factionChange.newLevel();
    }

    public boolean hasLevelChange() {
        return get(LevelChange.KEY) != null;
    }

    public int getNewLordLevel() {
        return getNewLordLevel(0);
    }

    public int getNewLordLevel(int fallback) {
        LordLevelChange factionChange = get(LordLevelChange.KEY);
        return factionChange == null ? fallback : factionChange.newLevel();
    }

    public boolean hasLordLevelChange() {
        return get(LordLevelChange.KEY) != null;
    }

    public String toJson() {
        return "{" +
                this.values.entrySet().stream().map(x -> "\"" + x.getKey().id + "\":" + x.getValue().toJson()).collect(Collectors.joining(",")) +
                "}";
    }

    public Builder copy() {
        var builder = new Builder();
        this.values.forEach((x, y) -> builder.add(y));
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LevelingChange neutral() {
        return new Builder()
                .faction(Factions.NEUTRAL)
                .level(0)
                .build();
    }

    public static Builder maxLevel(Holder<? extends IPlayableFaction<?>> faction) {
        return new Builder()
                .faction(faction)
                .level(faction.value().getHighestReachableLevel());
    }

    public static Builder maxLord(Holder<? extends IPlayableFaction<?>> faction) {
        return maxLevel(faction)
                .lordLevel(faction.value().getHighestLordLevel());
    }

    public static class Builder {
        private final Map<Key<?>, Change<?>> changes = new HashMap<>();
        private boolean notifyFactions = true;

        public Builder skipFactionNotification() {
            this.notifyFactions = false;
            return this;
        }

        public Builder add(Change<?> change) {
            this.changes.put(change.key(), change);
            return this;
        }

        public Builder level(int newLevel) {
            this.changes.put(LevelChange.KEY, new LevelChange(newLevel));
            return this;
        }

        public Builder lordLevel(int newLordLevel) {
            this.changes.put(LordLevelChange.KEY, new LordLevelChange(newLordLevel));
            return this;
        }

        public Builder faction(Holder<? extends IPlayableFaction<?>> newFaction) {
            this.changes.put(FactionChange.KEY, new FactionChange(newFaction));
            return this;
        }

        public LevelingChange build() {
            return new LevelingChange(Collections.unmodifiableMap(this.changes), this.notifyFactions);
        }
    }

    public interface Change<T extends Change<T>> {

        Key<T> key();

        default String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    public record LevelChange(int newLevel) implements Change<LevelChange> {
        public static final Key<LevelChange> KEY = new Key<>(FResourceLocation.mod("level"));

        public Key<LevelChange> key() {
            return KEY;
        }



    }

    public record FactionChange(Holder<? extends IPlayableFaction<?>> newFaction) implements Change<FactionChange> {
        public static final Key<FactionChange> KEY = new Key<>(FResourceLocation.mod("faction"));

        @Override
        public Key<FactionChange> key() {
            return KEY;
        }

    }

    public record LordLevelChange(int newLevel) implements Change<LordLevelChange> {
        public static final Key<LordLevelChange> KEY = new Key<>(FResourceLocation.mod("lord"));

        @Override
        public Key<LordLevelChange> key() {
            return KEY;
        }
    }

    public record Key<T>(Identifier id) {
    }
}
