package de.teamlapen.vampirism.common.world.items.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.items.components.IContainedProjectiles;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ContainedProjectiles implements IContainedProjectiles {

    public static final ContainedProjectiles EMPTY = new ContainedProjectiles(List.of(), 1);
    public static final Codec<ContainedProjectiles> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ItemStack.CODEC.listOf().fieldOf("contents").forGetter(ContainedProjectiles::getProjectiles),
                    Codec.INT.fieldOf("maxCount").forGetter(ContainedProjectiles::getMaxCount)
            ).apply(inst, ContainedProjectiles::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ContainedProjectiles> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), ContainedProjectiles::getProjectiles,
            ByteBufCodecs.INT, ContainedProjectiles::getMaxCount,
            ContainedProjectiles::new
    );

    public static ContainedProjectiles of(List<ItemStack> contents, int maxCount) {
        return new ContainedProjectiles(contents, maxCount);
    }

    private final List<ItemStack> contents;
    private final int maxCount;

    private ContainedProjectiles(List<ItemStack> contents, int maxCount) {
        this.contents = contents;
        this.maxCount = maxCount;
    }

    public List<ItemStack> getProjectiles() {
        return Lists.transform(this.contents, ItemStack::copy);
    }

    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ContainedProjectiles other) {
            return ItemStack.listMatches(this.contents, other.contents) && this.maxCount == other.maxCount;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(this.contents) + this.maxCount;
    }
}
