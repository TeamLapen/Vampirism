package de.teamlapen.faction.api.world.entities.minion;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Range;

public abstract class MinionStat  {

    private final int maxLevel;
    private final Component description;
    private final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> identifier;

    public MinionStat(DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> identifier, @Range(from = 1, to = Integer.MAX_VALUE) int maxLevel, Component description) {
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

    public DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> getIdentifier() {
        return identifier;
    }

    public void apply(int level, IMinionEntity minion, IMinionData data) {

    }

    public int currentLevel(IMinionData minion) {
        return minion.getOrDefault(this.identifier, 0);
    }

    public abstract String currentValue(IMinionEntity minion, IMinionData data);

}
