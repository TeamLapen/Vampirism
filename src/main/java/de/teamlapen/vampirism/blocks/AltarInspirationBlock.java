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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Altar of inspiration used for vampire levels 1-4
 */
public class AltarInspirationBlock extends VampirismBlockContainer {
    protected static final VoxelShape altarShape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.25, 0.75, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.1875, 0.875, 0.75, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.13125, 0, 0.125, 0.86875, 0.75, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.13125, 0, 0.75, 0.86875, 0.75, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.1875, 0.25, 0.75, 0.8125), BooleanOp.OR);

        return shape;
    }

    public AltarInspirationBlock() {
        super(Properties.of(Material.METAL).strength(2f, 3f).noOcclusion());
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AltarInspirationBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return altarShape;
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModTiles.ALTAR_INSPIRATION.get(), AltarInspirationBlockEntity::serverTick);
    }
}
