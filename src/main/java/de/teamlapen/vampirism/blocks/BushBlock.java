package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BushBlock extends DeadBushBlock {

    public BushBlock(BlockBehaviour.@NotNull Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState blockState, @NotNull BlockGetter blockReader, @NotNull BlockPos pos) {
        return BuiltInRegistries.BLOCK.wrapAsHolder(blockState.getBlock()).is(ModBlockTags.CURSED_EARTH);
    }
}
