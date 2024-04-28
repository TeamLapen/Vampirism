package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.mixin.accessor.MobEffectInstanceAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class CodecModifications {

    public static void changeMobEffectCodec() {
        StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance> codec = MobEffectInstance.STREAM_CODEC;
        var newCodec = new StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance>() {

            @Override
            public MobEffectInstance decode(RegistryFriendlyByteBuf p_320376_) {
                MobEffectInstance decode = codec.decode(p_320376_);
                if (p_320376_.readBoolean()) {
                    ((EffectInstanceWithSource) decode).setSource(p_320376_.readResourceLocation());
                }
                return decode;
            }

            @Override
            public void encode(RegistryFriendlyByteBuf p_320158_, MobEffectInstance p_320396_) {
                codec.encode(p_320158_, p_320396_);
                ResourceLocation source = ((EffectInstanceWithSource) p_320396_).getSource();
                if (source != null) {
                    p_320158_.writeBoolean(true);
                    p_320158_.writeResourceLocation(source);
                } else {
                    p_320158_.writeBoolean(false);
                }
            }
        };
        MobEffectInstanceAccessor.setStreamCodec(newCodec);
    }
}
