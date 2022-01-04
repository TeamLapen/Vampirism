package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class BushBlock extends DeadBushBlock {

    public BushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, @Nonnull BlockGetter blockReader, @Nonnull BlockPos pos) {
        return ForgeRegistries.BLOCKS.getHolder(blockState.getBlock()).map(holder -> holder.is(ModTags.Blocks.CURSED_EARTH)).orElse(false);
    }
}
