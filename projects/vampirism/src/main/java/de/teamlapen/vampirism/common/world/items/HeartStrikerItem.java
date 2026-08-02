package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class HeartStrikerItem extends VampireSwordItem implements BaseDisplayItemGenerator.CreativeTabItemProvider {

    public static final ToolMaterial IRON = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, -3.9f, 3.2F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_SIMPLE);
    public static final ToolMaterial DIAMOND = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1750, -3.9f, 5.2F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);
    public static final ToolMaterial NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, -3.9f, 7.2F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);

    public HeartStrikerItem(ToolMaterial material, Tier tier, float trainSpeedIncrease, Properties properties) {
        super(material, tier, 3, trainSpeedIncrease , properties);
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return this.getVampirismTier() == Tier.ULTIMATE ? super.getXpRepairRatio(stack) / 2f : super.getXpRepairRatio(stack);
    }

    @Override
    protected float getChargeUsage(ItemStack stack) {
        return (float) ((ModConfig.balance().vampireSwordBloodUsageFactor.get() / 100f) * (getVampirismTier().ordinal() + 2) / 2f) * getPurityChargeUsageModifier(stack);
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) (ModConfig.balance().vampireSwordChargingFactor.get() * 2f / (getVampirismTier().ordinal() + 2f)) * getPurityChargeSpeedModifier(stack);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(PureLevel.pureBlood(this, 0));
        for (int i = 1; i < 6; i++) {
            output.accept(PureLevel.pureBlood(this, i), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
