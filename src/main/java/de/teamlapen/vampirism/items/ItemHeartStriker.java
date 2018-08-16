package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHeartStriker extends VampirismVampireSword implements IItemWithTierNBTImpl {

    public static final String regName = "heart_striker";
    private final static float[] DAMAGE_TIER = {6.0F, 7.0F, 9.0F};
    private final static float[] SPEED_TIER = {0.35f, 0.45f, 0.55f};

    public ItemHeartStriker() {
        super(regName, ToolMaterial.IRON, 0.0f, 0.0f);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addTierInformation(stack, tooltip);

    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (TIER t : TIER.values()) {
                items.add(setTier(new ItemStack(this), t));
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (target.getHealth() <= 0.0f && Helper.isVampire(attacker)) {
            float trained = getTrained(stack, attacker);
            int exp = target instanceof EntityPlayer ? 10 : (attacker instanceof EntityPlayer ? (Helper.getExperiencePoints(target, (EntityPlayer) attacker)) : 5);
            trained += exp / 5f * (1.0f - trained) / 15f;
            setTrained(stack, attacker, trained);
        }
        float charged = getCharged(stack);
        charged -= Balance.general.HEART_SEEKER_USAGE_FACTOR * (getTier(stack).ordinal() + 2) / 2f;
        setCharged(stack, charged);
        attacker.setHeldItem(EnumHand.MAIN_HAND, stack);
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    protected float getBaseAttackDamage(ItemStack stack) {
        return DAMAGE_TIER[getTier(stack).ordinal()];
    }

    @Override
    protected float getBaseAttackSpeed(ItemStack stack) {
        return SPEED_TIER[getTier(stack).ordinal()];
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) Balance.general.HEART_SEEKER_CHARGING_FACTOR * 2f / (getTier(stack).ordinal() + 2);
    }
}
