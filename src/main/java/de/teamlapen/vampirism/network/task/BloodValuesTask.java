package de.teamlapen.vampirism.network.task;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.network.ClientboundBloodValuePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.Consumer;

public class BloodValuesTask implements ICustomConfigurationTask {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "blood_value");
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ID);
    @Override
    public void run(Consumer<CustomPacketPayload> sender) {

        Map<ResourceLocation, Float>[] bloodValues = (Map<ResourceLocation, Float>[]) Array.newInstance(Map.class, 1);
        bloodValues[0] = BloodConversionRegistry.getEntityConversions();
        Map<EntityType<? extends PathfinderMob>, ResourceLocation> convertibleOverlay = VampirismAPI.entityRegistry().getConvertibleOverlay();

        sender.accept(new ClientboundBloodValuePacket(bloodValues, (Map<EntityType<?>, ResourceLocation>) (Object) convertibleOverlay));

    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }
}
