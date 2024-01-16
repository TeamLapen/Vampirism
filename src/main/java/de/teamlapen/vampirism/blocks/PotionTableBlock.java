package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.PotionTableBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionTableBlock extends VampirismBlockContainer {
    public static final MapCodec<PotionTableBlock> CODEC = simpleCodec(PotionTableBlock::new);
    protected static final VoxelShape shape = makeShape();

    private static @NotNull VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.box(1, 1, 1, 15, 2, 15);
        VoxelShape c = Block.box(2, 2, 2, 14, 9, 14);
        VoxelShape d = Block.box(0, 9, 0, 16, 11, 16);
        return Shapes.or(a, b, c, d);
    }

    public PotionTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PotionTableBlockEntity(pos, state);
    }


    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull BlockState blockState, LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(world, blockPos, blockState, entity, stack);
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (entity instanceof Player && tile instanceof PotionTableBlockEntity) {
            ((PotionTableBlockEntity) tile).setOwnerID((Player) entity);
        }
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!worldIn.isClientSide && player instanceof ServerPlayer) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof PotionTableBlockEntity) {
                if (((PotionTableBlockEntity) tile).canOpen(player)) {
                    player.openMenu((PotionTableBlockEntity) tile, buffer -> buffer.writeBoolean(((PotionTableBlockEntity) tile).isExtended()));
                    player.awardStat(ModStats.interact_with_potion_table.get());
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PotionTableBlockEntity) {
            for (int i = 0; i < 8; ++i) {
                this.dropItem(worldIn, pos, ((PotionTableBlockEntity) te).removeItemNoUpdate(i));
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.POTION_TABLE.get(), PotionTableBlockEntity::tick);
    }
}
