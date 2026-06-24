package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public class ArrowContainerProjectiles {

    public static final int MaxCount = 15;

    public static final Codec<ArrowContainerProjectiles> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("contents").forGetter(ArrowContainerProjectiles::count)
            ).apply(inst, ArrowContainerProjectiles::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArrowContainerProjectiles> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ArrowContainerProjectiles::count,
            ArrowContainerProjectiles::new
    );

    public static ArrowContainerProjectiles of(int count) {
        return new ArrowContainerProjectiles(count);
    }

    public static boolean canContainItem(ItemStack stack) {
        return stack.is(ModItems.CROSSBOW_ARROW_NORMAL);
    }

    private final int count;

    private ArrowContainerProjectiles(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ArrowContainerProjectiles other) {
            return this.count == other.count;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.count);
    }

}
