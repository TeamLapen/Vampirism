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

    public static final String regName = "heart_striker";
    private final static int[] DAMAGE_TIER = {8, 9, 11};
    private final static float[] UNTRAINED_SPEED_TIER = {-4.2f, -3.9f, -3.6f};
    private final static float[] TRAINED_SPEED_TIER = {-1.6f, -1.5f, -1.4f};
    private final TIER tier;

    public HeartStrikerItem(TIER tier) {
        super(regName + "_" + tier.getName(), ItemTier.IRON, DAMAGE_TIER[tier.ordinal()], UNTRAINED_SPEED_TIER[tier.ordinal()], TRAINED_SPEED_TIER[tier.ordinal()], new Properties().group(VampirismMod.creativeTab).maxDamage(2500));
        this.tier = tier;
        this.setTranslation_key(regName);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);

    }

    @Override
    public String getBaseRegName() {
        return regName;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    protected float getChargeUsage() {
        return (float) ((VampirismConfig.BALANCE.vampireSwordBloodUsageFactor.get() / 100f) * (getVampirismTier().ordinal() + 2) / 2f);
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) (VampirismConfig.BALANCE.vampireSwordChargingFactor.get() * 2f / (getVampirismTier().ordinal() + 2f));
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return (this.getVampirismTier() == TIER.NORMAL ? ModItems.blood_infused_iron_ingot : ModItems.blood_infused_enhanced_iron_ingot).equals( repair.getItem()) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return this.getVampirismTier()==TIER.ULTIMATE ? super.getXpRepairRatio(stack)/2f : super.getXpRepairRatio(stack);
    }
}
