package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

    @Mutable
    @Final
    @Accessor("STREAM_CODEC")
    static void setStreamCodec(StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance> codec) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
