package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public class HunterMinionStatsScreen extends MinionStatsScreen<HunterMinionEntity.HunterMinionData, HunterMinionEntity> {

    private final TranslationTextComponent inventoryLevel = new TranslationTextComponent("text.vampirism.minion.stats.inventory_level");
    private final TranslationTextComponent healthLevel = new TranslationTextComponent("attribute.name." + SharedMonsterAttributes.MAX_HEALTH.getName());
    private final TranslationTextComponent strengthLevel = new TranslationTextComponent("attribute.name." + SharedMonsterAttributes.ATTACK_DAMAGE.getName());
    private final TranslationTextComponent resourceLevel = new TranslationTextComponent("text.vampirism.minion.stats.resource_level");

    public HunterMinionStatsScreen(HunterMinionEntity entity) {
        super(entity, 4);
    }

    @Override
    protected boolean isActive(HunterMinionEntity.HunterMinionData data, int i) {
        switch (i) {
            case 0:
                return data.getRemainingStatPoints() > 0 && data.getInventoryLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_INVENTORY;
            case 1:
                return data.getRemainingStatPoints() > 0 && data.getHealthLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_HEALTH;
            case 2:
                return data.getRemainingStatPoints() > 0 && data.getStrengthLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_STRENGTH;
            case 3:
                return data.getRemainingStatPoints() > 0 && data.getResourceEfficiencyLevel() < HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES;
            default:
                return false;
        }
    }

    @Override
    protected boolean isMaxLevel(HunterMinionEntity.HunterMinionData d) {
        return d.isMaxLevel();
    }

    @Override
    protected void renderStats(HunterMinionEntity.HunterMinionData data) {
        renderLevelRow(data.getInventoryLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL + 1);
        renderStatRow(0, inventoryLevel, new StringTextComponent("" + data.getInventorySize()), data.getInventoryLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(1, healthLevel, new StringTextComponent("" + entity.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue()), data.getHealthLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(2, strengthLevel, new StringTextComponent("" + entity.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()), data.getStrengthLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(3, resourceLevel, new StringTextComponent(""), data.getResourceEfficiencyLevel() + 1, HunterMinionEntity.HunterMinionData.MAX_LEVEL_RESOURCES + 1);

    }
}
