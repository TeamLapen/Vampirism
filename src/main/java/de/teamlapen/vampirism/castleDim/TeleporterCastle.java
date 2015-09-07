package de.teamlapen.vampirism.castleDim;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.VampireLordData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * Teleporter for the castle dimension
 */
public class TeleporterCastle extends Teleporter {
	private final WorldServer server;
	private final boolean toCastle;

	public TeleporterCastle(WorldServer worldServer) {
		super(worldServer);
		this.server = worldServer;
		toCastle = server.provider.getDimensionId() == VampirismMod.castleDimensionId;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw) {
		if (toCastle && entity instanceof EntityPlayer) {
			handleRegeneration();
			server.getPlayerManager().filterChunkLoadQueue((EntityPlayerMP) entity);
		}
		BlockPos blockPos = null;
		if (entity instanceof EntityPlayer) {
			NBTTagCompound extra = VampirePlayer.get((EntityPlayer) entity).getExtraDataTag();
			if (toCastle) {
				extra.setIntArray("teleporter_castle_old", new int[] { MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ) });
			} else {

				int[] old = extra.getIntArray("teleporter_castle_old");
				extra.removeTag("teleporter_castle_old");
				if (old != null&&old.length==3) {
					blockPos = new BlockPos(old[0], old[1], old[2]);
				}
			}
		}
		if (blockPos == null) {
			if (toCastle)
				blockPos = server.provider.getSpawnCoordinate();
			if (!toCastle)
				blockPos = server.getSpawnPoint();
		}

		double x = (double) blockPos.getZ() - 0.5D;
		entity.posY = (double) blockPos.getY() + 0.1D;
		double z = (double) blockPos.getZ();
		entity.setLocationAndAngles(x, entity.posY, z, 180.0F, 0.0F);

		if (entity.isEntityAlive()) {
			server.updateEntityWithOptionalForce(entity, false);
		}
	}


	private void handleRegeneration() {
		if (!VampireLordData.get(server).shouldRegenerateCastleDim())
			return;
		if (server.theChunkProviderServer.loadedChunks.size() > 0) {
			Logger.w("Teleporter", "Cannot regenerate the castle dimension, since there are chunks loaded");
			return;
		}
		CastlePositionData.reset(server);
		server.unloadEntities(server.loadedEntityList);
		server.theChunkProviderServer.provideChunk(2, 6);
		server.theChunkProviderServer.provideChunk(3, 6);
		for (int x = 0; x < 6; x++) {
			for (int z = 0; z < 6; z++) {
				server.theChunkProviderServer.provideChunk(x, z);
				Chunk newC = server.theChunkProviderServer.serverChunkGenerator.provideChunk(x, z);
				Chunk old = (Chunk) server.theChunkProviderServer.id2ChunkMap.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
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
				server.theChunkProviderServer.id2ChunkMap.add(ChunkCoordIntPair.chunkXZ2Int(x, z), newC);
				newC.onChunkLoad();
				//WorldGenVampirism.castleGenerator.checkBiome(server, x, z, server.rand, true);
				//server.getPlayerManager().markBlockForUpdate(x<<4,15,z<<4); not necessary
			}
		}
		VampireLordData.get(server).setRegenerateCastleDim(false);
		//server.theChunkProviderServer.unloadAllChunks(); not necessary
	}
}
