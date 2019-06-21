package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet that syncs selected config values
 */
public class SyncConfigPacket implements IMessage {//TODO @maxanier
    public static SyncConfigPacket createSyncConfigPacket() {
        SyncConfigPacket packet = new SyncConfigPacket();
        packet.loadConfig();
        return packet;
    }

    private NBTTagCompound nbt;

    /**
     * Don't use
     */
    public SyncConfigPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    @OnlyIn(Dist.CLIENT)
    private void applyConfig() {
        if (UtilLib.isSameInstanceAsServer()) {
            VampirismMod.log.d("SyncConfigPacket", "Not applying as same instance as server");
            return;
        }
        Configs.readFromNBTClient(nbt);
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).readFromNBTClient(nbt);
    }

    private void loadConfig() {
        nbt = new NBTTagCompound();
        Configs.writeToNBTServer(nbt);
        ((SundamageRegistry) VampirismAPI.sundamageRegistry()).writeToNBTServer(nbt);
    }

    public static class Handler extends AbstractClientMessageHandler<SyncConfigPacket> {

        @Override
        public IMessage handleClientMessage(EntityPlayer player, SyncConfigPacket message, MessageContext ctx) {
            VampirismMod.log.d("SyncConfigPacket", "Received config packet");
            if (!FMLCommonHandler.instance().getSide().isClient()) {
                VampirismMod.log.e(null, "Trying to apply synced configs on server side");
                return null;
            }
            message.applyConfig();
            return null;
        }


        @Override
        protected boolean handleOnMainThread() {
            return true;
        }
    }
}
