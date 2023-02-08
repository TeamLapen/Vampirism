package de.teamlapen.vampirism.blocks.mother;

import de.teamlapen.vampirism.blockentity.MotherBlockEntity;
import de.teamlapen.vampirism.blocks.connected.ConnectedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.stream.Stream;

public class RemainsConnector extends ConnectedBlock.Connector<IRemainsBlock> {

    public RemainsConnector() {
        super(IRemainsBlock.class);
    }

    public Optional<Pair<BlockPos, BlockState>> getMother(Level level, BlockPos pos) {
        return this.find(level, pos, state -> ((IRemainsBlock) state.getBlock()).isMother(state));
    }

    public Optional<MotherBlockEntity> getMotherEntity(Level level, BlockPos pos) {
        return getMother(level, pos).map(pair -> {
            BlockEntity blockEntity = level.getBlockEntity(pair.getLeft());
            if (blockEntity instanceof MotherBlockEntity mother) {
                return mother;
            }
            return null;
        });
    }

    public Stream<BlockState> getVulnerabilities(Level level, BlockPos pos) {
        return this.collectConnectedBlocks(level, pos, state -> ((IRemainsBlock) state.getBlock()).isVulnerability(state));
    }
}
