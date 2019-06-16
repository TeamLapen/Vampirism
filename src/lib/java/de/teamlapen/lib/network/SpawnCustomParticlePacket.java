package de.teamlapen.lib.network;

import de.teamlapen.lib.VampLib;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.Random;
import java.util.function.Supplier;


public class SpawnCustomParticlePacket implements IMessage {

    static void encode(SpawnCustomParticlePacket msg, PacketBuffer buf) {
        buf.writeCompoundTag(msg.nbt);
    }

    static SpawnCustomParticlePacket decode(PacketBuffer buf) {
        return new SpawnCustomParticlePacket(buf.readCompoundTag());
    }


    public static void handle(final SpawnCustomParticlePacket pkt, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        NBTTagCompound nbt = pkt.nbt;
        double posX = nbt.getDouble("x");
        double posY = nbt.getDouble("y");
        double posZ = nbt.getDouble("z");
        ResourceLocation particle = new ResourceLocation(nbt.getString("id"));
        NBTTagCompound data = nbt.getCompound("data");
        int count = nbt.getInt("count"); //Defaults to 0
        double maxDist = nbt.getDouble("maxdist"); //Defaults to 0
        Validate.notNull(ctx.getSender());
        ctx.enqueueWork(() -> { //Execute on main thread
            if (count > 0) {
                Random random = ctx.getSender().getRNG();
                VampLib.proxy.getParticleHandler().spawnParticles(ctx.getSender().getEntityWorld(), particle, posX, posY, posZ, count, maxDist, random, data);

            } else {
                VampLib.proxy.getParticleHandler().spawnParticle(ctx.getSender().getEntityWorld(), particle, posX, posY, posZ, data);
            }
        });
        ctx.setPacketHandled(true);
    }


    private NBTTagCompound nbt;

    private SpawnCustomParticlePacket(NBTTagCompound tag) {
        this.nbt = tag;
    }

    public SpawnCustomParticlePacket(ResourceLocation particle, double posX, double posY, double posZ, NBTTagCompound param, int count, double maxdist) {
        this(particle, posX, posY, posZ, param);
        nbt.putInt("count", count);
        nbt.putDouble("maxdist", maxdist);
    }

    public SpawnCustomParticlePacket(ResourceLocation particle, double posX, double posY, double posZ, NBTTagCompound param) {
        nbt = new NBTTagCompound();
        nbt.putDouble("x", posX);
        nbt.putDouble("y", posY);
        nbt.putDouble("z", posZ);
        nbt.putString("id", particle.toString());
        nbt.put("data", param);
    }
}
