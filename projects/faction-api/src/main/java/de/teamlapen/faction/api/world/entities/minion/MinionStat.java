package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.FactionDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
        data.set(identifier, level);
    }

    public int currentLevel(IMinionData minion) {
        return minion.getOrDefault(this.identifier, 0);
    }

    public abstract String currentValue(IMinionEntity minion, IMinionData data);

    public static final MinionStat HEALTH_STATS = new MinionStat(FactionDataComponents.MINION_HEALTH_LEVEL, 3, Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId())){
        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            return String.format("%.1f", minion.asEntity().getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        }
    };
    public static final MinionStat STRENGTH_STATS = new MinionStat(FactionDataComponents.MINION_STRENGTH_LEVEL, 3, Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId())){
        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            return String.format("%.1f", minion.asEntity().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
    };
    public static final MinionStat INVENTORY_STATS = new MinionStat(FactionDataComponents.MINION_INVENTORY_LEVEL, 2, Component.translatable("gui.vampirism.minion.stats.inventory_level")) {
        @Override
        public void apply(int level, IMinionEntity minion, IMinionData data) {
            super.apply(level, minion, data);
            switch (level) {
                case 1,2,3 -> data.set(FactionDataComponents.MINION_INVENTORY_SLOTS, level * 3);
                default -> data.remove(FactionDataComponents.MINION_INVENTORY_SLOTS.get());
            }
            data.getInventory().updateFromData(data);
        }

        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            return String.valueOf(data.getInventory().getContainerSize());
        }
    };
    public static final MinionStat SPEED_STATS = new MinionStat(FactionDataComponents.MINION_SPEED_LEVEL, 3, Component.translatable(Attributes.MOVEMENT_SPEED.value().getDescriptionId())){
        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            return String.format("%.1f", minion.asEntity().getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
        }
    };
    public static final MinionStat RESOURCES_STATS = new MinionStat(FactionDataComponents.MINION_RESOURCES_LEVEL, 2, Component.translatable("gui.vampirism.minion.stats.resource_level")) {
        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            return String.format("%.1f", (Math.ceil((float) (currentLevel(data) + 1) / (RESOURCES_STATS.getMaxLevel() + 1) * 100))) + "%";
        }
    };
}
