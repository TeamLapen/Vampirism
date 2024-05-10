package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.components.IBloodCharged;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Range;

public record BloodCharged(float charged) implements IBloodCharged {

    public static final BloodCharged EMPTY = new BloodCharged(0);

    public static final Codec<BloodCharged> CODEC = Codec.floatRange(0, 1).xmap(BloodCharged::new, BloodCharged::charged);
    public static final StreamCodec<ByteBuf, BloodCharged> STREAM_CODEC = ByteBufCodecs.FLOAT.map(BloodCharged::new, BloodCharged::charged);

    public BloodCharged(float charged) {
        this.charged = Math.clamp(charged, 0, 1);
    }

    public BloodCharged charged(float charged) {
        return new BloodCharged(charged);
    }
}
