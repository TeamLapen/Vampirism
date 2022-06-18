package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class HeartStrikerItem extends VampirismVampireSword implements IItemWithTier {

    private final static int[] DAMAGE_TIER = {8, 9, 10};
    private final static float[] UNTRAINED_SPEED_TIER = {-3.8f, -3.7f, -3.6f};
    private final static float[] TRAINED_SPEED_TIER = {-2.4f, -2.3f, -2.2f};
    private final TIER tier;

    public HeartStrikerItem(TIER tier) {
        super(ItemTier.IRON, DAMAGE_TIER[tier.ordinal()], UNTRAINED_SPEED_TIER[tier.ordinal()], TRAINED_SPEED_TIER[tier.ordinal()], new Properties().tab(VampirismMod.creativeTab).durability(2500));
        this.tier = tier;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);

    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return (this.getVampirismTier() == TIER.NORMAL ? ModItems.BLOOD_INFUSED_IRON_INGOT.get() : ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get()).equals(repair.getItem()) || super.isValidRepairItem(toRepair, repair);
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
