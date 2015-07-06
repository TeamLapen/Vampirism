package de.teamlapen.vampirism.generation.castle;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages and stores the position data for a world
 * @author Maxanier
 */
public class CastlePositionData extends WorldSavedData{

	private final static String IDENTIFIER = "castle_positions";
	List<Position> positions;
	private List<Position> fullyGeneratedPositions;
	boolean checked;
	public static CastlePositionData get(World world){
		CastlePositionData data= (CastlePositionData) world.perWorldStorage.loadData(CastlePositionData.class, IDENTIFIER);
		if(data==null){
			data = new CastlePositionData(IDENTIFIER);
			world.perWorldStorage.setData(IDENTIFIER,data);
		}
		return data;
	}
	public CastlePositionData(String identifier) {
		super(identifier);
		positions=new LinkedList<Position>();
		fullyGeneratedPositions=new LinkedList<Position>();
	}

	/**
	 * @return Whether the world was already checked for castle positions
	 */
	public boolean isChecked(){
		return checked;
	}

	public List<Position> getPositions(){
		return positions;
	}

	@Override public void readFromNBT(NBTTagCompound nbt) {
		checked=nbt.getBoolean("checked");
		if(checked){
			if(nbt.hasKey("positions")){
				NBTTagList list=nbt.getTagList("positions", 10);
				while(list.tagCount()>0){
					positions.add(createPositionFromNBT((NBTTagCompound) list.removeTag(0)));
				}
			}
			if(nbt.hasKey("fullpositions")){
				NBTTagList list=nbt.getTagList("fullpositions", 10);
				while(list.tagCount()>0){
					fullyGeneratedPositions.add(createPositionFromNBT((NBTTagCompound) list.removeTag(0)));
				}
			}
			Logger.d("Test","Loaded %d fully generated and %d other positions",fullyGeneratedPositions.size(),positions.size());
		}
	}

	@Override public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("checked",checked);
		if(checked){
			if(positions.size()>0){
				NBTTagList pos=new NBTTagList();
				NBTTagList fullPos=new NBTTagList();
				for(Position p:positions){
					if(p.fullyGenerated()){
						fullPos.appendTag(p.toNBT());
					}
					else {
						pos.appendTag(p.toNBT());
					}
				}
				nbt.setTag("positions",pos);
				nbt.setTag("fullpositions",fullPos);
			}
		}
	}

	/**
	 * Holds a castles positions information including size and tiles
	 */
	public static class Position extends ChunkCoordIntPair {
		private int sizeX;
		private int sizeZ;
		private int tileCount;
		private String[][] tiles;
		private int generated;

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		private int height=-1;
		public Position(int chunkX, int chunkY) {
			super(chunkX, chunkY);
		}

		public void setSize(int x,int y){
			sizeX=x;
			sizeZ =y;
			tileCount=x*y;
		}

		public int getSizeX() {
			return sizeX;
		}

		public int getSizeZ() {
			return sizeZ;
		}

		public boolean hasSize(){
			return sizeZ >0&&sizeX>0;
		}

		public boolean hasTiles(){
			return tiles!=null;
		}

		public void setTiles(String[][] tiles){
			this.tiles=tiles;
		}

		public void markGenerated(int cx,int cz){
			this.generated++;
		}

		public String getTileAt(int x,int z){
			if(!hasTiles())return "";
			return tiles[x][z];
		}

		public boolean fullyGenerated(){
			return generated==tileCount;
		}

		/**
		 * Checks if the given chunk coordinates are within this position
		 * @param cx
		 * @param cz
		 * @return
		 */
		public boolean isChunkInPosition(int cx,int cz){
			if(!hasSize())return false;
			if(cx>=this.chunkXPos&&cx<this.chunkXPos+sizeX){
				if(cz>=this.chunkZPos&&cz<this.chunkZPos+sizeZ){
					return true;
				}
			}
			return false;
		}

		private NBTTagCompound toNBT(){
			NBTTagCompound nbt=new NBTTagCompound();
			nbt.setIntArray("pos",new int[]{chunkXPos,chunkZPos,sizeX,sizeZ,height});
			if(hasTiles()&&!fullyGenerated()){
				String s="";
				for(int x=0;x<sizeX;x++){
					for(int z=0;z<sizeZ;z++){
						s+=tiles[x][z]+"/";
					}
				}
				s.substring(0,s.length()-1);
				nbt.setString("tiles",s);
			}
			nbt.setInteger("generated",generated);
			return nbt;
		}

		private void loadTilesFromString(String s){
			String[] parts=s.split("/");
			if(parts.length!=tileCount){
				Logger.w("CastlePos","Cannot load tiles due to wrong size");
			}
			else{
				tiles=new String[sizeX][sizeZ];
				for(int x=0;x<sizeX;x++){
					for(int z=0;z<sizeZ;z++){
						tiles[x][z]=parts[x*sizeZ+z];
					}
				}
			}
		}
	}

	private Position createPositionFromNBT(NBTTagCompound nbt){
		int[] pos=nbt.getIntArray("pos");
		Position p = new Position(pos[0],pos[1]);
		p.setSize(pos[2],pos[3]);
		p.setHeight(pos[4]);
		p.generated=nbt.getInteger("generated");
		if(nbt.hasKey("tiles")&&!p.fullyGenerated()){
			p.loadTilesFromString(nbt.getString("tiles"));
		}
		return p;
	}
}
