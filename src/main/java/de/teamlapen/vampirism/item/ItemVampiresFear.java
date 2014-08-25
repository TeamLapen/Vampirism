package de.teamlapen.vampirism.item;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemVampiresFear extends SwordVampirism {
	
	public static final String name="vampiresFear";

	public ItemVampiresFear() {
		super(Item.ToolMaterial.WOOD);
		this.setNoRepair();
		setUnlocalizedName(name);
		this.maxStackSize=1;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(this.getUnlocalizedName());
	}
	
	@Override
	public void onCreated(ItemStack itemStack,World world,EntityPlayer player){
		itemStack.stackTagCompound=new NBTTagCompound();
		itemStack.stackTagCompound.setInteger("blood", 0);
		
	}
	
	@Override
	public void addInformation(ItemStack itemStack,EntityPlayer player,List list, boolean par4){
		if(itemStack.stackTagCompound!=null){
			int blood=itemStack.stackTagCompound.getInteger("blood");
			list.add(EnumChatFormatting.RED+"Blood: "+blood+"/"+REFERENCE.neededBlood);
		}
	}

	
	
	// TODO change the following two classes
	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase e1,
			EntityLivingBase e2) {
		
		
		if(e1 instanceof EntityVampire){
			if(e1.getHealth()<=3){
				
				
				e1.worldObj.spawnParticle("hugeexplosion",e1.posX,e1.posY+1,e1.posZ, 0.0D, 0.1D, 0.0D);
				e1.setHealth(0.0F);
				
				if(itemStack.stackTagCompound==null){
					itemStack.stackTagCompound=new NBTTagCompound();
					itemStack.stackTagCompound.setInteger("blood", 0);
				}
				itemStack.stackTagCompound.setInteger("blood",itemStack.stackTagCompound.getInteger("blood")+1);
			}
			
		}
		return false;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack p_150894_1_, World p_150894_2_,
			Block p_150894_3_, int p_150894_4_, int p_150894_5_,
			int p_150894_6_, EntityLivingBase p_150894_7_) {

		return true;
	}

}
