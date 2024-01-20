package de.teamlapen.vampirism.mixin;

import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockFamilies.class)
public interface BlockFamiliesAccessor {

    @Invoker("familyBuilder(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/data/BlockFamily$Builder")
    static BlockFamily.Builder familyBuilder(Block pBaseBlock) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
