package de.teamlapen.vampirism.items.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.components.IContainedProjectiles;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ContainedProjectiles implements IContainedProjectiles {

    public static final ContainedProjectiles EMPTY = new ContainedProjectiles(List.of());
    public static final Codec<ContainedProjectiles> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ItemStack.CODEC.listOf().fieldOf("contents").forGetter(ContainedProjectiles::getProjectiles)
            ).apply(inst, ContainedProjectiles::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ContainedProjectiles> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), ContainedProjectiles::getProjectiles,
            ContainedProjectiles::new
    );

    public static ContainedProjectiles of(List<ItemStack> contents) {
        return new ContainedProjectiles(contents);
    }

    private final List<ItemStack> contents;

    private ContainedProjectiles(List<ItemStack> contents) {
        this.contents = contents;
    }

    public List<ItemStack> getProjectiles() {
        return Lists.transform(this.contents, ItemStack::copy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ContainedProjectiles other) {
            return ItemStack.listMatches(this.contents, other.contents);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(this.contents);
    }
}
