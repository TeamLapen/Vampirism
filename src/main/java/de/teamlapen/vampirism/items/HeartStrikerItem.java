package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.items.component.PureLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;

public class HeartStrikerItem extends VampireSwordItem implements IItemWithTier, ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final ToolMaterial IRON = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, -4.1f, 3.2F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_SIMPLE);
    public static final ToolMaterial DIAMOND = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1750, -4.0f, 5.2F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);
    public static final ToolMaterial NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, -3.9f, 7.2F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);

    private final Tier tier;

    public HeartStrikerItem(ToolMaterial material, Tier tier, float trainSpeedIncrease, Properties properties) {
        super(material, 3, trainSpeedIncrease , properties);
        this.tier = tier;
    }

    @Override
    public Tier getVampirismTier() {
        return tier;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return this.getVampirismTier() == Tier.ULTIMATE ? super.getXpRepairRatio(stack) / 2f : super.getXpRepairRatio(stack);
    }

    @Override
    protected float getChargeUsage(ItemStack stack) {
        return (float) ((VampirismConfig.BALANCE.vampireSwordBloodUsageFactor.get() / 100f) * (getVampirismTier().ordinal() + 2) / 2f) * getPurityChargeUsageModifier(stack);
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) (VampirismConfig.BALANCE.vampireSwordChargingFactor.get() * 2f / (getVampirismTier().ordinal() + 2f)) * getPurityChargeSpeedModifier(stack);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(PureLevel.pureBlood(this, 0));
        for (int i = 1; i < 5; i++) {
            output.accept(PureLevel.pureBlood(this, i), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
