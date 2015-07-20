package de.teamlapen.vampirism.castleDim;

import de.teamlapen.vampirism.VampireLordData;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * Teleporter for the castle dimension
 */
public class TeleporterCastle extends Teleporter {
	private final WorldServer server;
	private final boolean toCastle;

	public TeleporterCastle(WorldServer worldServer) {
		super(worldServer);
		this.server = worldServer;
		toCastle = server.provider.dimensionId == VampirismMod.castleDimensionId;
	}

	@Override public void placeInPortal(Entity entity, double p_77185_2_, double p_77185_4_, double p_77185_6_, float p_77185_8_) {
		if (toCastle && entity instanceof EntityPlayer) {
			handleRegeneration();
			server.getPlayerManager().filterChunkLoadQueue((EntityPlayerMP) entity);
		}
		ChunkCoordinates chunkCoordinates = null;
		if (entity instanceof EntityPlayer) {
			NBTTagCompound extra = VampirePlayer.get((EntityPlayer) entity).getExtraDataTag();
			if (toCastle) {
				extra.setIntArray("teleporter_castle_old", new int[] { MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ) });
			} else {

				int[] old = extra.getIntArray("teleporter_castle_old");
				extra.removeTag("teleporter_castle_old");
				if (old != null) {
					chunkCoordinates = new ChunkCoordinates(old[0], old[1], old[2]);
				}
			}
		}
		if (chunkCoordinates == null) {
			if (toCastle)
				chunkCoordinates = server.getEntrancePortalLocation();
			if (!toCastle)
				chunkCoordinates = server.getSpawnPoint();
		}

		double x = (double) chunkCoordinates.posX - 0.5D;
		entity.posY = (double) chunkCoordinates.posY + 0.1D;
		double z = (double) chunkCoordinates.posZ;
		entity.setLocationAndAngles(x, entity.posY, z, 180.0F, 0.0F);

		if (entity.isEntityAlive()) {
			server.updateEntityWithOptionalForce(entity, false);
		}
	}

	public void handleRegeneration() {
		if (!VampireLordData.get(server).shouldRegenerateCastleDim())
			return;
		if (server.theChunkProviderServer.loadedChunks.size() > 0) {
			Logger.w("Teleporter", "Cannot regenerate the castle dimension, since there are chunks loaded");
			return;
		}
		CastlePositionData.reset(server);
		server.unloadEntities(server.getLoadedEntityList());
		server.theChunkProviderServer.provideChunk(2, 6);
		server.theChunkProviderServer.provideChunk(3, 6);
		for (int x = 0; x < 6; x++) {
			for (int z = 0; z < 6; z++) {
				server.theChunkProviderServer.provideChunk(x, z);
				Chunk newC = server.theChunkProviderServer.currentChunkProvider.provideChunk(x, z);
				Chunk old = (Chunk) server.theChunkProviderServer.loadedChunkHashMap.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
				if (old != null) {


					old.onChunkUnload();
//					for(List l:old.entityLists){
//						for(Object o:l){
//							((Entity)o).setDead();
//						}
//					}
					server.theChunkProviderServer.loadedChunks.remove(old);
				}
				server.theChunkProviderServer.loadedChunks.add(newC);
				server.theChunkProviderServer.loadedChunkHashMap.add(ChunkCoordIntPair.chunkXZ2Int(x, z), newC);
				newC.onChunkLoad();
				//WorldGenVampirism.castleGenerator.checkBiome(server, x, z, server.rand, true);
				//server.getPlayerManager().markBlockForUpdate(x<<4,15,z<<4); not necessary
			}
		}
		//server.theChunkProviderServer.unloadAllChunks(); not necessary
	}
}
