package de.teamlapen.vampirism.util;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

public class DifficultyCalculator {

	public static class Difficulty {
		public final int minLevel, maxLevel, avgLevel;

		private Difficulty(int mil, int mal, int al) {
			this.minLevel = mil;
			this.maxLevel = mal;
			this.avgLevel = al;
		}

		public boolean isZero() {
			return (minLevel == 0 && maxLevel == 0);
		}

		@Override
		public String toString() {
			return "Difficulty: min_" + minLevel + " max_" + maxLevel + " avg_" + avgLevel;
		}
	}

	public static interface IAdjustableLevel {
		/**
		 * @return The current level
		 */
		public int getLevel();

		/**
		 * @return Maximal existent level
		 */
		public int getMaxLevel();

		/**
		 * Set the level
		 * 
		 * @param level
		 */
		public void setLevel(int level);

		/**
		 * Calculate a (random) level under consideration of the given difficulty
		 * 
		 * @param d
		 * @return Can be under min or over max level
		 */
		public int suggestLevel(Difficulty d);

	}

	private static Difficulty calculateDifficulty(List<EntityPlayer> list) {
		if (list == null || list.isEmpty()) {
			return new Difficulty(0, 0, 0);
		}
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		int sum = 0;
		for (EntityPlayer p : list) {
			int l = VampirePlayer.get(p).getLevel();
			if (l < min) {
				min = l;
			}
			if (l > max) {
				max = l;
			}
			sum += l;
		}
		return new Difficulty(min, max, Math.round(((float) sum) / (float) list.size()));
	}

	public static Difficulty getGlobalDifficulty() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			throw new IllegalStateException("You can only use this method on server side");
		}
		return calculateDifficulty(MinecraftServer.getServer().getConfigurationManager().playerEntityList);
	}

	public static Difficulty getLocalDifficulty(World w, double cX, double cZ, int r) {
		List<EntityPlayer> list = w.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(cX - r, 0, cZ - r, cX + r, 256, cZ + r));
		return calculateDifficulty(list);
	}

	public static Difficulty getWorldDifficulty(World w) {
		return calculateDifficulty(w.playerEntities);
	}
}
