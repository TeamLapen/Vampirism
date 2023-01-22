package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.core.ModVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RemainsBlock extends Block implements BonemealableBlock {

    private final boolean vulnerable;

    public RemainsBlock(Properties properties, boolean vulnerable) {
        super(properties);
        this.vulnerable = vulnerable;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return pLevel.getBlockState(pPos.below()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    public boolean isVulnerable() {
        return vulnerable;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, BlockPos pos, @NotNull BlockState state) {
        level.setBlockAndUpdate(pos.below(), ModBlocks.CURSED_HANGING_ROOTS.get().defaultBlockState());
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if(level.getPoiManager().find(p -> p.is(ModVillage.MOTHER.getKey()), a -> true, pos, 30, PoiManager.Occupancy.ANY).isEmpty() ) {
            level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
        }
        if(this == ModBlocks.VULNERABLE_REMAINS.get()){
            if(Arrays.stream(Direction.values()).allMatch(d -> level.getBlockState(pos.relative(d)).is(ModTags.Blocks.REMAINS))){
                level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (this == ModBlocks.VULNERABLE_REMAINS.get() && !level.isClientSide()) {
            level.setBlockAndUpdate(pos, ModBlocks.ACTIVE_VULNERABLE_REMAINS.get().defaultBlockState());
        }
    }
}
