package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;

public class CursedRootedDirtBlock extends Block implements BonemealableBlock {

    public CursedRootedDirtBlock() {
        super(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.TERRACOTTA_BROWN).strength(0.5F).sound(SoundType.ROOTED_DIRT));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return pLevel.getBlockState(pPos.below()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, BlockPos pos, @NotNull BlockState state) {
        level.setBlockAndUpdate(pos.below(), ModBlocks.CURSED_HANGING_ROOTS.get().defaultBlockState());
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if(level.getPoiManager().find(p -> p.is(ModVillage.MOTHER.getKey()), a -> true, pos, 30, PoiManager.Occupancy.ANY).isEmpty()) {
            level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
        }
    }
}
