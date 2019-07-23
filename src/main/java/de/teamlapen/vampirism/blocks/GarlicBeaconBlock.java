package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.Balance;
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
    private final static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.75, 0.93);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final Type type;
    private static final VoxelShape shape = makeShape();

    public GarlicBeaconBlock(Type type) {
        super(regName + "_" + type.getName(), Properties.create(Material.ROCK).hardnessAndResistance(3f).sound(SoundType.STONE));
        this.type = type;
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public String getTranslationKey() {
        return "block.vampirism.garlic_beacon";
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(UtilLib.translated(getTranslationKey() + "." + type.getName()).applyTextStyle(TextFormatting.AQUA));
        }

        tooltip.add(UtilLib.translated("block.vampirism.garlic_beacon.tooltip1"));
        int c = 1 + 2 * (type == Type.IMPROVED ? Balance.hps.GARLIC_DIFFUSOR_ENHANCED_DISTANCE : (type == Type.WEAK ? Balance.hps.GARLIC_DIFFUSOR_WEAK_DISTANCE : Balance.hps.GARLIC_DIFFUSOR_NORMAL_DISTANCE));
        tooltip.add(UtilLib.translated("block.vampirism.garlic_beacon.tooltip2", c, c));
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (te != null && te instanceof GarlicBeaconTileEntity) {
            ((GarlicBeaconTileEntity) te).onTouched(player);
        }
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty() && ModItems.purified_garlic.equals(heldItem.getItem())) {
            if (!world.isRemote) {
                GarlicBeaconTileEntity t = getTile(world, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        player.sendMessage(new TranslationTextComponent("tile.vampirism.garlic_beacon.already_fueled"));
                    } else {
                        t.onFueled();
                        if (!player.isCreative()) heldItem.shrink(1);
                        player.sendMessage(new TranslationTextComponent("tile.vampirism.garlic_beacon.successfully_fueled"));
                    }

                }
            }
            return true;
        }
        return false;
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

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
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
        public String getName() {
            return name;
        }
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 1, 15, 2, 15);
        VoxelShape b = Block.makeCuboidShape(3, 2, 3, 13, 12, 13);
        return VoxelShapes.or(a, b);
    }
}
