package de.teamlapen.vampirism.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Helper {
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
	
	public static class Reflection{
		public static Object callMethod(Object obj,String methodName,Class[] paramtype,Object[] param){
			return Reflection.callMethod(obj.getClass(), obj, methodName, paramtype, param);
		}
		
		@SuppressWarnings("unchecked")
		public static Object callMethod(Class cls,Object obj,String methodName,Class[] paramtype,Object[] param){
			if(param!=null && paramtype.length!=param.length){
				Logger.w("ReflectCallMethod", "Param count doesnt fit paramtype count");
				return null;
			}
			
			try {
				Method method=cls.getDeclaredMethod(methodName, paramtype);
				method.setAccessible(true);
				return method.invoke(obj, param);
				
			} catch (Exception e) {
				Logger.e("ReflectCallMethod", "Failed to invoke method");
				e.printStackTrace();
				return null;
			}
		}
	}
}
