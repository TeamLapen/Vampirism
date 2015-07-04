package de.teamlapen.vampirism.generation.castle;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

/**
 * Created by Max on 04.07.2015.
 */
public class Extra {
	public final TYPE type;
	public final BlockList.BlockPosition pos;
	private String extra;
	public Extra(TYPE t,BlockList.BlockPosition pos){
		this.type=t;
		this.pos=pos;
	}

	public  void applyExtra(TileEntity tileEntity){
		try {
			switch (type){
			case SPAWNER:
				((TileEntityMobSpawner)tileEntity).func_145881_a().readFromNBT((NBTTagCompound) JsonToNBT.func_150315_a(extra));
				break;
			case CHEST:
				NBTTagList list = (NBTTagList) JsonToNBT.func_150315_a(extra);
				TileEntityChest chest= (TileEntityChest) tileEntity;
				for (int i = 0; i < list.tagCount(); ++i)
				{
					NBTTagCompound nbttagcompound1 = list.getCompoundTagAt(i);
					int j = nbttagcompound1.getByte("Slot") & 255;

					if (j >= 0 && j < chest.getSizeInventory())
					{
						chest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
					}
				}
				break;
			}
		} catch (NBTException e) {
			Logger.e("CastleExtra",e,"Failed to add extra %s",extra);
		}
	}
	public void retrieveExtra(TileEntity tileEntity){
		switch (type){
		case SPAWNER:
			NBTTagCompound nbt=new NBTTagCompound();
			((TileEntityMobSpawner)tileEntity).func_145881_a().writeToNBT(nbt);
			extra=nbt.toString();
			break;
		case CHEST:
			NBTTagList nbttaglist = new NBTTagList();
			TileEntityChest chest= (TileEntityChest) tileEntity;
			for (int i = 0; i < chest.getSizeInventory(); ++i)
			{
				ItemStack stack=chest.getStackInSlot(i);
				if (stack != null)
				{
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte)i);
					stack.writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			}

			extra=nbttaglist.toString();
			break;
		}
	}

	public static enum TYPE{
		SPAWNER,COFFIN,CHEST;
	}
}
