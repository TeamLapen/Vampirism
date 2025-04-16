package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.items.component.PureLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeartSeekerItem extends VampireSwordItem implements IItemWithTier, ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final ToolMaterial IRON = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, -3.6f, 1.7F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_SIMPLE);
    public static final ToolMaterial DIAMOND = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1750, -3.5f, 2.7F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);
    public static final ToolMaterial NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, -3.4f, 3.7F, 14, ModItemTags.VAMPIRE_SWORD_REPAIRABLE_ENHANCED);

    private final @NotNull TIER tier;

    public HeartSeekerItem(@NotNull ToolMaterial material, @NotNull TIER tier, float trainIncrease, Properties properties) {
        super(material, 3, trainIncrease, properties);
        this.tier = tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        if (flagIn.hasAltDown()) {
            tooltip.add(Component.translatable("Consumes %s blood per hit", getChargeUsage(stack)));
        }
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return this.getVampirismTier() == TIER.ULTIMATE ? super.getXpRepairRatio(stack) / 2f : super.getXpRepairRatio(stack);
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
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(PureLevel.pureBlood(this, 0));
        for (int i = 1; i < 5; i++) {
            output.accept(PureLevel.pureBlood(this, i), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
