package de.teamlapen.vampirism.tileEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.generation.castle.BlockList;
import de.teamlapen.vampirism.generation.castle.BuildingTile;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Max on 03.07.2015.
 */
public class TileEntityTemplateGenerator extends TileEntity {
	private List<BlockList> blockLists;
	private final String TAG="TETemplateGenerator";
	public void onActivated(int minX){
		Logger.i(TAG,"Activated with minX %d",minX);
		blockLists=new LinkedList<BlockList>();
		Chunk chunk=
				this.getWorldObj().getChunkFromBlockCoords(this.xCoord, this.zCoord);

		for(int x=0;x<16;x++){
			for(int z=0;z<16;z++){
				for(int y=this.yCoord+minX;y<worldObj.getActualHeight();y++){
					Block block=chunk.getBlock(x, y, z);
					if(block.getMaterial()!= Material.air&&!block.equals(Blocks.sand)&&!block.equals(ModBlocks.templateGenerator)){
						int m=chunk.getBlockMetadata(x, y, z);
						int[] meta=guessMetaForBlock(block,m);
						addBlock(block,meta,x,y-this.yCoord,z);
					}


				}
			}
		}
		save();
		blockLists=null;
	}

	private void save(){
		Logger.i(TAG,"Saving template");
		for(BlockList bl:blockLists){
			bl.prepareForSave();
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(new BuildingTile(blockLists));
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
	private int[] guessMetaForBlock(Block block,int meta){
		return new int[]{meta,meta,meta,meta};
	}
}
