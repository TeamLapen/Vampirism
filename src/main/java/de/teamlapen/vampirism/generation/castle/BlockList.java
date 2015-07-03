package de.teamlapen.vampirism.generation.castle;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds positions for a single block type
 * @author Maxanier
 */
public class BlockList {
	public Block block;
	public String blockId;
	private final int[] meta;

	private LinkedList<BlockPosition> blockPositions;
	public BlockList(Block block,int[] meta){
		this.block=block;
		blockPositions =new LinkedList<BlockPosition>();
		this.meta=meta;
	}

	public void addPosition(int x,int y,int z){
		blockPositions.add(new BlockPosition(x,y,z));
	}

	public List<BlockPosition> getPositions(){
		return blockPositions;
	}

	public int getBlockMetaForRotation(int rotation){
		if(rotation>3)rotation=0;
		return meta[rotation];
	}

	public static class BlockPosition {
		public final int x,y,z;

		public BlockPosition(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public void prepareForSave(){
		blockId=getPairedIdForBlock(block);
		block=null;
	}

	public void finishLoad(){
		block=getBlockForString(blockId);

		if(block==null){
			Logger.w("BlockList","Could not find block %s",blockId);
			block= Blocks.stone;
		}
		blockId=null;
	}

	public static String getPairedIdForBlock(Block block)
	{
		GameRegistry.UniqueIdentifier un = GameRegistry.findUniqueIdentifierFor(block);
		String name = "";

		if (un != null)
		{
			name = un.modId + ":" + un.name;
		}

		return name;
	}

	public static Block getBlockForString(String str)
	{
		String[] parts = str.split(":");
		String modId = parts[0];
		String name = parts[1];
		return GameRegistry.findBlock(modId, name);
	}

	public boolean equals(Block b,int[] meta){
		return b.equals(block)&& Arrays.equals(this.meta,meta);
	}
}
