package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;


public class VampireMinionStatsScreen extends MinionStatsScreen<VampireMinionEntity.VampireMinionData, VampireMinionEntity> {
    private final TranslationTextComponent inventoryLevel = new TranslationTextComponent("text.vampirism.minion.stats.inventory_level");
    private final TranslationTextComponent healthLevel = new TranslationTextComponent(Attributes.MAX_HEALTH.getDescriptionId());
    private final TranslationTextComponent strengthLevel = new TranslationTextComponent(Attributes.ATTACK_DAMAGE.getDescriptionId());
    private final TranslationTextComponent speedLevel = new TranslationTextComponent(Attributes.MOVEMENT_SPEED.getDescriptionId());


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
        switch (i) {
            case 0:
                return data.getRemainingStatPoints() > 0 && data.getInventoryLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY;
            case 1:
                return data.getRemainingStatPoints() > 0 && data.getHealthLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH;
            case 2:
                return data.getRemainingStatPoints() > 0 && data.getStrengthLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH;
            case 3:
                return data.getRemainingStatPoints() > 0 && data.getSpeedLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED;
            default:
                return false;
        }
    }

    @Override
    protected void renderStats(MatrixStack mStack, VampireMinionEntity.VampireMinionData data) {
        renderLevelRow(mStack, data.getLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL + 1);
        renderStatRow(mStack, 0, inventoryLevel, new StringTextComponent("" + data.getInventorySize()), data.getInventoryLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(mStack, 1, healthLevel, new StringTextComponent(String.format("%.1f", entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue())), data.getHealthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(mStack, 2, strengthLevel, new StringTextComponent(String.format("%.1f", entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue())), data.getStrengthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(mStack, 3, speedLevel, new StringTextComponent(String.format("%.1f", entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue())), data.getSpeedLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED + 1);

    }
}