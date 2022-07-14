package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.blockentity.AltarInspirationBlockEntity;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class AltarInspirationBlock extends VampirismBlockContainer {
    protected static final VoxelShape altarShape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 16, 2, 16);
        VoxelShape b1 = Block.box(0, 0, 0, 1, 6, 1);
        VoxelShape b2 = Block.box(15, 0, 0, 16, 6, 1);
        VoxelShape b3 = Block.box(0, 0, 15, 1, 6, 16);
        VoxelShape b4 = Block.box(15, 0, 15, 16, 6, 16);
        VoxelShape c1 = Block.box(6, 2, 6, 10, 3, 10);
        VoxelShape c2 = Block.box(5, 3, 5, 11, 4, 11);
        VoxelShape c3 = Block.box(4, 4, 4, 12, 5, 12);
        VoxelShape c4 = Block.box(3, 5, 3, 13, 6, 13);
        VoxelShape c5 = Block.box(2, 6, 2, 14, 7, 14);
        VoxelShape c6 = Block.box(1, 7, 1, 15, 9, 15);
        VoxelShape c7 = Block.box(2, 9, 2, 14, 10, 14);
        VoxelShape c8 = Block.box(3, 10, 3, 13, 11, 13);
        VoxelShape c9 = Block.box(4, 11, 4, 12, 12, 12);
        VoxelShape c10 = Block.box(5, 12, 5, 11, 13, 11);
        VoxelShape c11 = Block.box(6, 13, 6, 10, 14, 10);

        return Shapes.or(a, b1, b2, b3, b4, c1, c2, c3, c4, c5, c5, c6, c7, c8, c9, c10, c11);
    }

    public AltarInspirationBlock() {
        super(Properties.of(Material.METAL).strength(2f).noOcclusion());
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new AltarInspirationBlockEntity(pos, state);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return altarShape;
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            LazyOptional<IFluidHandlerItem> opt = FluidLib.getFluidItemCap(stack);
            if (opt.isPresent()) {
                AltarInspirationBlockEntity tileEntity = (AltarInspirationBlockEntity) worldIn.getBlockEntity(pos);
                if (!player.isShiftKeyDown() && tileEntity != null) {
                    FluidUtil.interactWithFluidHandler(player, hand, worldIn, pos, hit.getDirection());
                }
                return InteractionResult.SUCCESS;
            }
        } else {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof AltarInspirationBlockEntity altar) {
                altar.startRitual(player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModTiles.ALTAR_INSPIRATION.get(), AltarInspirationBlockEntity::serverTick);
    }
}
