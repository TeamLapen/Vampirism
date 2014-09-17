package de.teamlapen.vampirism.network;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.util.Helper;

public class SpawnParticlePacket implements IMessage{

	private String type;
	private double x,y,z,velX,velY,velZ;
	private int amount;
	
	
	/**
	 * Tells the client to spawn a particle. Arguments are the same as in {@link net.minecraft.world.World#spawnParticle}, only pamount is added
	 * @param ptype
	 * @param px
	 * @param py
	 * @param pz
	 * @param pvelx
	 * @param pvely
	 * @param pvelz
	 * @param pamount Amount of randomly spawned particle
	 */
	public SpawnParticlePacket(String ptype,double px,double py,double pz, double pvelx, double pvely, double pvelz, int pamount){
		type=ptype;
		x=px;
		y=py;
		z=pz;
		velX=pvelx;
		velY=pvely;
		velZ=pvelz;
		amount=pamount;
	}
	
	/**
	 * Dont use
	 */
	public SpawnParticlePacket(){
		
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag=ByteBufUtils.readTag(buf);
		type=tag.getString("type");
		x=tag.getDouble("x");
		y=tag.getDouble("y");
		z=tag.getDouble("z");
		velX=tag.getDouble("velX");
		velY=tag.getDouble("velY");
		velZ=tag.getDouble("velZ");
		amount=tag.getInteger("amount");
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag=new NBTTagCompound();
		tag.setString("type", type);
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setDouble("velX", velX);
		tag.setDouble("velY", velY);
		tag.setDouble("velZ", velZ);
		tag.setInteger("amount", amount);
		ByteBufUtils.writeTag(buf, tag);
		
	}
	
	public static class Handler implements IMessageHandler<SpawnParticlePacket, IMessage>{

		@Override
		public IMessage onMessage(SpawnParticlePacket message, MessageContext ctx) {
			
			if(message.type.equals("blood_eat")){
				Class[] paramtype=new Class[]{ItemStack.class,Integer.TYPE};
				ItemStack is=new ItemStack(Item.getItemById(260));
				Helper.Reflection.callMethod(EntityPlayer.class,Minecraft.getMinecraft().thePlayer, "updateItemUse", paramtype, new Object[]{is,message.amount} );
				return null;
			}
			WorldClient world=Minecraft.getMinecraft().theWorld;
			world.spawnParticle(message.type, message.x, message.y, message.z, message.velX, message.velY, message.velZ);
			for(int i=1;i<message.amount;i++){
					Random ran=world.rand;
					double x=message.x + (ran.nextGaussian());
					double y=message.y + (ran.nextGaussian());
					double z=message.z + (ran.nextGaussian());
					world.spawnParticle(message.type, x, y, z, ran.nextDouble(),ran.nextGaussian(),ran.nextGaussian());
			}
			return null;
		}
		
	}

}
