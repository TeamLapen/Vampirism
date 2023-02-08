package de.teamlapen.vampirism.blocks.connected;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IConnectedBlock {

    IConnector getConnector();

    interface IConnector {
        Stream<BlockState> collectConnectedBlocks(Level level, BlockPos pos);

        Stream<BlockState> collectConnectedBlocks(Level level, BlockPos pos, Predicate<BlockState> predicate);

        Optional<Pair<BlockPos, BlockState>> find(Level level, BlockPos pos, Predicate<BlockState> predicate);

        void foreach(Level level, BlockPos pos, TriConsumer<Level, BlockPos, BlockState> consumer);
    }
}
