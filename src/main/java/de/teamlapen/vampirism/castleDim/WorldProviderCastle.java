package de.teamlapen.vampirism.castleDim;

import de.teamlapen.vampirism.ModBiomes;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * World provider for castle dimension
 */
public class WorldProviderCastle extends WorldProvider {
	@Override public String getDimensionName() {
		return "Vampire castle";
	}

	@Override protected void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(ModBiomes.biomeVampireForest, 0.1F);
		this.dimensionId = VampirismMod.castleDimensionId;
	}

	@Override public IChunkProvider createChunkGenerator() {
		return new ChunkProviderCastle(worldObj, worldObj.getSeed());
	}

	@Override public boolean canRespawnHere() {
		return false;
	}

	public ChunkCoordinates getEntrancePortalLocation() {
		return new ChunkCoordinates(48, ChunkProviderCastle.MAX_Y_HEIGHT+1, 104);
	}

	public int getAverageGroundLevel() {
		return 12;
	}

	@Override public String getDepartMessage() {
		return "Leaving Dracula's castle";
	}

	@Override public String getWelcomeMessage() {
		return "Entering Dracula's castle";
	}

	@Override public long getWorldTime() {
		return 18000;
	}

	public boolean canCoordinateBeSpawn(int p_76566_1_, int p_76566_2_) {
		return this.worldObj.getTopBlock(p_76566_1_, p_76566_2_).getMaterial().blocksMovement();
	}

	@Override
	public boolean isDaytime() {
		return false;
	}
}
