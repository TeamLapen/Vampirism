package de.teamlapen.vampirism.blocks.connected;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ConnectedBlock<T extends IConnectedBlock> extends Block implements IConnectedBlock {


    public ConnectedBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public abstract Connector<T> getConnector();


    public static class Connector<T> implements IConnectedBlock.IConnector {

        @NotNull
        private final Class<T> connectedBlock;

        public Connector(@NotNull Class<T> connectedBlock) {

            this.connectedBlock = connectedBlock;
        }

        @Override
        public Stream<BlockState> collectConnectedBlocks(Level level, BlockPos pos) {
            return this.innerCollect(level, pos, new HashSet<>(), state -> true);
        }

        @Override
        public Stream<BlockState> collectConnectedBlocks(Level level, BlockPos pos, Predicate<BlockState> predicate) {
            return this.innerCollect(level, pos, new HashSet<>(), predicate);
        }

        @Override
        public Optional<Pair<BlockPos, BlockState>> find(Level level, BlockPos pos, Predicate<BlockState> predicate) {
            return this.innerFind(level, pos, new HashSet<>(), predicate);
        }

        @Override
        public void foreach(Level level, BlockPos pos, TriConsumer<Level, BlockPos, BlockState> consumer) {
            innerForEach(level, pos, new HashSet<>(), consumer);
        }

        public void foreachFacing(Level level, BlockPos pos, TriConsumer<Level, BlockPos, BlockState> consumer) {
            HashSet<BlockPos> objects = new HashSet<>();
            Direction.stream().map(pos::relative).forEach(newPos -> this.innerForEach(level, newPos, objects, consumer));
        }

        private Stream<BlockState> innerCollect(Level level, BlockPos pos, Set<BlockPos> collected, Predicate<BlockState> predicate) {
            if (collected.contains(pos)) return Stream.empty();
            collected.add(pos);
            BlockState blockState = level.getBlockState(pos);
            if (!this.connectedBlock.isAssignableFrom(blockState.getBlock().getClass())) return Stream.empty();
            Stream<BlockState> stream = Direction.stream().map(pos::relative).flatMap(newPos -> this.innerCollect(level, newPos, collected, predicate));
            if (predicate.test(blockState)) {
                return Stream.concat(Stream.of(blockState), stream);
            } else {
                return stream;
            }
        }

        private void innerForEach(Level level, BlockPos pos, Set<BlockPos> collected, TriConsumer<Level, BlockPos, BlockState> consumer) {
            if (collected.contains(pos)) return;
            collected.add(pos);
            BlockState blockState = level.getBlockState(pos);
            if (!this.connectedBlock.isAssignableFrom(blockState.getBlock().getClass())) return;
            consumer.accept(level, pos, blockState);
            Direction.stream().map(pos::relative).forEach(newPos -> this.innerForEach(level, newPos, collected, consumer));
        }

        private Optional<Pair<BlockPos, BlockState>> innerFind(Level level, BlockPos pos, Set<BlockPos> visited, Predicate<BlockState> predicate) {
            if (visited.contains(pos)) return Optional.empty();
            visited.add(pos);
            BlockState blockState = level.getBlockState(pos);
            if (!this.connectedBlock.isAssignableFrom(blockState.getBlock().getClass())) return Optional.empty();
            if (predicate.test(blockState)) {
                return Optional.of(Pair.of(pos, blockState));
            } else {
                return Direction.stream().map(pos::relative).map(newPos -> this.innerFind(level, newPos, visited, predicate)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
            }
        }
    }
}
