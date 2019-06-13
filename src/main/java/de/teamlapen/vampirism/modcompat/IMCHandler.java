package de.teamlapen.vampirism.modcompat;

import com.google.common.collect.ImmutableList;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class IMCHandler {

    public static void handleInterModMessage(ImmutableList<FMLInterModComms.IMCMessage> messages) {
        IVampirismEntityRegistry entityRegistry = VampirismAPI.entityRegistry();
        ISundamageRegistry sundamageRegistry = VampirismAPI.sundamageRegistry();
        for (FMLInterModComms.IMCMessage m : messages) {
            try {
                if ("blood-value".equals(m.key)) {
                    if (m.isNBTMessage()) {
                        NBTTagCompound nbt = m.getNBTValue();
                        if (nbt.contains("id") && nbt.contains("value")) {
                            ResourceLocation id = new ResourceLocation(nbt.getString("id"));
                            int value = nbt.getInt("value");
                            VampirismMod.log.i("InterModComm", "Received blood value of %s for %s from %s", value, id, m.getSender());
                            entityRegistry.addBloodValue(id, value);
                        } else {
                            VampirismMod.log.w("InterModComm", "Received invalid blood value nbt from %s", m.getSender());
                        }
                    } else {
                        VampirismMod.log.w("InterModComm", "Received invalid blood value message type from %s", m.getSender());
                    }
                } else if ("nosundamage-biome".equals(m.key)) {
                    if (m.isResourceLocationMessage()) {
                        VampirismMod.log.i("InterModComm", "Received no sun damage biome %s from %s", m.getResourceLocationValue(), m.getSender());
                        sundamageRegistry.addNoSundamageBiome(m.getResourceLocationValue());
                    } else {
                        VampirismMod.log.w("InterModComm", "Received invalid no sun damage biome message from %s", m.getSender());
                    }
                } else {
                    VampirismMod.log.w("InterModComm", "Received unknown message (%s) from %s", m.key, m.getSender());
                }
            } catch (Exception e) {
                VampirismMod.log.e("InterModComm", e, "Failed to parse message from %s", m.getSender());
            }
        }
    }
}
