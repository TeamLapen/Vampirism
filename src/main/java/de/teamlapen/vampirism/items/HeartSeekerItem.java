package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeartSeekerItem extends VampirismVampireSwordItem implements IItemWithTier {

    public static final VampireSwordMaterial NORMAL = new VampireSwordMaterial(TIER.NORMAL, BlockTags.INCORRECT_FOR_IRON_TOOL, 500, -3.6f, 1.5F, 14, () -> Ingredient.of(ModItems.BLOOD_INFUSED_IRON_INGOT.get()), 1.3f);
    public static final VampireSwordMaterial ENHANCED = new VampireSwordMaterial(TIER.ENHANCED,BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1750, -3.5f, 2.5F, 14, () -> Ingredient.of(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get()), 1.4f);
    public static final VampireSwordMaterial ULTIMATE = new VampireSwordMaterial(TIER.ULTIMATE,BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, -3.4f, 3.5F, 14, () -> Ingredient.of(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get()), 1.5f);

    private final @NotNull TIER tier;

    public HeartSeekerItem(@NotNull VampireSwordMaterial material) {
        super(material, 3, new Properties());
        this.tier = material.getTier();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, context, tooltip, flagIn);
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
    protected float getChargeUsage() {
        return (float) ((VampirismConfig.BALANCE.vampireSwordBloodUsageFactor.get() / 100f) * (getVampirismTier().ordinal() + 2) / 2f);
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) (VampirismConfig.BALANCE.vampireSwordChargingFactor.get() * 2f / (getVampirismTier().ordinal() + 2f));
    }
}
