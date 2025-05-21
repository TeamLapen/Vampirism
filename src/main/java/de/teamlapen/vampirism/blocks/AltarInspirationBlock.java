package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.blockentity.AltarInspirationBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class AltarInspirationBlock extends VampirismBlockContainer {

    public static final MapCodec<AltarInspirationBlock> CODEC = simpleCodec(AltarInspirationBlock::new);

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 0, 4, 12, 1, 12),
            Block.box(12, 0, 4, 14, 12, 12),
            Block.box(2.1, 0, 2, 13.9, 12, 4),
            Block.box(2.1, 0, 12, 13.9, 12, 14),
            Block.box(2, 0, 4, 4, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AltarInspirationBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarInspirationBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty()) {
            Optional<IFluidHandlerItem> opt = FluidLib.getFluidItemCap(stack);
            if (opt.isPresent()) {
                AltarInspirationBlockEntity tileEntity = (AltarInspirationBlockEntity) worldIn.getBlockEntity(pos);
                if (!player.isShiftKeyDown() && tileEntity != null) {
                    player.awardStat(ModStats.INTERACT_WITH_ALTAR_INSPIRATION.get());
                    FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, worldIn, pos, hit.getDirection());
                }
                return InteractionResult.SUCCESS;
            }
        } else {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof AltarInspirationBlockEntity altar) {
                player.awardStat(ModStats.ALTAR_OF_INSPIRATION_RITUALS_PERFORMED.get());
                altar.startRitual(player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.ALTAR_INSPIRATION.get(), AltarInspirationBlockEntity::serverTick);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
