package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemLeechSword extends ItemSword {
	public static final int MAX_BLOOD = 100;

	@SideOnly(Side.CLIENT)
	private static final ModelResourceLocation model=new ModelResourceLocation("vampirism:leechSword","inventory");
	@SideOnly(Side.CLIENT)
	private static final ModelResourceLocation model_unused=new ModelResourceLocation("vampirism:leechSwordUnused","inventory");

	public static final String name = "leechSword";

	public static int getBlood(ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasTagCompound()) {
			return 0;
		}
		return itemStack.getTagCompound().getInteger("blood");
	}

	public static void setBlood(ItemStack itemStack, int amount) {
		if (itemStack == null || amount < 0) {
			return;
		}
		if (!itemStack.hasTagCompound()) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
		if (amount > MAX_BLOOD)
			amount = MAX_BLOOD;
		itemStack.getTagCompound().setInteger("blood", amount);
	}

	public ItemLeechSword() {
		super(Item.ToolMaterial.IRON);
		this.setNoRepair();
		setUnlocalizedName(REFERENCE.MODID+":"+name);
		this.maxStackSize = 1;
		setCreativeTab(VampirismMod.tabVampirism);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		if (itemStack.hasTagCompound()) {
			int blood = itemStack.getTagCompound().getInteger("blood");
			list.add(EnumChatFormatting.RED + "Blood: " + blood + "/" + MAX_BLOOD);
		}
	}


	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
		return getBlood(stack)==0?model_unused:model;
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return this.getUnlocalizedName();
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityTarget, EntityLivingBase e2) {
		if (entityTarget.worldObj.isRemote)
			return false;
		if (entityTarget.getHealth() <= 0 && entityTarget instanceof EntityCreature) {
			if (!itemStack.hasTagCompound()) {
				itemStack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound tag=itemStack.getTagCompound();
			tag.setInteger("blood", tag.getInteger("blood") + VampireMob.get((EntityCreature) entityTarget).getBlood());
			if (tag.getInteger("blood") > MAX_BLOOD)
				tag.setInteger("blood", MAX_BLOOD);
			else if (tag.getInteger("blood") < 0)
				tag.setInteger("blood", 0);
		}
		return false;
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
//		itemStack.stackTagCompound = new NBTTagCompound();
//		itemStack.stackTagCompound.setInteger("blood", 0);
	}

}
