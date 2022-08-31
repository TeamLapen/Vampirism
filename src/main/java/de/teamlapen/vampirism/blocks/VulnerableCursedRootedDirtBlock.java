package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.VulnerabelCursedRootedDirtBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VulnerableCursedRootedDirtBlock extends CursedRootedDirtBlock implements EntityBlock {

    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("is_active");
    public static final BooleanProperty IS_INVULNERABLE = BooleanProperty.create("is_invulnerable");


    public VulnerableCursedRootedDirtBlock() {
        this.registerDefaultState(defaultBlockState().setValue(IS_ACTIVE, true).setValue(IS_INVULNERABLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_ACTIVE).add(IS_INVULNERABLE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new VulnerabelCursedRootedDirtBlockEntity(pos, state);
    }

    private Optional<VulnerabelCursedRootedDirtBlockEntity> getBlockEntity(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VulnerabelCursedRootedDirtBlockEntity) {
            return Optional.of((VulnerabelCursedRootedDirtBlockEntity) blockEntity);
        }
        return Optional.empty();
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            getBlockEntity(level, pos).ifPresent(entity -> entity.attacked(state, serverPlayer));
        }
    }
}
