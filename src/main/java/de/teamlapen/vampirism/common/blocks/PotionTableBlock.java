package de.teamlapen.vampirism.common.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.blockentity.PotionTableBlockEntity;
import de.teamlapen.vampirism.common.blocks.base.BaseContainerBlock;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class PotionTableBlock extends BaseContainerBlock {

    public static final MapCodec<PotionTableBlock> CODEC = simpleCodec(PotionTableBlock::new);

    protected static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(2, 2, 2, 14, 9, 14),
            Block.box(0, 9, 0, 16, 10, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public PotionTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionTableBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof PotionTableBlockEntity potionTableBlockEntity && placer instanceof Player player) {
            potionTableBlockEntity.setOwnerID(player);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof PotionTableBlockEntity potionTable) {
                if (potionTable.canOpen(player)) {
                    player.openMenu(potionTable, buffer -> buffer.writeBoolean(potionTable.isExtended()));
                    player.awardStat(ModStats.INTERACT_WITH_POTION_TABLE.get());
                }
            }
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void clearContainer(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PotionTableBlockEntity potionTableBlockEntity) {
            for (int i = 0; i < 8; ++i) {
                this.dropItem(level, pos, potionTableBlockEntity.removeItemNoUpdate(i));
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.POTION_TABLE.get(), PotionTableBlockEntity::tick);
    }
}
