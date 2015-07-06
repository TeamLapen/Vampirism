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
 * All positions are relative to the block with the lowest x and z values within a chunk and at ground height
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

	/**
	 * Add a position for this block type.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addPosition(int x,int y,int z){
		blockPositions.add(new BlockPosition(x,y,z));
	}

	public List<BlockPosition> getPositions(){
		return blockPositions;
	}

	/**
	 *
	 * @param rotation
	 * @return The metadata of this block rotated by the given rotation
	 */
	public int getBlockMetaForRotation(int rotation){
		if(rotation>3)rotation=0;
		return meta[rotation];
	}

	/**
	 * Simple class to hold relative x,y,z positions
	 */
	public static class BlockPosition {
		public final int x,y,z;

		public BlockPosition(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	/**
	 * Converts the block instance to its string id
	 */
	public void prepareForSave(){
		blockId=getPairedIdForBlock(block);
		block=null;
	}

	/**
	 * Converts the saved block id string back to an instance of the block
	 */
	public void finishLoad(){
		block=getBlockForString(blockId);

		if(block==null){
			Logger.w("BlockList","Could not find block %s",blockId);
			block= Blocks.stone;
		}
		blockId=null;
	}

	/**
	 *
	 * @param block
	 * @return The unique id for the given block
	 */
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

	/**
	 *
	 * @param str
	 * @return The block corresponding to the given unique id
	 */
	public static Block getBlockForString(String str)
	{
		String[] parts = str.split(":");
		String modId = parts[0];
		String name = parts[1];
		return GameRegistry.findBlock(modId, name);
	}

	/**
	 * Checks if this BlockList represents the given blocktype
	 * @param b
	 * @param meta
	 * @return
	 */
	public boolean equals(Block b,int[] meta){
		return b.equals(block)&& Arrays.equals(this.meta,meta);
	}
}
