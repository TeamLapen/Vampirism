package de.teamlapen.vampirism.tileEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.block.BlockBloodAltar1;
import de.teamlapen.vampirism.block.BlockBloodAltar2;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.generation.castle.BlockList;
import de.teamlapen.vampirism.generation.castle.BuildingTile;
import de.teamlapen.vampirism.generation.castle.Extra;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Used for development. Generates the .json schematics from chunks.
 */
public class TileEntityTemplateGenerator extends TileEntity {
	private List<BlockList> blockLists;
	private List<Extra> extraList;
	private final String TAG="TETemplateGenerator";
	public void onActivated(int minY){
		Logger.i(TAG,"Activated with minY %d",minY);
		blockLists=new LinkedList<BlockList>();
		extraList=new LinkedList<Extra>();
		Chunk chunk=
				this.getWorldObj().getChunkFromBlockCoords(this.xCoord, this.zCoord);

		for(int x=0;x<16;x++){
			for(int z=0;z<16;z++){
				for(int y=this.yCoord+minY;y<worldObj.getActualHeight();y++){
					Block block=chunk.getBlock(x, y, z);
					if(block.equals(ModBlocks.templateGenerator))continue;
					if(block.equals(Blocks.sand))continue;
					if(block.getMaterial()==Material.air&&y>=this.yCoord)continue;
					if(block.equals(Blocks.standing_sign)){
						TileEntitySign sign= (TileEntitySign) chunk.getTileEntityUnsafe(x,y,z);
						if(sign!=null){
							Extra e=new Extra(Extra.TYPE.SPAWN_ENTITY,new BlockList.BlockPosition(x,y-this.yCoord,z));
							e.retrieveExtra(sign);
							extraList.add(e);
						}
						if(y<this.yCoord){
							addBlock(Blocks.air,new int[]{0,0,0,0},x,y-this.yCoord,z);
						}
						continue;
					}

						int m=chunk.getBlockMetadata(x, y, z);
						int[] meta=guessMetaForBlock(block,m);
						addBlock(block,meta,x,y-this.yCoord,z);
						Extra.TYPE extraType=null;
						if(block instanceof BlockMobSpawner){
							extraType= Extra.TYPE.SPAWNER;
						}
						else if(block instanceof BlockChest){
							extraType= Extra.TYPE.CHEST;
						}
						else if(block instanceof BlockCoffin){
							extraType = Extra.TYPE.COFFIN;
						}
						else if(block.equals(Blocks.wall_sign)){
							extraType= Extra.TYPE.WALL_SIGN;
						}
						else if(block instanceof BlockBloodAltar2){
							extraType= Extra.TYPE.ALTAR_2;
						}
					else if(block instanceof BlockBloodAltar1){
							extraType= Extra.TYPE.ALTAR_1;
						}
						if(extraType!=null){

							TileEntity t=chunk.getTileEntityUnsafe(x, y, z);
							if(t!=null){
								Extra e=new Extra(extraType,new BlockList.BlockPosition(x,y-this.yCoord,z));
								e.retrieveExtra(t);
								extraList.add(e);
							}

						}



				}
			}
		}
		List<EntityPainting> paintings=new ArrayList<EntityPainting>();
		chunk.getEntitiesOfTypeWithinAAAB(EntityPainting.class, AxisAlignedBB.getBoundingBox(Integer.MIN_VALUE, this.yCoord + minY, Integer.MIN_VALUE, Integer.MAX_VALUE, worldObj.getActualHeight(), Integer.MAX_VALUE), paintings, null);
		for(EntityPainting p:paintings){
			Extra extra=new Extra(Extra.TYPE.PAINTING,new BlockList.BlockPosition(p.field_146063_b-(chunk.xPosition<<4),p.field_146064_c-this.yCoord,p.field_146062_d-(chunk.zPosition<<4)));
			extra.retrieveExtra(p);
			extraList.add(extra);
		}
		save();
		blockLists=null;
		extraList=null;
	}

	private void save(){
		Logger.i(TAG,"Saving template");
		for(BlockList bl:blockLists){
			bl.prepareForSave();
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(new BuildingTile(blockLists,extraList));
		Writer writer;
		try
		{
			File f=new File("vampirism/"+new Random().nextInt() + ".json");
			f.getParentFile().mkdirs();
			writer = new FileWriter(f);
			writer.write(json);
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void addBlock(Block b, int[] meta,int x, int y,int z){
		Iterator<BlockList> iterator=blockLists.iterator();
		while(iterator.hasNext()){
			BlockList bl=iterator.next();
			if(bl.equals(b,meta)){
				bl.addPosition(x,y,z);
				return;
			}
		}
		BlockList bl=new BlockList(b,meta);
		bl.addPosition(x,y,z);
		blockLists.add(bl);
	}

	private int[] rotate(int[] data, int rot){
		if(rot==0)return data;
		if(rot==1)return new int[]{data[1],data[2],data[3],data[0]};
		if(rot==2)return new int[]{data[2],data[3],data[0],data[1]};
		return new int[]{data[3],data[0],data[1],data[2]};

	}
	private int[] guessMetaForBlock(Block block,int meta){
		int[] metadata=null;
		try {
			if (block instanceof BlockStairs)
			{
				/**
				 * Used retrieve direction from meta
				 */
				int[] rotSet=new int[]{3,1,0,2};
				/**
				 * Represents rotation metadata for direction 0 (south)
				 */
				int[] normalSet = new int[]{2, 1, 3, 0};
				int[] upSet = new int[]{6, 5, 7, 4};

				if(meta>3){
					metadata=rotate(upSet,rotSet[meta-4]);
				}
				else{
					metadata=rotate(normalSet,rotSet[meta]);
				}
			}
			else if(block instanceof BlockLadder)
			{
				int[] rotSet=new int[]{0,0,0,2,3,1};

				int[] set= new int[]{2,5,3,4};
				metadata=rotate(set,rotSet[meta]);

			}else if(block instanceof BlockTrapDoor)
			{
				int[] rotSet=new int[]{0,2,3,1};
				int[] normalSet=new int[]{0,3,1,2};
				int[] upSet=new int[]{8,11,9,10};

				if(meta>7){
					metadata=rotate(upSet,rotSet[meta-8]);
				}
				else{
					metadata=rotate(normalSet,rotSet[meta]);
				}
			}else if(block instanceof BlockTorch)
			{
				int[] rotSet=new int[]{1,3,2,0};
				int[] set= new int[]{4,1,3,2};


				if(meta!=5){
					metadata=rotate(set,rotSet[meta-1]);
				}
			}else if(block instanceof BlockDoor)
			{
				int[] rotSet = new int[]{3,0,1,2};

				int[] set= new int[]{1,2,3,0};
				int[] openSet= new int[]{5,6,7,4};

				if(meta<4){
					metadata=rotate(set,rotSet[meta]);
				}
				else if(meta<8){
					metadata=rotate(openSet,rotSet[meta-4]);
				}
			}
			else if(block instanceof BlockCoffin){
				int[] set={8,9,10,11};
				if(meta>0){
					metadata=rotate(set,meta%4);
				}
			}
			else if(block instanceof BlockChest){
				int[] rotSet={0,2,3,1};
				int[] set={2,5,3,4};
				metadata=rotate(set,rotSet[meta-2]);
			}
			else if(block instanceof BlockFenceGate){
				int[] set={0,1,2,3};
				int[] setOpen={4,5,6,7};
				if(meta>3){
					metadata=rotate(setOpen,meta-4);
				}
				else{
					metadata=rotate(set,meta);
				}
			}
			else if(block instanceof BlockFurnace){
				int[] set={2,5,3,4};
				int[] rotSet={0,2,3,1};
				metadata=rotate(set,rotSet[meta-2]);
			}
			else if(Blocks.wall_sign.equals(block)){
				int[] set={2,5,3,4};
				int[] rotSet={0,2,3,1};
				metadata=rotate(set,rotSet[meta-2]);
			}
			else if(block instanceof BlockFenceGate){
				int[] set={0,1,2,3};
				int[] openSet={4,5,6,7};
				metadata=rotate(meta>3?openSet:set,meta);
			}
			else if(block instanceof BlockButton){
				if(meta>0&&meta<5){
					int[] set={3,2,4,1};
					int[] rotSet={3,1,0,2};
					metadata=rotate(set,rotSet[meta-1]);
				}
			}
			if(metadata!=null)return metadata;
		} catch (IndexOutOfBoundsException e) {
			Logger.w(TAG,"Failed to guess the right metadata for %s %d.",block,meta);
		}
		return new int[]{meta,meta,meta,meta};
	}
}
