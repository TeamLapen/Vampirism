package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Used to tell the client it should render the screen reddish
 * @author Maxanier
 *
 */
public class ShaderPacket implements IMessage {
	public final static int SATURATION1=1;
	private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
	public static class Handler implements IMessageHandler<ShaderPacket, IMessage> {

		@Override
		public IMessage onMessage(ShaderPacket message, MessageContext ctx) {
			switch(message.type){
			case SATURATION1:
				activateShader(saturation1);
				break;
			default:
				Minecraft.getMinecraft().entityRenderer.deactivateShader();
			}
			return null;
		}

	}

	private int type;
	/**
	 * Dont use
	 */
	public ShaderPacket() {

	}


	public ShaderPacket(int type) {
		this.type=type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		type=tag.getInteger("type");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		ByteBufUtils.writeTag(buf, tag);

	}
	
	private static void activateShader(ResourceLocation s){
		EntityRenderer renderer=Minecraft.getMinecraft().entityRenderer;
		Minecraft mc=Minecraft.getMinecraft();
		if(OpenGlHelper.shadersSupported){
			if (renderer.theShaderGroup != null)
            {
                renderer.theShaderGroup.deleteShaderGroup();
            }
			try {
				renderer.theShaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), s);
				renderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
			} catch (JsonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
