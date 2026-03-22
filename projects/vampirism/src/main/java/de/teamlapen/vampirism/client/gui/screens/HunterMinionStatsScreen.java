package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.client.gui.screens.MinionStatsScreen;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;


public class HunterMinionStatsScreen extends MinionStatsScreen<HunterMinionEntity.HunterMinionData, HunterMinionEntity> {

    public HunterMinionStatsScreen(HunterMinionEntity entity, @Nullable Screen backScreen) {
        super(entity, backScreen);

    }

    @Override
    protected void initStatRows() {
        addStatRow(new StatRow(Component.translatable("gui.vampirism.minion.stats.inventory_level"), HunterMinionEntity.HunterMinionData.MAX_LEVEL_INVENTORY, () -> String.valueOf(this.minionData.getInventorySize()), () -> this.minionData.getInventoryLevel()));
        addStatRow(new StatRow(Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId()), HunterMinionEntity.HunterMinionData.MAX_LEVEL_HEALTH, () -> String.format("%.1f", entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue()), () -> this.minionData.getHealthLevel()));
        addStatRow(new StatRow(Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId()), HunterMinionEntity.HunterMinionData.MAX_LEVEL_STRENGTH, () -> String.format("%.1f", entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()), () -> this.minionData.getStrengthLevel()));
        addStatRow(new StatRow(Component.translatable("gui.vampirism.minion.stats.resource_level"), HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES, () -> String.format("%.1f", (Math.ceil((float) (this.minionData.getResourceEfficiencyLevel() + 1) / (HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES + 1) * 100))) + "%", () -> this.minionData.getResourceEfficiencyLevel()));
    }


    @Override
    protected int getRemainingStatPoints() {
        return this.minionData.getRemainingStatPoints();
    }

    @Override
    protected int getLevel() {
        return this.minionData.getLevel();
    }

    @Override
    protected int getMaxLevel() {
        return HunterMinionEntity.HunterMinionData.MAX_LEVEL;
    }
}