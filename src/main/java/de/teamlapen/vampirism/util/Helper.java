package de.teamlapen.vampirism.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.villages.VillageVampire;

public class Helper {
	public static class Obfuscation {
		private static final HashMap<String, String[]> posNames = new HashMap<String, String[]>();

		private static void add(String key, String... value) {
			posNames.put(key, value);
		}

		public static void fillMap() {
			add("EntityPlayer/updateItemUse", "updateItemUse", "func_71010_c");
			add("EntityPlayer/setSize", "setSize", "func_70105_a");
			add("EntityPlayer/sleeping", "sleeping", "field_71083_bS");
			add("EntityPlayer/sleepTimer", "sleepTimer", "field_71076_b");
			add("Minecraft/fileAssets", "fileAssets", "field_110446_Y");
			add("TileEntityBeacon/field_146015_k", "field_146015_k");
			add("Entity/setSize", "setSize", "func_70105_a");
		}

		public static String[] getPosNames(String key) {
			return posNames.get(key);
		}
	}

	public static class Reflection {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Object callMethod(Class cls, Object obj, String[] methodName, Class[] paramtype, Object... param) {
			if (param != null && paramtype.length != param.length) {
				Logger.w("ReflectCallMethod", "Param count doesnt fit paramtype count");
				return null;
			}

			try {
				Method method = ReflectionHelper.findMethod(cls, obj, methodName, paramtype);
				return method.invoke(obj, param);

			} catch (Exception e) {
				Logger.e("ReflectCallMethod", "Failed to invoke method");
				e.printStackTrace();
				return null;
			}
		}

		@SuppressWarnings("rawtypes")
		public static Object callMethod(Object obj, String[] methodName, Class[] paramtype, Object... param) {
			return Reflection.callMethod(obj.getClass(), obj, methodName, paramtype, param);
		}

		/**
		 * Create Class Array
		 * 
		 * @param objects
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		public static Class[] createArray(Class... objects) {
			return objects;
		}

		@SuppressWarnings("rawtypes")
		public static Object getPrivateFinalField(Class cls, Object obj, String... fieldname) {
			try {
				Field privateStringField = ReflectionHelper.findField(cls, fieldname);
				return privateStringField.get(obj);
			} catch (Exception e) {
				Logger.e("Reflection", "Failed to get " + Arrays.toString(fieldname) + " from " + obj.toString() + " of class " + cls.getCanonicalName(), e);
				return null;
			}
		}

		@SuppressWarnings("rawtypes")
		public static void setPrivateField(Class cls, Object obj, Object value, String... fieldname) {
			try {
				Field privateStringField = ReflectionHelper.findField(cls, fieldname);
				privateStringField.set(obj, value);
			} catch (Exception e) {
				Logger.e("Reflection", "Failed to get " + Arrays.toString(fieldname) + " from " + obj.toString() + " of class " + cls.getCanonicalName(), e);
				return;
			}
		}
	}

	public static String entityToString(Entity e) {
		if (e == null) {
			return "Entity is null";
		}
		return e.toString();// +" at "+e.posX+" "+e.posY+" "+e.posZ+" Id "+e.getEntityId();
	}

	/**
	 * Gets players looking spot (blocks only).
	 * 
	 * @param player
	 * @param restriction
	 *            Max distance or 0 for player reach distance or -1 for not restricted
	 * @return The position as a MovingObjectPosition, null if not existent cf: https ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs /util/CommonUtils.java
	 */
	public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, double restriction) {
		float scale = 1.0F;
		float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scale;
		float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scale;
		double x = player.prevPosX + (player.posX - player.prevPosX) * scale;
		double y = player.prevPosY + (player.posY - player.prevPosY) * scale + 1.62D - player.yOffset;
		double z = player.prevPosZ + (player.posZ - player.prevPosZ) * scale;
		Vec3 vector1 = Vec3.createVectorHelper(x, y, z);
		float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
		float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
		float pitchAdjustedSinYaw = sinYaw * cosPitch;
		float pitchAdjustedCosYaw = cosYaw * cosPitch;
		double distance = 500D;
		if (restriction == 0 && player instanceof EntityPlayerMP) {
			distance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		} else if (restriction > 0) {
			distance = restriction;
		}

		Vec3 vector2 = vector1.addVector(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
		return player.worldObj.rayTraceBlocks(vector1, vector2);
	}

	public static ChunkCoordinates getRandomPosInBox(World w, AxisAlignedBB box) {
		int x = (int) box.minX + w.rand.nextInt((int) (box.maxX - box.minX) + 1);
		int z = (int) box.minZ + w.rand.nextInt((int) (box.maxZ - box.minZ) + 1);
		int y = w.getHeightValue(x, z) + 1;
		if (y < box.minX || y > box.maxY) {
			y = (int) box.minY + w.rand.nextInt((int) (box.maxY - box.minY) + 1);
		}
		return new ChunkCoordinates(x, y, z);
	}

	@SideOnly(Side.SERVER)
	public static void sendPacketToPlayersAround(IMessage message, Entity e) {
		VampirismMod.modChannel.sendToAllAround(message, new TargetPoint(e.dimension, e.posX, e.posY, e.posZ, 100));
	}

	public static Entity spawnEntityBehindEntity(EntityLivingBase p, String name) {
		EntityLiving e = (EntityLiving) EntityList.createEntityByName(name, p.worldObj);
		float yaw = p.rotationYawHead;
		float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		int distance = 2;
		double x = p.posX + sinYaw * distance;
		double z = p.posZ + cosYaw * distance;

		e.setPosition(x, p.posY, z);

		if (e.getCanSpawnHere()) {
			p.worldObj.spawnEntityInWorld(e);
			return e;
		} else {
			e.setPosition(x, p.worldObj.getHeightValue((int) Math.round(x), (int) Math.round(z)), z);
			if (e.getCanSpawnHere()) {
				p.worldObj.spawnEntityInWorld(e);
				return e;
			}
		}
		e.setDead();
		return null;
	}

	public static List<Entity> spawnEntityInVillage(Village v, int max, String name, World world) {
		// VillageVampire vv=VillageVampireData.get(world).getVillageVampire(v);
		List<Entity> list = new ArrayList<Entity>();
		Entity e = null;
		for (int i = 0; i < max; i++) {
			if (e == null) {
				e = EntityList.createEntityByName(name, world);
			}
			if (spawnEntityInWorld(world, VillageVampire.getBoundingBox(v), e, 5)) {
				list.add(e);
				e = null;
			}

		}
		if (e != null) {
			e.setDead();
		}
		return list;
	}

	public static boolean spawnEntityInWorld(World world, AxisAlignedBB box, Entity e, int maxTry) {
		boolean flag = false;
		int i = 0;
		while (!flag && i++ < maxTry) {
			ChunkCoordinates c = getRandomPosInBox(world, box);
			e.setPosition(c.posX, c.posY, c.posZ);
			if (!(e instanceof EntityLiving) || ((EntityLiving) e).getCanSpawnHere()) {
				flag = true;
			}
		}
		if (flag) {
			world.spawnEntityInWorld(e);
			return true;
		} else {
		}
		return false;
	}

	public static Entity spawnEntityInWorld(World world, AxisAlignedBB box, String name, int maxTry) {
		Entity e = EntityList.createEntityByName(name, world);
		if (spawnEntityInWorld(world, box, e, maxTry)) {
			return e;
		} else {
			e.setDead();
			return null;
		}
	}

	public static void spawnParticlesAroundEntity(EntityLivingBase e, String particle, double maxDistance, int amount) {
		if (!e.worldObj.isRemote) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("particle", particle);
			nbt.setInteger("id", e.getEntityId());
			nbt.setDouble("distance", maxDistance);
			Helper.sendPacketToPlayersAround(new SpawnCustomParticlePacket(2, 0, 0, 0, amount, nbt), e);
			return;
		}
		short short1 = (short) amount;
		for (int l = 0; l < short1; ++l) {
			double d6 = l / (short1 - 1.0D) - 0.5D;
			float f = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
			float f1 = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
			float f2 = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
			double d7 = e.posX + (maxDistance) * d6 + (e.getRNG().nextDouble() - 0.5D) * e.width * 2.0D;
			double d8 = e.posY + (maxDistance / 2) * d6 + e.getRNG().nextDouble() * e.height;
			double d9 = e.posZ + (maxDistance) * d6 + (e.getRNG().nextDouble() - 0.5D) * e.width * 2.0D;
			e.worldObj.spawnParticle(particle, d7, d8, d9, f, f1, f2);
		}
	}

	/**
	 * Teleports the entity
	 * 
	 * @param entity
	 * @param x
	 * @param y
	 * @param z
	 * @param sound
	 *            If a teleport sound should be played
	 * @return Wether the teleport was successful or not
	 */
	public static boolean teleportTo(EntityLiving entity, double x, double y, double z, boolean sound) {
		double d3 = entity.posX;
		double d4 = entity.posY;
		double d5 = entity.posZ;
		entity.posX = x;
		entity.posY = y;
		entity.posZ = z;
		boolean flag = false;
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);

		if (entity.worldObj.blockExists(i, j, k)) {
			boolean flag1 = false;

			while (!flag1 && j > 0) {
				Block block = entity.worldObj.getBlock(i, j - 1, k);
				if (block.getMaterial().blocksMovement())
					flag1 = true;
				else {
					--entity.posY;
					--j;
				}
			}

			if (flag1) {
				entity.setPosition(entity.posX, entity.posY, entity.posZ);

				if (entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() && !entity.worldObj.isAnyLiquid(entity.boundingBox))
					flag = true;
			}
		}

		if (!flag) {
			entity.setPosition(d3, d4, d5);
			return false;
		} else {
			short short1 = 128;

			for (int l = 0; l < short1; ++l) {
				double d6 = l / (short1 - 1.0D);
				float f = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
				float f1 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
				float f2 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (entity.posX - d3) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.width * 2.0D;
				double d8 = d4 + (entity.posY - d4) * d6 + entity.getRNG().nextDouble() * entity.height;
				double d9 = d5 + (entity.posZ - d5) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.width * 2.0D;
				entity.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
			}

			if (sound) {
				// TODO different sound (bang?)
				entity.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
				entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
			}

			return true;
		}
	}

	/**
	 *
	 * @param ran
	 * @return Random double between -1 and 1
	 */
	public static double rnd1n1(Random ran){
		return (ran.nextDouble()-0.5D)*2;
	}

}
