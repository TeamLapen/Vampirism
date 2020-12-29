package de.teamlapen.vampirism.blocks;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class GarlicBeaconBlock extends VampirismBlockContainer {

    public final static String regName = "garlic_beacon";
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.75, 0.93);
    private static final VoxelShape shape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 1, 15, 2, 15);
        VoxelShape b = Block.makeCuboidShape(3, 2, 3, 13, 12, 13);
        return VoxelShapes.or(a, b);
    }
    private final Type type;

    public GarlicBeaconBlock(Type type) {
        super(regName + "_" + type.getName(), Properties.create(Material.ROCK).hardnessAndResistance(3f).sound(SoundType.STONE).notSolid());
        this.type = type;
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(new TranslationTextComponent(getTranslationKey() + "." + type.getName()).mergeStyle(TextFormatting.AQUA));
        }

        tooltip.add(new TranslationTextComponent("block.vampirism.garlic_beacon.tooltip1"));
        int c = 1 + 2 * (type == Type.IMPROVED ? VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist.get() : (type == Type.WEAK ? VampirismConfig.BALANCE.hsGarlicDiffusorWeakDist.get() : VampirismConfig.BALANCE.hsGarlicDiffusorNormalDist.get()));
        tooltip.add(new TranslationTextComponent("block.vampirism.garlic_beacon.tooltip2", c, c));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        GarlicBeaconTileEntity tile = new GarlicBeaconTileEntity();
        tile.setType(type);
        return tile;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public String getTranslationKey() {
        return "block.vampirism.garlic_beacon";
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (te instanceof GarlicBeaconTileEntity) {
            ((GarlicBeaconTileEntity) te).onTouched(player);
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty() && ModItems.purified_garlic.equals(heldItem.getItem())) {
            if (!world.isRemote) {
                GarlicBeaconTileEntity t = getTile(world, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        player.sendMessage(new TranslationTextComponent("block.vampirism.garlic_beacon.already_fueled"), Util.DUMMY_UUID);
                    } else {
                        t.onFueled();
                        if (!player.isCreative()) heldItem.shrink(1);
                        player.sendMessage(new TranslationTextComponent("block.vampirism.garlic_beacon.successfully_fueled"), Util.DUMMY_UUID);
                    }

                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        GarlicBeaconTileEntity tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.onTouched(playerIn);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    private GarlicBeaconTileEntity getTile(IBlockReader world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
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


        @Override
        public String getString() {
            return name;
        }

        public String getName() {
            return this.getString();
        }
    }
}
