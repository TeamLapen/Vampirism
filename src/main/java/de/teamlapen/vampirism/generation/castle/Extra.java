package de.teamlapen.vampirism.generation.castle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

/**
 * Used to save additional information from tileentitys or other things with building tiles
 * @author Maxanier
 */
public class Extra {
	public final TYPE type;
	public final BlockList.BlockPosition pos;
	private JsonObject extra;
	public Extra(TYPE t,BlockList.BlockPosition pos){
		this.type=t;
		this.pos=pos;
	}

	/**
	 * Applies this extra to the given world
	 * @param world
	 * @param wx World x coordinate
	 * @param wy World y coordinate
	 * @param wz World z coordinate
	 */
	public void applyExtra(World world,int wx,int wy,int wz){
		if(type==TYPE.SPAWN_ENTITY){
			int c=extra.get("count").getAsInt();
			String entity=extra.get("entity").getAsString();
			for(int i=0;i<c;i++){
				Entity e= EntityList.createEntityByName(entity,world);
				if(e!=null) {
					e.setPosition(wx, wy, wz);
					world.spawnEntityInWorld(e);
				}
			}

		}
		else if(type==TYPE.PAINTING){
			int dir=extra.get("dir").getAsInt();
			String title=extra.get("title").getAsString();
			EntityPainting.EnumArt art=null;
			for(EntityPainting.EnumArt a: EntityPainting.EnumArt.values()){
				if(a.title.equals(title)){
					art=a;
					break;
				}
			}
			if(art==null)art= EntityPainting.EnumArt.Alban;
			EntityPainting p=new EntityPainting(world,wx,wy,wz,dir);
			p.art=art;
			world.spawnEntityInWorld(p);

		} else{
			TileEntity tileEntity=world.getTileEntity(wx, wy, wz);
			switch (type) {
			case SPAWNER:
				((TileEntityMobSpawner) tileEntity).func_145881_a().setEntityName(extra.get("entity_name").getAsString());
				break;
			case CHEST:
				JsonArray items = extra.getAsJsonArray("items");
				TileEntityChest chest = (TileEntityChest) tileEntity;
				for (int i = 0; i < items.size(); ++i) {
					JsonObject item = items.get(i).getAsJsonObject();
					int slot = item.get("s").getAsInt();

					if (slot >= 0 && slot < chest.getSizeInventory()) {
						String[] part = item.get("name").getAsString().split(":");
						Item it = GameRegistry.findItem(part[0], part[1]);
						ItemStack st = new ItemStack(it, item.get("count").getAsInt());
						st.setItemDamage(item.get("damage").getAsInt());
						chest.setInventorySlotContents(slot, st);
					}
				}
				break;
			case COFFIN:
				TileEntityCoffin te = ((TileEntityCoffin) tileEntity);
				te.tryToFindOtherTile();
				te.color = extra.get("color").getAsInt();
				te.occupied=extra.get("closed").getAsBoolean();
				break;
			case WALL_SIGN:
				TileEntitySign sign2=(TileEntitySign)tileEntity;
				sign2.signText[0]=extra.get("t0").getAsString();
				sign2.signText[1]=extra.get("t1").getAsString();
				sign2.signText[2]=extra.get("t2").getAsString();
				sign2.signText[3]=extra.get("t3").getAsString();
			}

		}
	}

	/**
	 * Retrieves the information from a tileentity as long as this extra has the right type
	 * @param tileEntity
	 */
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
			extra.addProperty("closed",((TileEntityCoffin)tileEntity).occupied);
			break;
		case SPAWN_ENTITY:
			TileEntitySign sign=(TileEntitySign)tileEntity;
			int count=Integer.parseInt(sign.signText[0]);
			String entity=sign.signText[1]+sign.signText[2]+sign.signText[3];
			extra.addProperty("count",count);
			extra.addProperty("entity",entity.trim());
			break;
		case WALL_SIGN:
			TileEntitySign sign2=(TileEntitySign)tileEntity;
			extra.addProperty("t0",sign2.signText[0]);
			extra.addProperty("t1",sign2.signText[1]);
			extra.addProperty("t2",sign2.signText[2]);
			extra.addProperty("t3",sign2.signText[3]);
			break;
		}

	}

	/**
	 * Retrieves information from a painting
	 * @param p
	 */
	public void retrieveExtra(EntityPainting p){
		extra=new JsonObject();
		extra.addProperty("dir",p.hangingDirection);
		extra.addProperty("title",p.art.title);
	}

	/**
	 * Types of {@link Extra}s
	 */
	public static enum TYPE{
		SPAWNER,COFFIN,CHEST,SPAWN_ENTITY,PAINTING,WALL_SIGN;
	}
}
