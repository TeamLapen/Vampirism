package de.teamlapen.vampirism.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;

public class ItemLeechSword extends ItemSword {
	public static int getBlood(ItemStack itemStack) {
		if (itemStack == null || itemStack.stackTagCompound == null) {
			return 0;
		}
		return itemStack.stackTagCompound.getInteger("blood");
	}

	public static void setBlood(ItemStack itemStack, int amount) {
		if (itemStack == null || amount < 0) {
			return;
		}
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
			itemStack.stackTagCompound.setInteger("blood", 0);
		}
		if (amount > MAX_BLOOD)
			amount = MAX_BLOOD;
		itemStack.stackTagCompound.setInteger("blood", amount);
		;
	}

	public static final int MAX_BLOOD = 100;

	public static final String name = "leechSword";

	private IIcon unusedIcon;

	public ItemLeechSword() {
		super(Item.ToolMaterial.IRON);
		this.setNoRepair();
		setUnlocalizedName(name);
		this.maxStackSize = 1;
		setCreativeTab(VampirismMod.tabVampirism);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		if (itemStack.stackTagCompound != null) {
			int blood = itemStack.stackTagCompound.getInteger("blood");
			list.add(EnumChatFormatting.RED + "Blood: " + blood + "/" + MAX_BLOOD);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconIndex(ItemStack stack) {
		if (getBlood(stack) > 0) {
			return itemIcon;
		}
		return unusedIcon;
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityTarget, EntityLivingBase e2) {
		if (entityTarget.worldObj.isRemote)
			return false;
		if (entityTarget.getHealth() <= 0 && entityTarget instanceof EntityCreature) {
			if (itemStack.stackTagCompound == null) {
				itemStack.stackTagCompound = new NBTTagCompound();
				itemStack.stackTagCompound.setInteger("blood", 0);
			}
			itemStack.stackTagCompound.setInteger("blood", itemStack.stackTagCompound.getInteger("blood") + VampireMob.getMaxBloodAmount((EntityCreature) entityTarget));
			if (itemStack.stackTagCompound.getInteger("blood") > MAX_BLOOD)
				itemStack.stackTagCompound.setInteger("blood", MAX_BLOOD);
			else if (itemStack.stackTagCompound.getInteger("blood") < 0)
				itemStack.stackTagCompound.setInteger("blood", 0);
		}
		return false;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack p_150894_1_, World p_150894_2_, Block p_150894_3_, int p_150894_4_, int p_150894_5_, int p_150894_6_, EntityLivingBase p_150894_7_) {

		return true;
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("blood", 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
		unusedIcon = iconRegister.registerIcon("vampirism:leechSwordUnused");
	}
}
