package de.teamlapen.vampirism.generation.castle;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a chunksized building component
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
		int x=cx<<4;
		int z=cz<<4;
		Logger.d("Tile","Building tile at %d %d (%d %d %d) with rotation %d",cx,cz,x,groundHeight,z,rotation);
		boolean debugged=false;
		for (int i=x;i<x+16;i++){
			for(int j=z;j<z+16;j++){
				for(int k=groundHeight;k<groundHeight+15;k++){
					world.setBlockToAir(i,k,j);
				}
			}
		}
		for(BlockList l:blocks){
			List<BlockList.BlockPosition> pos=l.getPositions();
			for(BlockList.BlockPosition p:pos){
				p=rotatePosition(rotation,p);
				world.setBlock(x+p.x,groundHeight+p.y,z+p.z,l.block,l.getBlockMetaForRotation(rotation),2);
				if(!debugged){
					Logger.d("Tile","%d %d %d %s",x+p.x,groundHeight+p.y,z+p.z,l.block);
					debugged=true;
				}
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
