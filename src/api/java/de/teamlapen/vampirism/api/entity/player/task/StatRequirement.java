package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class StatRequirement<T extends IForgeRegistryEntry<?>> extends TaskRequirement {

    private final Stat<T> stat;
    private final int amount;
    private final T type;
    private final StatType<T> stats;

    public StatRequirement(StatType<T> statType, T type, int amount) {
        super(Type.STATS);
        this.stat = statType.get(type);
        this.amount = amount;
        this.type = type;
        this.stats = statType;
    }

    public StatType<T> getStatsType() {
        return stats;
    }

    public T getStatType() {
        return type;
    }

    public Stat<?> getStat() {
        return stat;
    }

    public int getAmount() {
        return amount;
    }
}