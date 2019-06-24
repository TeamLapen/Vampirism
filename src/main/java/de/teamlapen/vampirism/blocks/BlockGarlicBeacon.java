package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.TileGarlicBeacon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class BlockGarlicBeacon extends VampirismBlockContainer {

    public final static String regName = "garlic_beacon";
    private final static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.75, 0.93);
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
    private final static AxisAlignedBB COLLISION_BOX_2 = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.19, 0.93);
    private final static AxisAlignedBB COLLISION_BOX_1 = new AxisAlignedBB(0.19, 0, 0.19, 0.81, 0.75, 0.81); //TODO 1.13 shape
    private final Type type;

    public BlockGarlicBeacon(Type type) {
        super(regName + "_" + type.getName(), Properties.create(Material.ROCK).hardnessAndResistance(3f).sound(SoundType.STONE));
        this.type = type;
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(UtilLib.translated(getTranslationKey() + "." + type.getName()).applyTextStyle(TextFormatting.AQUA));
        }

        tooltip.add(UtilLib.translated(getTranslationKey() + ".tooltip1"));
        int c = 1 + 2 * (type == Type.IMPROVED ? Balance.hps.GARLIC_DIFFUSOR_ENHANCED_DISTANCE : (type == Type.WEAK ? Balance.hps.GARLIC_DIFFUSOR_WEAK_DISTANCE : Balance.hps.GARLIC_DIFFUSOR_NORMAL_DISTANCE));
        tooltip.add(UtilLib.translated(getTranslationKey() + ".tooltip2", c, c));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        TileGarlicBeacon tile = new TileGarlicBeacon();
        tile.setType(type);
        return tile;
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty() && ModItems.purified_garlic.equals(heldItem.getItem())) {
            if (!world.isRemote) {
                TileGarlicBeacon t = getTile(world, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        player.sendMessage(new TextComponentTranslation("tile.vampirism.garlic_beacon.already_fueled"));
                    } else {
                        t.onFueled();
                        if (!player.isCreative()) heldItem.shrink(1);
                        player.sendMessage(new TextComponentTranslation("tile.vampirism.garlic_beacon.successfully_fueled"));
                    }

                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn) {
        TileGarlicBeacon tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.onTouched(playerIn);
        }
    }


    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }


    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (te != null && te instanceof TileGarlicBeacon) {
            ((TileGarlicBeacon) te).onTouched(player);
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState rotate(IBlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }



    @Nullable
    private TileGarlicBeacon getTile(IBlockReader world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileGarlicBeacon) {
            return (TileGarlicBeacon) t;
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
        public String getName() {
            return name;
        }
    }
}
