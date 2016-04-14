package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.network.AbstractClientMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Random;


public class SpawnCustomParticlePacket implements IMessage {

    private NBTTagCompound nbt;

    public SpawnCustomParticlePacket() {

    }

    public SpawnCustomParticlePacket(ResourceLocation particle, double posX, double posY, double posZ, NBTTagCompound param, int count, double maxdist) {
        this(particle, posX, posY, posZ, param);
        nbt.setInteger("count", count);
        nbt.setDouble("maxdist", maxdist);
    }

    public SpawnCustomParticlePacket(ResourceLocation particle, double posX, double posY, double posZ, NBTTagCompound param) {
        nbt = new NBTTagCompound();
        nbt.setDouble("x", posX);
        nbt.setDouble("y", posY);
        nbt.setDouble("z", posZ);
        nbt.setString("id", particle.toString());
        nbt.setTag("data", param);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class Handler extends AbstractClientMessageHandler<SpawnCustomParticlePacket> {

        @Override
        public IMessage handleClientMessage(EntityPlayer player, SpawnCustomParticlePacket message, MessageContext ctx) {
            NBTTagCompound nbt = message.nbt;
            double posX = nbt.getDouble("x");
            double posY = nbt.getDouble("y");
            double posZ = nbt.getDouble("z");
            ResourceLocation particle = new ResourceLocation(nbt.getString("id"));

            NBTTagCompound data = nbt.getCompoundTag("data");
            if (nbt.hasKey("count")) {
                int count = nbt.getInteger("count");
                double maxDist = nbt.getDouble("maxdist");
                Random random = player.getRNG();
                VampLib.proxy.getParticleHandler().spawnParticles(player.worldObj, particle, posX, posY, posZ, count, maxDist, random, data);

            } else {
                VampLib.proxy.getParticleHandler().spawnParticle(player.worldObj, particle, posX, posY, posZ, data);
            }
            return null;
        }

        @Override
        protected boolean handleOnMainThread() {
            return true;
        }
    }
}
