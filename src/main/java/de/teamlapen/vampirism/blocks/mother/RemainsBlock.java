package de.teamlapen.vampirism.blocks.mother;

import de.teamlapen.vampirism.blocks.connected.ConnectedBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RemainsBlock extends ConnectedBlock implements BonemealableBlock, IRemainsBlock {

    private final boolean vulnerable;
    private final boolean isVulnerability;
    private final RemainsConnector connector = new RemainsConnector();

    public RemainsBlock(Properties properties, boolean vulnerable, boolean isVulnerability) {
        super(properties);
        this.vulnerable = vulnerable;
        this.isVulnerability = isVulnerability;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, @NotNull BlockState pState, boolean pIsClient) {
        return pLevel.getBlockState(pPos.below()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isVulnerable(BlockState state) {
        return this.vulnerable;
    }

    @Override
    public boolean isMother(BlockState state) {
        return false;
    }

    @Override
    public boolean isVulnerability(BlockState state) {
        return this.isVulnerability;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, BlockPos pos, @NotNull BlockState state) {
        level.setBlockAndUpdate(pos.below(), ModBlocks.CURSED_HANGING_ROOTS.get().defaultBlockState());
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (this.getConnector().getMotherEntity(level, pos).isEmpty()) {
            level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
        }
        if (this == ModBlocks.VULNERABLE_REMAINS.get()) {
            if (Arrays.stream(Direction.values()).allMatch(d -> level.getBlockState(pos.relative(d)).is(ModTags.Blocks.REMAINS))) {
                level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
            }
        }
    }

    @Override
    public RemainsConnector getConnector() {
        return this.connector;
    }

    @Override
    public void unFreeze(Level level, BlockPos pos, BlockState state) {
        if (this == ModBlocks.VULNERABLE_REMAINS.get()) {
            level.setBlock(pos, ModBlocks.ACTIVE_VULNERABLE_REMAINS.get().defaultBlockState(), 3);
        }
    }
}
