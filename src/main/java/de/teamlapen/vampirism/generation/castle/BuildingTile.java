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
	private List<Extra> extras;
	public  BuildingTile(List<BlockList> blocks,List<Extra> extras){
		this.blocks=blocks;
		this.extras=extras;
	}

	public BuildingTile(){
		this.blocks=new LinkedList<BlockList>();
		this.extras=new LinkedList<Extra>();
	}
	public void build(int cx,int cz,World world,int groundHeight,int rotation){
		int x=cx<<4;
		int z=cz<<4;
//		Logger.d("Tile","Building tile at %d %d (%d %d %d) with rotation %d",cx,cz,x,groundHeight,z,rotation);
		for(BlockList l:blocks){
			List<BlockList.BlockPosition> pos=l.getPositions();
			for(BlockList.BlockPosition p:pos){
				p=rotatePosition(rotation,p);
				world.setBlock(x+p.x,groundHeight+p.y,z+p.z,l.block,l.getBlockMetaForRotation(rotation),2);
			}
		}
		for(Extra extra:extras){
			BlockList.BlockPosition p=extra.pos;
			p=rotatePosition(rotation,p);
			extra.applyExtra(world.getTileEntity(x+p.x,groundHeight+p.y,z+p.z));
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
