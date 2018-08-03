package de.teamlapen.vampirism.items;

import java.util.List;

import javax.annotation.Nullable;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHeartSeeker extends VampirismVampireSword implements IItemWithTierNBTImpl {

	public static final String regName = "heart_seeker";
	private final static float[] DAMAGE_TIER = { 5.0F, 6.0F, 8.0F };
	private final static float[] SPEED_TIER = { 0.4f, 0.5f, 0.6f };

	public ItemHeartSeeker() {
		super(regName, ToolMaterial.IRON, 0.0f, 0.0f);
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (TIER t : TIER.values()) {
			subItems.add(setTier(new ItemStack(itemIn), t));
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

	@SideOnly(Side.CLIENT)
	@Override
	protected void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		addTierInformation(stack, tooltip);
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
