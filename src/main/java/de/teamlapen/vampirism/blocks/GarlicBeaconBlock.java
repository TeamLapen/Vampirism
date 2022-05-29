package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.GarlicBeaconTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class GarlicBeaconBlock extends VampirismBlockContainer {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    private final static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.75, 0.93);
    private static final VoxelShape shape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 2, 15);
        VoxelShape b = Block.box(3, 2, 3, 13, 12, 13);
        return VoxelShapes.or(a, b);
    }

    private final Type type;

    public GarlicBeaconBlock(Type type) {
        super(Properties.of(Material.STONE).strength(3f).sound(SoundType.STONE).noOcclusion());
        this.type = type;
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(new TranslationTextComponent(getDescriptionId() + "." + type.getName()).withStyle(TextFormatting.AQUA));
        }

        tooltip.add(new TranslationTextComponent("block.vampirism.garlic_beacon.tooltip1").withStyle(TextFormatting.GRAY));
        int c = VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist == null /* During game start config is not yet set*/ ? 1 : 1 + 2 * (type == Type.IMPROVED ? VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist.get() : (type == Type.WEAK ? VampirismConfig.BALANCE.hsGarlicDiffusorWeakDist.get() : VampirismConfig.BALANCE.hsGarlicDiffusorNormalDist.get()));
        tooltip.add(new TranslationTextComponent("block.vampirism.garlic_beacon.tooltip2", c, c).withStyle(TextFormatting.GRAY));
    }

    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        GarlicBeaconTileEntity tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.onTouched(playerIn);
        }
    }

    @Override
    public String getDescriptionId() {
        return "block.vampirism.garlic_beacon";
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        GarlicBeaconTileEntity tile = new GarlicBeaconTileEntity();
        tile.setType(type);
        int bootTime = VampirismConfig.BALANCE.garlicDiffusorStartupTime.get() * 20;
        if (worldIn instanceof ServerWorld) {
            if (((ServerWorld) worldIn).players().size() <= 1) {
                bootTime >>= 2; // /4
            }
        }
        tile.setNewBootDelay(bootTime);
        return tile;
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (te instanceof GarlicBeaconTileEntity) {
            ((GarlicBeaconTileEntity) te).onTouched(player);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && ModItems.PURIFIED_GARLIC.get().equals(heldItem.getItem())) {
            if (!world.isClientSide) {
                GarlicBeaconTileEntity t = getTile(world, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        player.sendMessage(new TranslationTextComponent("block.vampirism.garlic_beacon.already_fueled"), Util.NIL_UUID);
                    } else {
                        t.onFueled();
                        if (!player.isCreative()) heldItem.shrink(1);
                        player.sendMessage(new TranslationTextComponent("block.vampirism.garlic_beacon.successfully_fueled"), Util.NIL_UUID);
                    }

                }
            }
            return ActionResultType.SUCCESS;
        } else {
            if (world.isClientSide) {
                GarlicBeaconTileEntity t = getTile(world, pos);
                if (t != null) {
                    VampirismMod.proxy.displayGarlicBeaconScreen(t, getName());
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    private GarlicBeaconTileEntity getTile(IBlockReader world, BlockPos pos) {
        TileEntity t = world.getBlockEntity(pos);
        if (t instanceof GarlicBeaconTileEntity) {
            return (GarlicBeaconTileEntity) t;
        }
        return null;
    }

    public enum Type implements IStringSerializable {
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
