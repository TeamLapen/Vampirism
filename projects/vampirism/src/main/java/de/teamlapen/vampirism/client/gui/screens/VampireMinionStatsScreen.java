package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.client.gui.screens.MinionStatsScreen;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;


public class VampireMinionStatsScreen extends MinionStatsScreen<VampireMinionEntity.VampireMinionData, VampireMinionEntity> {


    public VampireMinionStatsScreen(VampireMinionEntity entity, @Nullable ILastScreenProvider backScreen) {
        super(entity, backScreen);
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
        return VampireMinionEntity.VampireMinionData.MAX_LEVEL;
    }

    @Override
    protected void initStatRows() {
        addStatRow(VampireMinionEntity.VampireMinionData.INVENTORY_STATS);
        addStatRow(VampireMinionEntity.VampireMinionData.HEALTH_STATS);
        addStatRow(VampireMinionEntity.VampireMinionData.STRENGTH_STATS);
        addStatRow(VampireMinionEntity.VampireMinionData.SPEED_STATS);
    }
}