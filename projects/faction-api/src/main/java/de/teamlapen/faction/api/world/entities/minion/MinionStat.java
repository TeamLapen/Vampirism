package de.teamlapen.faction.api.world.entities.minion;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.Supplier;

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

    /**
     * @param minion The minion entity or null if the minion is not loaded
     */
    public void apply(int level, @Nullable IMinionEntity minion, IMinionData data) {
        data.set(identifier, level);
    }

    public int currentLevel(IMinionData minion) {
        return minion.getOrDefault(this.identifier, 0);
    }

    public abstract String currentValue(IMinionEntity minion, IMinionData data);

    public static class AttributeBased extends MinionStat {

        private final Holder<Attribute> attribute;
        private final Supplier<Double> levelValue;

        public AttributeBased(DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> identifier,  Holder<Attribute> attribute, int maxLevel, Supplier<Double> levelValue) {
            super(identifier, maxLevel, Component.translatable(attribute.value().getDescriptionId()));
            this.attribute = attribute;
            this.levelValue = levelValue;
        }

        public Holder<Attribute> getAttribute() {
            return attribute;
        }

        public double perLevelValue() {
            return levelValue.get();
        }

        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            AttributeInstance instance = minion.asEntity().getAttribute(this.attribute);
            return String.format("%.1f", instance != null ? instance.getBaseValue() : 0);
        }
    }
}
