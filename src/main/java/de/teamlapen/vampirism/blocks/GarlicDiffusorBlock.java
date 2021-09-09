package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.blockentity.GarlicDiffusorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class GarlicDiffusorBlock extends VampirismBlockContainer {

    public final static String regName = "garlic_diffusor";
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape shape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 2, 15);
        VoxelShape b = Block.box(3, 2, 3, 13, 12, 13);
        return Shapes.or(a, b);
    }

    private final Type type;

    public GarlicDiffusorBlock(Type type) {
        super(regName + "_" + type.getName(), Properties.of(Material.STONE).strength(3f).sound(SoundType.STONE).noOcclusion());
        this.type = type;
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(new TranslatableComponent(getDescriptionId() + "." + type.getName()).withStyle(ChatFormatting.AQUA));
        }

        tooltip.add(new TranslatableComponent("block.vampirism.garlic_beacon.tooltip1").withStyle(ChatFormatting.GRAY));
        int c = VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist == null /* During game start config is not yet set*/ ? 1 : 1 + 2 * (type == Type.IMPROVED ? VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist.get() : (type == Type.WEAK ? VampirismConfig.BALANCE.hsGarlicDiffusorWeakDist.get() : VampirismConfig.BALANCE.hsGarlicDiffusorNormalDist.get()));
        tooltip.add(new TranslatableComponent("block.vampirism.garlic_beacon.tooltip2", c, c).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) {
        GarlicDiffusorBlockEntity tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.onTouched(playerIn);
        }
    }

    @Override
    public String getDescriptionId() {
        return "block.vampirism.garlic_beacon";
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        GarlicDiffusorBlockEntity tile = new GarlicDiffusorBlockEntity(pos, state);
        tile.setType(type);
        tile.initiateBootTimer();
        return tile;
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (te instanceof GarlicDiffusorBlockEntity) {
            ((GarlicDiffusorBlockEntity) te).onTouched(player);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && ModItems.purified_garlic.equals(heldItem.getItem())) {
            if (!world.isClientSide) {
                GarlicDiffusorBlockEntity t = getTile(world, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        player.sendMessage(new TranslatableComponent("block.vampirism.garlic_beacon.already_fueled"), Util.NIL_UUID);
                    } else {
                        t.onFueled();
                        if (!player.isCreative()) heldItem.shrink(1);
                        player.sendMessage(new TranslatableComponent("block.vampirism.garlic_beacon.successfully_fueled"), Util.NIL_UUID);
                    }

                }
            }
            return InteractionResult.SUCCESS;
        } else {
            if (world.isClientSide) {
                GarlicDiffusorBlockEntity t = getTile(world, pos);
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
    private GarlicDiffusorBlockEntity getTile(BlockGetter world, BlockPos pos) {
        BlockEntity t = world.getBlockEntity(pos);
        if (t instanceof GarlicDiffusorBlockEntity) {
            return (GarlicDiffusorBlockEntity) t;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.garlic_beacon, GarlicDiffusorBlockEntity::tick);
    }

    public enum Type implements StringRepresentable {
        NORMAL("normal", 0), IMPROVED("improved", 1), WEAK("weak", 2);


        private final String name;
        private final int id;

        Type(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
