package de.teamlapen.vampirism.blocks.mother;

import de.teamlapen.vampirism.blockentity.MotherBlockEntity;
import de.teamlapen.vampirism.blocks.VampirismBlock;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static de.teamlapen.vampirism.blocks.HorizontalContainerBlock.createTickerHelper;

public class MotherBlock extends VampirismBlock implements EntityBlock, IRemainsBlock {


    public MotherBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(5, 3600000.0F).sound(SoundType.CHAIN));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }


    private Optional<MotherBlockEntity> getBlockEntity(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MotherBlockEntity mother) {
            return Optional.of(mother);
        }
        return Optional.empty();
    }

    @Override
    public boolean isVulnerable(BlockState state) {
        return false;
    }

    @Override
    public boolean isMother(BlockState state) {
        return true;
    }

    @Override
    public boolean isVulnerability(BlockState state) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MotherBlockEntity(pos, state);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (getBlockEntity(level, pos).map(MotherBlockEntity::isCanBeBroken).orElse(Boolean.TRUE)) {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
        return false;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModTiles.MOTHER.get(), MotherBlockEntity::serverTick);
    }

}
