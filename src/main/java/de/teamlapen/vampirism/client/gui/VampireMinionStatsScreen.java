package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;


public class VampireMinionStatsScreen extends MinionStatsScreen<VampireMinionEntity.VampireMinionData, VampireMinionEntity> {
    private final TranslatableComponent inventoryLevel = new TranslatableComponent("text.vampirism.minion.stats.inventory_level");
    private final TranslatableComponent healthLevel = new TranslatableComponent(Attributes.MAX_HEALTH.getDescriptionId());
    private final TranslatableComponent strengthLevel = new TranslatableComponent(Attributes.ATTACK_DAMAGE.getDescriptionId());
    private final TranslatableComponent speedLevel = new TranslatableComponent(Attributes.MOVEMENT_SPEED.getDescriptionId());


    public VampireMinionStatsScreen(VampireMinionEntity entity, @Nullable Screen backScreen) {
        super(entity, 4, backScreen);
    }

    @Override
    protected boolean areButtonsVisible(VampireMinionEntity.VampireMinionData d) {
        return d.getRemainingStatPoints() > 0 || d.getLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL;
    }

    @Override
    protected int getRemainingStatPoints(VampireMinionEntity.VampireMinionData d) {
        return d.getRemainingStatPoints();
    }

    @Override
    protected boolean isActive(VampireMinionEntity.VampireMinionData data, int i) {
        return switch (i) {
            case 0 -> data.getRemainingStatPoints() > 0 && data.getInventoryLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY;
            case 1 -> data.getRemainingStatPoints() > 0 && data.getHealthLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH;
            case 2 -> data.getRemainingStatPoints() > 0 && data.getStrengthLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH;
            case 3 -> data.getRemainingStatPoints() > 0 && data.getSpeedLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED;
            default -> false;
        };
    }

    @Override
    protected void renderStats(PoseStack mStack, VampireMinionEntity.VampireMinionData data) {
        renderLevelRow(mStack, data.getLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL + 1);
        renderStatRow(mStack, 0, inventoryLevel, new TextComponent("" + data.getInventorySize()), data.getInventoryLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(mStack, 1, healthLevel, new TextComponent("" + entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue()), data.getHealthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(mStack, 2, strengthLevel, new TextComponent("" + entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()), data.getStrengthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(mStack, 3, speedLevel, new TextComponent("" + entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()), data.getSpeedLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED + 1);

    }
}