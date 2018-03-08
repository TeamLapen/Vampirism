package de.teamlapen.vampirism.network;

import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;


/**
 * Currently unused
 */
@Deprecated
public class BloodValuePacket implements IMessage {

    private NBTTagCompound nbt;

    /**
     * DO NOT USE
     */
    public BloodValuePacket() {

    }

    public BloodValuePacket(Map<ResourceLocation, Integer> values) {
        nbt = new NBTTagCompound();
        for (Map.Entry<ResourceLocation, Integer> entry : values.entrySet()) {
            nbt.setInteger(entry.getKey().toString(), entry.getValue());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    private Map<ResourceLocation, Integer> getValues() {
        Map<ResourceLocation, Integer> values = Maps.newHashMap();
        try {
            for (String s : nbt.getKeySet()) {
                values.put(new ResourceLocation(s), nbt.getInteger(s));
            }
        } catch (Exception e) {
            VampirismMod.log.e("SyncBloodValuePacket", "Failed to parse nbt packet");
        }
        return values;
    }

    public static class Handler extends AbstractClientMessageHandler<BloodValuePacket> {

        @Override
        public IMessage handleClientMessage(EntityPlayer player, BloodValuePacket message, MessageContext ctx) {
            VampirismMod.log.d("SyncBloodValuePacket", "Received blood value packet");
            if (!FMLCommonHandler.instance().getSide().isClient()) {
                VampirismMod.log.e(null, "Trying to apply synced blood values on server side");
                return null;
            }
            if (UtilLib.isSameInstanceAsServer()) {
                VampirismMod.log.d("SyncBloodValuePakcet", "Not loading since same as server");
                return null;
            }
            Map<ResourceLocation, Integer> values = message.getValues();
            VampirismEntityRegistry.getBiteableEntryManager().addDynamic(values);
            return null;
        }

        @Override
        protected boolean handleOnMainThread() {
            return true;
        }
    }


}
