package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.client.gui.screens.MinionStatsScreen;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;


public class HunterMinionStatsScreen extends MinionStatsScreen<HunterMinionEntity.HunterMinionData, HunterMinionEntity> {

    public HunterMinionStatsScreen(HunterMinionEntity entity, @Nullable ILastScreenProvider backScreen) {
        super(entity, backScreen);

    }

    @Override
    protected void initStatRows() {
        addStatRow(HunterMinionEntity.HunterMinionData.INVENTORY_STATS);
        addStatRow(HunterMinionEntity.HunterMinionData.HEALTH_STATS);
        addStatRow(HunterMinionEntity.HunterMinionData.STRENGTH_STATS);
        addStatRow(HunterMinionEntity.HunterMinionData.RESOURCES_STATS);
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