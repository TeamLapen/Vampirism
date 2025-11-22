package de.teamlapen.vampirism.common.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record PureLevel(int level) {

    public static final PureLevel EMPTY = new PureLevel(-1);
    public static final PureLevel LOW = new PureLevel(0);

    public static final Codec<PureLevel> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("level").forGetter(PureLevel::level)
    ).apply(inst, PureLevel::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PureLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PureLevel::level,
            PureLevel::new
    );

    public static ItemStack pureBlood(ItemStack stack, int level) {
        stack.set(ModDataComponents.PURE_LEVEL, new PureLevel(level));
        return stack;
    }
    public static ItemStack pureBlood(ItemLike item, int level) {
        return pureBlood(item.asItem().getDefaultInstance(), level);
    }
}
