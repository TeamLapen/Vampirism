package de.teamlapen.vampirism.generation.castle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.item.Item;
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
	private JsonObject extra;
	public Extra(TYPE t,BlockList.BlockPosition pos){
		this.type=t;
		this.pos=pos;
	}

	public  void applyExtra(TileEntity tileEntity){
			switch (type){
			case SPAWNER:
				((TileEntityMobSpawner)tileEntity).func_145881_a().setEntityName(extra.get("entity_name").getAsString());
				break;
			case CHEST:
				JsonArray items=extra.getAsJsonArray("items");
				TileEntityChest chest= (TileEntityChest) tileEntity;
				for (int i = 0; i < items.size(); ++i)
				{
					JsonObject item=items.get(i).getAsJsonObject();
					int slot=item.get("s").getAsInt();

					if (slot >= 0 && slot < chest.getSizeInventory())
					{
						String[] part=item.get("name").getAsString().split(":");
						Item it=GameRegistry.findItem(part[0], part[1]);
						ItemStack st=new ItemStack(it,item.get("count").getAsInt());
						st.setItemDamage(item.get("damage").getAsInt());
						chest.setInventorySlotContents(slot,st);
					}
				}
				break;
			case COFFIN:
				TileEntityCoffin te=((TileEntityCoffin) tileEntity);
				te.tryToFindOtherTile();
				te.color=extra.get("color").getAsInt();
			}


		JsonObject jsonObject;
	}
	public void retrieveExtra(TileEntity tileEntity){
		extra=new JsonObject();
		switch (type){
		case SPAWNER:
			extra.addProperty("entity_name", ((TileEntityMobSpawner) tileEntity).func_145881_a().getEntityNameToSpawn());
			break;
		case CHEST:
			JsonArray items=new JsonArray();
			TileEntityChest chest= (TileEntityChest) tileEntity;
			for (int i = 0; i < chest.getSizeInventory(); ++i)
			{
				ItemStack stack=chest.getStackInSlot(i);
				if (stack != null)
				{
					JsonObject item=new JsonObject();
					item.addProperty("s",i);
					GameRegistry.UniqueIdentifier un=GameRegistry.findUniqueIdentifierFor(stack.getItem());
					if(un==null)continue;
					item.addProperty("name", un.modId + ":" + un.name);
					item.addProperty("count", stack.stackSize);
					item.addProperty("damage", stack.getItemDamage());
					items.add(item);
				}
			}

			extra.add("items",items);
			break;
		case COFFIN:
			extra.addProperty("color",((TileEntityCoffin)tileEntity).color);
			break;
		}

	}

	public static enum TYPE{
		SPAWNER,COFFIN,CHEST;
	}
}
