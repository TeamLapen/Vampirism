package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class VampireMinionStatsScreen extends MinionStatsScreen<VampireMinionEntity.VampireMinionData, VampireMinionEntity> {
    private final MutableComponent inventoryLevel = Component.translatable("text.vampirism.minion.stats.inventory_level");
    private final MutableComponent healthLevel = Component.translatable(Attributes.MAX_HEALTH.getDescriptionId());
    private final MutableComponent strengthLevel = Component.translatable(Attributes.ATTACK_DAMAGE.getDescriptionId());
    private final MutableComponent speedLevel = Component.translatable(Attributes.MOVEMENT_SPEED.getDescriptionId());


    public VampireMinionStatsScreen(VampireMinionEntity entity, @Nullable Screen backScreen) {
        super(entity, 4, backScreen);
    }

    @Override
    protected boolean areButtonsVisible(VampireMinionEntity.@NotNull VampireMinionData d) {
        return d.getRemainingStatPoints() > 0 || d.getLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL;
    }

    @Override
    protected int getRemainingStatPoints(VampireMinionEntity.@NotNull VampireMinionData d) {
        return d.getRemainingStatPoints();
    }

    @Override
    protected boolean isActive(VampireMinionEntity.@NotNull VampireMinionData data, int i) {
        return switch (i) {
            case 0 -> data.getRemainingStatPoints() > 0 && data.getInventoryLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY;
            case 1 -> data.getRemainingStatPoints() > 0 && data.getHealthLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH;
            case 2 -> data.getRemainingStatPoints() > 0 && data.getStrengthLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH;
            case 3 -> data.getRemainingStatPoints() > 0 && data.getSpeedLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED;
            default -> false;
        };
    }

    @Override
    protected void renderStats(@NotNull PoseStack mStack, VampireMinionEntity.@NotNull VampireMinionData data) {
        renderLevelRow(mStack, data.getLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL + 1);
        renderStatRow(mStack, 0, inventoryLevel, Component.literal("" + data.getInventorySize()), data.getInventoryLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(mStack, 1, healthLevel, Component.literal("" + entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue()), data.getHealthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(mStack, 2, strengthLevel, Component.literal("" + entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()), data.getStrengthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(mStack, 3, speedLevel, Component.literal("" + entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()), data.getSpeedLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED + 1);

    }
}