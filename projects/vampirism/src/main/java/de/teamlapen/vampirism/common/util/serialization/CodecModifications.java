package de.teamlapen.vampirism.common.util.serialization;

import de.teamlapen.vampirism.misc.mixin.accessor.MobEffectInstanceAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CodecModifications {

    public static void changeMobEffectCodec() {
        StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance> codec = MobEffectInstance.STREAM_CODEC;
        var newCodec = new StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance>() {

            @Override
            public @NotNull MobEffectInstance decode(@NotNull RegistryFriendlyByteBuf byteBuf) {
                MobEffectInstance decode = codec.decode(byteBuf);
                int count = byteBuf.readVarInt();
                Set<Identifier> properties = new HashSet<>();
                for (int i1 = 0; i1 < count; i1++) {
                    properties.add(byteBuf.readIdentifier());
                }
                decode.factions$setProperties(properties);
                return decode;
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf byteBuf, @NotNull MobEffectInstance instance) {
                codec.encode(byteBuf, instance);
                Set<Identifier> source = instance.factions$getProperties();
                byteBuf.writeVarInt(source.size());
                for (Identifier Identifier : source) {
                    byteBuf.writeIdentifier(Identifier);
                }
            }
        };
        MobEffectInstanceAccessor.setStreamCodec(newCodec);
    }
}
