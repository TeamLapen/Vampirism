package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;


public class VampireMinionStatsScreen extends MinionStatsScreen<VampireMinionEntity.VampireMinionData, VampireMinionEntity> {
    private final TranslationTextComponent inventoryLevel = new TranslationTextComponent("text.vampirism.minion.stats.inventory_level");
    private final TranslationTextComponent healthLevel = new TranslationTextComponent("attribute.name." + SharedMonsterAttributes.MAX_HEALTH.getName());
    private final TranslationTextComponent strengthLevel = new TranslationTextComponent("attribute.name." + SharedMonsterAttributes.ATTACK_DAMAGE.getName());
    private final TranslationTextComponent speedLevel = new TranslationTextComponent("attribute.name." + SharedMonsterAttributes.MOVEMENT_SPEED.getName());


    public VampireMinionStatsScreen(VampireMinionEntity entity, @Nullable Screen backScreen) {
        super(entity, 4, backScreen);
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
    protected boolean areButtonsVisible(VampireMinionEntity.VampireMinionData d) {
        return d.getRemainingStatPoints() > 0 || d.getLevel() < VampireMinionEntity.VampireMinionData.MAX_LEVEL;
    }

    @Override
    protected int getRemainingStatPoints(VampireMinionEntity.VampireMinionData d) {
        return d.getRemainingStatPoints();
    }

    @Override
    protected void renderStats(VampireMinionEntity.VampireMinionData data) {
        renderLevelRow(data.getLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL + 1);
        renderStatRow(0, inventoryLevel, new StringTextComponent("" + data.getInventorySize()), data.getInventoryLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(1, healthLevel, new StringTextComponent("" + entity.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue()), data.getHealthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(2, strengthLevel, new StringTextComponent("" + entity.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()), data.getStrengthLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(3, speedLevel, new StringTextComponent("" + entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()), data.getSpeedLevel() + 1, VampireMinionEntity.VampireMinionData.MAX_LEVEL_SPEED + 1);

    }
}
