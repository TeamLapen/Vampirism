package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blockentity.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class GarlicDiffuserBlock extends VampirismBlockContainer {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape shape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 2, 15);
        VoxelShape b = Block.box(3, 2, 3, 13, 12, 13);
        return Shapes.or(a, b);
    }

    private final Type type;

    public GarlicDiffuserBlock(Type type) {
        super(Properties.of(Material.STONE).strength(3f).sound(SoundType.STONE).noOcclusion());
        this.type = type;
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(Component.translatable(getDescriptionId() + "." + type.getName()).withStyle(ChatFormatting.AQUA));
        }

        tooltip.add(Component.translatable("block.vampirism.garlic_diffuser.tooltip1").withStyle(ChatFormatting.GRAY));
        int c = VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist == null /* During game start config is not yet set*/ ? 1 : 1 + 2 * (type == Type.IMPROVED ? VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist.get() : (type == Type.WEAK ? VampirismConfig.BALANCE.hsGarlicDiffuserWeakDist.get() : VampirismConfig.BALANCE.hsGarlicDiffuserNormalDist.get()));
        tooltip.add(Component.translatable("block.vampirism.garlic_diffuser.tooltip2", c, c).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void attack(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Player playerIn) {
        GarlicDiffuserBlockEntity tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.onTouched(playerIn);
        }
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.vampirism.garlic_diffuser";
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape;
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Nonnull
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        GarlicDiffuserBlockEntity tile = new GarlicDiffuserBlockEntity(pos, state);
        tile.setType(type);
        tile.initiateBootTimer();
        return tile;
    }

    @Override
    public void playerDestroy(@Nonnull Level worldIn, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable BlockEntity te, @Nonnull ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (te instanceof GarlicDiffuserBlockEntity) {
            ((GarlicDiffuserBlockEntity) te).onTouched(player);
        }
    }

    @Nonnull
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && ModItems.PURIFIED_GARLIC.get() == heldItem.getItem()) {
            if (!world.isClientSide) {
                GarlicDiffuserBlockEntity t = getTile(world, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        player.sendSystemMessage(Component.translatable("block.vampirism.garlic_diffuser.already_fueled"));
                    } else {
                        t.onFueled();
                        if (!player.isCreative()) heldItem.shrink(1);
                        player.sendSystemMessage(Component.translatable("block.vampirism.garlic_diffuser.successfully_fueled"));
                    }

                }
            }
            return InteractionResult.SUCCESS;
        } else {
            if (world.isClientSide) {
                GarlicDiffuserBlockEntity t = getTile(world, pos);
                if (t != null) {
                    VampirismMod.proxy.displayGarlicBeaconScreen(t, getName());
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    private GarlicDiffuserBlockEntity getTile(BlockGetter world, BlockPos pos) {
        BlockEntity t = world.getBlockEntity(pos);
        if (t instanceof GarlicDiffuserBlockEntity) {
            return (GarlicDiffuserBlockEntity) t;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.GARLIC_DIFFUSER.get(), GarlicDiffuserBlockEntity::tick);
    }

    public enum Type implements StringRepresentable {
        NORMAL("normal"), IMPROVED("improved"), WEAK("weak");


        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
