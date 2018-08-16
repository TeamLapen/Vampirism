package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.TileGarlicBeacon;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 1.10
 *
 * @author maxanier
 */
public class BlockGarlicBeacon extends VampirismBlockContainer {

    public final static String regName = "garlic_beacon";
    public final static PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
    private final static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.75, 0.93);
    private final static AxisAlignedBB COLLISION_BOX_1 = new AxisAlignedBB(0.19, 0, 0.19, 0.81, 0.75, 0.81);
    private final static AxisAlignedBB COLLISION_BOX_2 = new AxisAlignedBB(0.07, 0, 0.07, 0.93, 0.19, 0.93);

    public BlockGarlicBeacon() {
        super(regName, Material.ROCK);
        this.setHasFacing();
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, Type.NORMAL));
        this.setHardness(3);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX_1);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX_2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if ((stack.getMetadata()) > 0) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate(getUnlocalizedName() + "." + Type.fromId(stack.getMetadata()).getName()));
        }
        tooltip.add(UtilLib.translateFormatted(getUnlocalizedName() + ".tooltip1"));
        int c = 1 + 2 * (stack.getMetadata() == Type.IMPROVED.getId() ? Balance.hps.GARLIC_DIFFUSOR_ENHANCED_DISTANCE : (stack.getMetadata() == Type.WEAK.getId() ? Balance.hps.GARLIC_DIFFUSOR_WEAK_DISTANCE : Balance.hps.GARLIC_DIFFUSOR_NORMAL_DISTANCE));
        tooltip.add(UtilLib.translateFormatted(getUnlocalizedName() + ".tooltip2", c, c));
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        TileGarlicBeacon tile = new TileGarlicBeacon();
        tile.setType(Type.fromId(meta >> 2));
        return tile;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).id;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

        return BOUNDING_BOX;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | state.getValue(TYPE).id << 2;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ModBlocks.garlic_beacon, 1, state.getValue(TYPE).getId());
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(TYPE, Type.fromId(placer.getHeldItem(hand).getMetadata()));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing face = EnumFacing.getHorizontal(meta);
        Type t = Type.fromId(meta >> 2);
        return this.getDefaultState().withProperty(FACING, face).withProperty(TYPE, t);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Type t : Type.values()) {
            items.add(new ItemStack(this, 1, t.id));
        }
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
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing faing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (!heldItem.isEmpty() && ModItems.purified_garlic.equals(heldItem.getItem())) {
            if (!worldIn.isRemote) {
                TileGarlicBeacon t = getTile(worldIn, pos);
                if (t != null) {
                    if (t.getFuelTime() > 0) {
                        playerIn.sendMessage(new TextComponentTranslation("tile.vampirism.garlic_beacon.already_fueled"));
                    } else {
                        t.onFueled();
                        if (!playerIn.capabilities.isCreativeMode) heldItem.shrink(1);
                        playerIn.sendMessage(new TextComponentTranslation("tile.vampirism.garlic_beacon.successfully_fueled"));
                    }

                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        TileGarlicBeacon tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.onTouched(playerIn);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, TYPE);
    }

    @Nullable
    private TileGarlicBeacon getTile(IBlockAccess world, BlockPos pos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileGarlicBeacon) {
            return (TileGarlicBeacon) t;
        }
        return null;
    }

    public enum Type implements IStringSerializable {
        NORMAL("standard", 0), IMPROVED("improved", 1), WEAK("weak", 2);

        private static Type fromId(int id) {
            for (Type t : values()) {
                if (t.id == id) return t;
            }
            return NORMAL;
        }

        private final String name;
        private final int id;

        Type(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
