package de.teamlapen.vampirism.common.util.serialization;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.AnimationState;

public class ModStreamCodecs {

    public static final StreamCodec<ByteBuf, AnimationState> ANIMATION_STATE = new StreamCodec<ByteBuf, AnimationState>() {
        @Override
        public AnimationState decode(ByteBuf b) {
            var state = new AnimationState();
            state.start(b.readInt());
            return state;
        }

        @Override
        public void encode(ByteBuf o, AnimationState animationState) {
            o.writeInt(animationState.vampirism$startTick());
        }
    };
}
