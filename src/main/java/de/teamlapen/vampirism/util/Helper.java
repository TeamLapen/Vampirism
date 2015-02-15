package de.teamlapen.vampirism.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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
}
