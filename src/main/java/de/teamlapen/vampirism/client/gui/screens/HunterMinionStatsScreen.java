package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class HunterMinionStatsScreen extends MinionStatsScreen<HunterMinionEntity.HunterMinionData, HunterMinionEntity> {

    private final MutableComponent inventoryLevel = Component.translatable("text.vampirism.minion.stats.inventory_level");
    private final MutableComponent healthLevel = Component.translatable(Attributes.MAX_HEALTH.getDescriptionId());
    private final MutableComponent strengthLevel = Component.translatable(Attributes.ATTACK_DAMAGE.getDescriptionId());
    private final MutableComponent resourceLevel = Component.translatable("text.vampirism.minion.stats.resource_level");

    public HunterMinionStatsScreen(HunterMinionEntity entity, @Nullable Screen backScreen) {
        super(entity, 4, backScreen);
    }

    @Override
    protected boolean areButtonsVisible(HunterMinionEntity.@NotNull HunterMinionData d) {
        return d.getRemainingStatPoints() > 0 || d.getLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL;
    }

    @Override
    protected int getRemainingStatPoints(HunterMinionEntity.@NotNull HunterMinionData d) {
        return d.getRemainingStatPoints();
    }

    @Override
    protected boolean isActive(HunterMinionEntity.@NotNull HunterMinionData data, int i) {
        return switch (i) {
            case 0 -> data.getRemainingStatPoints() > 0 && data.getInventoryLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_INVENTORY;
            case 1 -> data.getRemainingStatPoints() > 0 && data.getHealthLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_HEALTH;
            case 2 -> data.getRemainingStatPoints() > 0 && data.getStrengthLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_STRENGTH;
            case 3 -> data.getRemainingStatPoints() > 0 && data.getResourceEfficiencyLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES;
            default -> false;
        };
    }

    @Override
    protected void renderStats(@NotNull PoseStack mStack, HunterMinionEntity.@NotNull HunterMinionData data) {
        renderLevelRow(mStack, data.getLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL + 1);
        renderStatRow(mStack, 0, inventoryLevel, Component.literal("" + data.getInventorySize()), data.getInventoryLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(mStack, 1, healthLevel, Component.literal(String.format("%.1f", entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue())), data.getHealthLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(mStack, 2, strengthLevel, Component.literal(String.format("%.1f", entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue())), data.getStrengthLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(mStack, 3, resourceLevel, Component.literal(String.format("%.1f", (Math.ceil((float) (data.getResourceEfficiencyLevel() + 1) / (HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES + 1) * 100))) + "%"), data.getResourceEfficiencyLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES + 1);

    }
}