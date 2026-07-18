package de.teamlapen.faction.common.factions.minions.stats;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.faction.common.util.collections.CollectionUtil;
import de.teamlapen.sync.PropertySync;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MinionStat<T extends MinionData>  {

    private final int maxLevel;
    private final Component description;
    private final Identifier identifier;

    public MinionStat(Identifier identifier, @Range(from = 1, to = Integer.MAX_VALUE) int maxLevel, Component description) {
        this.identifier = identifier;
        this.maxLevel = maxLevel;
        this.description = description;
    }

    public Component getDescription() {
        return description;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
    public Identifier getIdentifier() {
        return identifier;
    }

    public void apply(int level, MinionEntity<?> minion, T data) {

    }

    public int currentLevel(MinionData minion) {
        return minion.getStatLevel(this.identifier);
    }

    public abstract String currentValue(MinionEntity<?> minion, MinionData data);

    public static class StatCollection extends PropertySync {

        private final PropertySync parent;
        private final Map<Identifier, MinionStat<?>> stats;
        private Map<Identifier, Integer> levels = new HashMap<>();

        private static final Codec<Map<Identifier, Integer>> LEVEL_CODEC = Codec.unboundedMap(Identifier.CODEC, Codec.INT);

        public StatCollection(PropertySync parent, Collection<MinionStat<?>> stats) {
            this.parent = parent;
            this.stats = stats.stream().collect(Collectors.toUnmodifiableMap(x -> x.identifier, x -> x));
        }

        public boolean upgrade(Identifier identifier, MinionEntity<?> minion, MinionData data) {
            //noinspection unchecked
            MinionStat<MinionData> minionStat = (MinionStat<MinionData>) stats.get(identifier);
            if (minionStat == null) {
                return false;
            }
            int currentLevel = levels.getOrDefault(identifier, 0);
            if (currentLevel >= minionStat.getMaxLevel()) {
                return false;
            }

            levels.put(identifier, ++currentLevel);
            minionStat.apply(currentLevel, minion, data);
            minion.updateAttributes();
            return true;
        }

        public int getLevels() {
            return levels.values().stream().mapToInt(Integer::intValue).sum();
        }

        public int getStatLevel(Identifier identifier) {
            return stats.containsKey(identifier) ? levels.getOrDefault(identifier, 0) : 0;
        }

        public void reset(MinionEntity<?> minion, MinionData data) {
            this.levels.clear();
            //noinspection unchecked
            this.stats.forEach((_, minionStat) -> ((MinionStat<MinionData>) minionStat).apply(0, minion, data));
            minion.updateAttributes();
        }

        @Override
        protected void registerProperties() {
            this.registerProperty(FIdentifier.mod("stats")).map(LEVEL_CODEC).provider(() -> this.levels).commonLoader(x -> CollectionUtil.updateCollection(this.levels, x)).register();
        }

        @Override
        public void sync() {
            this.parent.sync();
        }
    }
}
