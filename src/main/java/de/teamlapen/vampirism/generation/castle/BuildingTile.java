package de.teamlapen.vampirism.generation.castle;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Max on 03.07.2015.
 */
public class BuildingTile {
	private List<BlockList> blocks;

	public  BuildingTile(List<BlockList> blocks){
		this.blocks=blocks;
	}

	public BuildingTile(){
		this.blocks=new LinkedList<BlockList>();
	}
	public void build(int cx,int cz,World world,int groundHeight,int rotation){
		Logger.d("Tile","Building tile at %d %d",cx,cz);
		for(BlockList l:blocks){
			List<BlockList.BlockPosition> pos=l.getPositions();
			for(BlockList.BlockPosition p:pos){
				p=rotatePosition(rotation,p);
				world.setBlock(cx+p.x,groundHeight+p.y,cz+p.z,l.block,l.getBlockMetaForRotation(rotation),2);
			}
		}
	}

	private BlockList.BlockPosition rotatePosition(int rotation,BlockList.BlockPosition pos){
		switch (rotation){
		case 1:
			return new BlockList.BlockPosition(15-pos.z,pos.y,pos.x);
		case 2:
			return new BlockList.BlockPosition(15-pos.x,pos.y,15-pos.z);
		case 3:
			return new BlockList.BlockPosition(pos.z,pos.y,15-pos.x);
		default:
			return pos;
		}
	}

	public void finishLoading() {
		for(BlockList l : blocks){
			l.finishLoad();
		}
	}
}
