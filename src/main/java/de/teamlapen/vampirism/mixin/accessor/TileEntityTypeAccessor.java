package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockEntityType.class)
public interface TileEntityTypeAccessor {

    @Mutable
    @Final
    @Accessor("validBlocks")
    void setValidBlocks(Set<Block> blocks);

    @Accessor("validBlocks")
    Set<Block> getValidBlocks();
}
