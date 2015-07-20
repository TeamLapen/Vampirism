package de.teamlapen.vampirism.castleDim;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

/**
 * Teleporter for the castle dimension
 */
public class TeleporterCastle extends Teleporter {
	private final WorldServer server;
	private final boolean toCastle;
	public TeleporterCastle(WorldServer worldServer) {
		super(worldServer);
		this.server=worldServer;
		toCastle=server.provider.dimensionId==VampirismMod.castleDimensionId;
	}

	@Override public void placeInPortal(Entity entity, double p_77185_2_, double p_77185_4_, double p_77185_6_, float p_77185_8_) {
		if(!toCastle){
			Logger.t("loaded %s", StringUtils.join(server.theChunkProviderServer.loadedChunks,","));
			Logger.t("loaded %s", StringUtils.join(server.playerEntities,","));
			if(server.playerEntities.size()==0){
				server.theChunkProviderServer.loadedChunks.clear();
				server.theChunkProviderServer.loadedChunkHashMap=new LongHashMap();
				DimensionManager.unloadWorld(VampirismMod.castleDimensionId);
			}
		}
		ChunkCoordinates chunkCoordinates=null;
		if(entity instanceof EntityPlayer){
			NBTTagCompound extra=VampirePlayer.get((EntityPlayer) entity).getExtraDataTag();
			if(toCastle){
				extra.setIntArray("teleporter_castle_old",new int[]{ MathHelper.floor_double(entity.posX),MathHelper.floor_double(entity.posY),MathHelper.floor_double(entity.posZ)});
			}
			else{

				int[] old=extra.getIntArray("teleporter_castle_old");
				extra.removeTag("teleporter_castle_old");
				if(old!=null){
					chunkCoordinates=new ChunkCoordinates(old[0],old[1],old[2]);
				}
			}
		}
		if(chunkCoordinates==null){
			if(toCastle)chunkCoordinates=server.getEntrancePortalLocation();
			if(!toCastle)chunkCoordinates=server.getSpawnPoint();
		}


		double x = (double)chunkCoordinates.posX-0.5D;
		entity.posY = (double)chunkCoordinates.posY+0.1D;
		double z = (double)chunkCoordinates.posZ;
		entity.setLocationAndAngles(x, entity.posY, z, 180.0F, 0.0F);

		if (entity.isEntityAlive()) {
			server.updateEntityWithOptionalForce(entity, false);
		}
	}
}
