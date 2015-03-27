package de.teamlapen.vampirism.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class Helper {
	public static class Reflection {

		@SuppressWarnings("unchecked")
		public static Object callMethod(Class cls, Object obj, String[] methodName, Class[] paramtype, Object[] param) {
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

		public static Object callMethod(Object obj, String[] methodName, Class[] paramtype, Object[] param) {
			return Reflection.callMethod(obj.getClass(), obj, methodName, paramtype, param);
		}

		public static Object getPrivateFinalField(Class cls, Object obj, String... fieldname) {
			try {
				Field privateStringField = ReflectionHelper.findField(cls, fieldname);
				return privateStringField.get(obj);
			} catch (Exception e) {
				Logger.e("Reflection", "Failed to get " + fieldname + " from " + obj.toString() + " of class " + cls.getCanonicalName(), e);
				return null;
			}
		}
		public static void setPrivateField(Class cls, Object obj, Object value, String... fieldname) {
			try {
				Field privateStringField = ReflectionHelper.findField(cls, fieldname);
				privateStringField.set(obj, value);
			} catch (Exception e) {
				Logger.e("Reflection", "Failed to get " + fieldname + " from " + obj.toString() + " of class " + cls.getCanonicalName(), e);
				return;
			}
		}
	}
	
	public static class Obfuscation{
		private static final HashMap<String,String[]> posNames=new HashMap<String,String[]>();
		
		private static final void add(String key,String... value){
			posNames.put(key, value);
		}
		public static final void fillMap(){
			
			add("EntityLiving/tasks","tasks","field_70714_bg");
			add("EntityLiving/targetTasks","targetTasks","field_70715_bh");
			add("EntityPlayer/updateItemUse","updateItemUse","func_71010_c");
			add("Minecraft/fileAssets","fileAssets","field_110446_Y");
		}
		
		public static String[] getPosNames(String key){
			return posNames.get(key);
		}
	}

	/**
	 * Gets players looking spot.
	 * 
	 * @param player
	 * @param restricts
	 *            Keeps distance to players block reach distance
	 * @return The position as a MovingObjectPosition, null if not existent cf:
	 *         https
	 *         ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs
	 *         /util/CommonUtils.java
	 */
	public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, boolean restrict) {
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
		if (player instanceof EntityPlayerMP && restrict) {
			distance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vector2 = vector1.addVector(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
		return player.worldObj.rayTraceBlocks(vector1, vector2);
	}
	
	public static List<Entity> spawnEntityInVillage(Village v,int max,String name,World world){
		//VillageVampire vv=VillageVampireData.get(world).getVillageVampire(v);
		List<Entity> list=new ArrayList<Entity>();
		Entity e=null;
		for (int i = 0; i < max; i++) {
			if(e==null){
				e=EntityList.createEntityByName(name, world);
			}
			if(spawnEntityInWorld(world,VillageVampire.getBoundingBox(v),e,5)){
				list.add(e);
				e=null;
			}
	
		}
		if(e!=null){
			e.setDead();
		}
		return list;
	}
	
	public static boolean spawnEntityInWorld(World world,AxisAlignedBB box,Entity e, int maxTry){
		boolean flag=false;
		int i=0;
		while(!flag&&i++<maxTry){
			ChunkCoordinates c=getRandomPosInBox(world,box);
			e.setPosition(c.posX,c.posY,c.posZ);
			if(!(e instanceof EntityLiving)||((EntityLiving)e).getCanSpawnHere() ){
				flag=true;
			}
		}
		if(flag){
			world.spawnEntityInWorld(e);
			return true;
		}
		else{
		}
		return false;
	}
	public static Entity spawnEntityInWorld(World world, AxisAlignedBB box,String name,int maxTry){
		Entity e=EntityList.createEntityByName(name, world);
		if(spawnEntityInWorld(world,box,e,maxTry)){
			return e;
		}
		else{
			e.setDead();
			return null;
		}
	}
	
	public static ChunkCoordinates getRandomPosInBox(World w,AxisAlignedBB box){
		int x=(int)box.minX+w.rand.nextInt((int)(box.maxX-box.minX)+1);
		int z=(int)box.minZ+w.rand.nextInt((int)(box.maxZ-box.minZ)+1);
		int y=w.getHeightValue(x, z)+1;
		if(y<box.minX||y>box.maxY){
			y=(int)box.minY+w.rand.nextInt((int)(box.maxY-box.minY)+1);
		}
		return new ChunkCoordinates(x,y,z);
	}
	
	@SideOnly(Side.SERVER)
	public static void sendPacketToPlayersAround(IMessage message,Entity e){
		VampirismMod.modChannel.sendToAllAround(message, new TargetPoint(e.dimension,e.posX,e.posY,e.posZ,100));
	}
}
