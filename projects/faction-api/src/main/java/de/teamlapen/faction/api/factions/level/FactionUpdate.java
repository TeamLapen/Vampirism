package de.teamlapen.faction.api.factions.level;

import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.level.change.Change;
import de.teamlapen.faction.api.factions.level.change.FactionChange;
import de.teamlapen.faction.api.factions.level.change.LevelChange;
import de.teamlapen.faction.api.factions.level.change.LordLevelChange;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

///
/// When changing the players' faction, level, or something else, all changes are made through this class. It is passed to the relevant places to change the players' status
///
/// This class defines the changes to a player's faction and faction properties, such as level and lord level.
/// If a change for a given property is not present, that property is not subject to change - however, it must
/// still be checked for validity against the other changes being applied (e.g. the level must not be too low for
/// a lord level above `0`).
public record FactionUpdate(Map<ChangeKey<?>, Change<?>> values, boolean notifyFactions) {

    public FactionUpdate(Map<ChangeKey<?>, Change<?>> values) {
        this(values, true);
    }

    public FactionUpdate(List<Change<?>> changes) {
        this(changes.stream().collect(Collectors.toMap(Change::key, Function.identity())));
    }


    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(ChangeKey<T> key) {
        return (T) values.get(key);
    }

    //<editor-fold desc="Change Helper">

    //<editor-fold desc="Faction">

    public boolean hasFactionChange() {
        return get(FactionChange.KEY) != null;
    }

    public Holder<? extends IPlayableFaction<?>> getFaction() {
        return getFaction(Factions.NEUTRAL);
    }

    public Holder<? extends IPlayableFaction<?>> getFaction(Holder<? extends IPlayableFaction<?>> fallback) {
        FactionChange factionChange = get(FactionChange.KEY);
        return factionChange == null ? fallback : factionChange.newFaction();
    }

    //</editor-fold>
    //<editor-fold desc="Level">

    public boolean hasLevelChange() {
        return get(LevelChange.KEY) != null;
    }

    public int getLevel() {
        return getLevel(0);
    }

    public int getLevel(int fallback) {
        LevelChange factionChange = get(LevelChange.KEY);
        return factionChange == null ? fallback : factionChange.newLevel();
    }

    //</editor-fold>

    //<editor-fold desc="Lord">

    public boolean hasLordLevelChange() {
        return get(LordLevelChange.KEY) != null;
    }

    public int getLordLevel() {
        return getLordLevel(0);
    }

    public int getLordLevel(int fallback) {
        LordLevelChange factionChange = get(LordLevelChange.KEY);
        return factionChange == null ? fallback : factionChange.newLevel();
    }

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Builder Helper">

    public Builder copy() {
        var builder = new Builder();
        this.values.forEach((x, y) -> builder.add(y));
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FactionUpdate neutral() {
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

    //</editor-fold>

    //<editor-fold desc="Builder">

    public static class Builder {
        private final Map<ChangeKey<?>, Change<?>> changes = new HashMap<>();
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

        public FactionUpdate build() {
            return new FactionUpdate(Collections.unmodifiableMap(this.changes), this.notifyFactions);
        }
    }

    //</editor-fold>


    public String toJson() {
        return "{" +
                this.values.entrySet().stream().map(x -> "\"" + x.getKey().id() + "\":" + x.getValue().toJson()).collect(Collectors.joining(",")) +
                "}";
    }

}
