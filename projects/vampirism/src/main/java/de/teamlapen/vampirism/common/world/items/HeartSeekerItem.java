package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HeartSeekerItem extends VampireSwordItem implements IItemWithTier, BaseDisplayItemGenerator.CreativeTabItemProvider {

    public static final ToolMaterial IRON = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, -3.6f, 1.7F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_SIMPLE);
    public static final ToolMaterial DIAMOND = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1750, -3.5f, 2.7F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);
    public static final ToolMaterial NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, -3.4f, 3.7F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);

    private final Tier tier;

    public HeartSeekerItem(ToolMaterial material, Tier tier, float trainIncrease, Properties properties) {
        super(material, 3, trainIncrease, properties);
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);

        if (flagIn.hasAltDown()) {
            tooltip.accept(Component.translatable("Consumes %s blood per hit", getChargeUsage(stack)));
        }
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
        return (float) ((ModConfig.BALANCE.vampireSwordBloodUsageFactor.get() / 100f) * (getVampirismTier().ordinal() + 2) / 2f) * getPurityChargeUsageModifier(stack);
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) (ModConfig.BALANCE.vampireSwordChargingFactor.get() * 2f / (getVampirismTier().ordinal() + 2f)) * getPurityChargeSpeedModifier(stack);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(PureLevel.pureBlood(this, 0));
        for (int i = 1; i < 5; i++) {
            output.accept(PureLevel.pureBlood(this, i), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
