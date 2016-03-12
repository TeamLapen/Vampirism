package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

import javax.annotation.Nullable;

/**
 * Block coffin. Uses Properties from BlockBed: {@link BlockBed#OCCUPIED}, {@link BlockBed#FACING} {@link BlockBed#PART}
 */
public class BlockCoffin extends VampirismBlockContainer {

    public static final String name = "blockCoffin";
    public final static Material material = Material.wood;

    public static boolean isOccupied(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(BlockBed.OCCUPIED);
    }

    public static void setCoffinOccupied(World world, BlockPos pos, boolean value) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.withProperty(BlockBed.OCCUPIED, value), 4);
    }

    public static boolean isHead(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD;
    }

    private static void wakeUpPlayer(EntityPlayer player) {
        //TODO
    }

    private static EntityPlayer.EnumStatus tryToSleepInCoffin(EntityPlayer player, BlockPos pos) {
        //TODO
        return EntityPlayer.EnumStatus.OTHER_PROBLEM;
    }

    private final String TAG = "BlockCoffin";

    public BlockCoffin() {
        super(name, material);
        this.setCreativeTab(null);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileCoffin te = (TileCoffin) world.getTileEntity(pos);
        if (te == null)
            return;
        world.setBlockToAir(te.otherPos);
        world.removeTileEntity(te.otherPos);
        if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD)
            world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.itemCoffin, 1)));
        if (state.getValue(BlockBed.OCCUPIED).booleanValue()) {
            wakeSleepingPlayer(world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCoffin();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.offset(state.getValue(BlockDirectional.FACING)));

            if (iblockstate1.getBlock() == this) {
                state = state.withProperty(BlockBed.OCCUPIED, iblockstate1.getValue(BlockBed.OCCUPIED));
            }
        }
        return state;
    }

    public EnumFacing getCoffinDirection(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(BlockDirectional.FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        byte b0 = 0;
        int i = b0 | state.getValue(BlockDirectional.FACING).getHorizontalIndex();
        if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
            i |= 8;
            if (state.getValue(BlockBed.OCCUPIED).booleanValue()) {
                i |= 4;
            }
        }
        return i;
    }

    // Miscellaneous methods (rendertype etc.)
    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public int getRenderType() {
        return 2;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumFacing = EnumFacing.getHorizontal(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD).withProperty(BlockDirectional.FACING, enumFacing).withProperty(BlockBed.OCCUPIED, Boolean.valueOf((meta & 4) > 0)) : this.getDefaultState().withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT).withProperty(BlockDirectional.FACING, enumFacing);
    }

    @Override
    public boolean isBed(IBlockAccess world, BlockPos pos, Entity player) {
        return true;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            // Gets the coordinates of the primary block
            if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
                TileCoffin te = (TileCoffin) world.getTileEntity(pos);
                pos = te.otherPos;
            }
            if (player.isSneaking()) {
                if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemDye) {
                    return false;
                }

            }

            if (world.provider.canRespawnHere() && world.getBiomeGenForCoords(pos) != BiomeGenBase.hell) {
                if (state.getValue(BlockBed.OCCUPIED).booleanValue()) {
                    player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.coffin.occupied"));
                    return true;
                }

                EntityPlayer.EnumStatus enumstatus = tryToSleepInCoffin(player, pos);

                if (enumstatus == EntityPlayer.EnumStatus.OK) {
                    setCoffinOccupied(world, pos, player, true);
                    world.getTileEntity(pos).markDirty();
                    return true;
                } else {
                    if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
                        player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.coffin.noSleep"));
                    } else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
                    }
                    return true;
                }
            } else {
                player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.coffin.wrong_dimension"));
                return true;
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        TileCoffin tileEntity = (TileCoffin) world.getTileEntity(pos);
        if (tileEntity != null) {
            if (!(world.getBlockState(tileEntity.otherPos).getBlock() instanceof BlockCoffin)) {
                // Logger.d(TAG, "Other coffin block destroyed, removing this one");
                this.breakBlock(world, pos, state);
                // world.setBlockToAir(x, y, z);
                // world.removeTileEntity(x, y, z);
            }
        }
    }

    public void setCoffinOccupied(World world, BlockPos pos, @Nullable EntityPlayer player, boolean flag) {
        setBedOccupied(world, pos, player, flag);
        ((TileCoffin) world.getTileEntity(pos)).occupied = flag;
        // if(!world.isRemote)
        // ((EntityPlayerMP)
        // player).playerNetServerHandler.sendPacket(world.getTileEntity(x, y,
        // z).getDescriptionPacket());
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, BlockBed.OCCUPIED, BlockBed.PART, BlockDirectional.FACING);
    }

    private void wakeSleepingPlayer(World world, BlockPos pos) {
        if (world.isRemote)
            return;
        WorldServer w = (WorldServer) world;
        for (int i = 0; i < w.playerEntities.size(); i++) {
            EntityPlayer p = w.playerEntities.get(i);
            if (p.isPlayerSleeping()) {
                // Logger.d("BlockCoffin", String.format(
                // "Found sleeping player: x=%s, y=%s, z=%s",
                // p.playerLocation.posX, p.playerLocation.posY,
                // p.playerLocation.posZ));
                if (p.playerLocation.equals(pos)) {
                    wakeUpPlayer(p);//wakeUpPlayer(false, true, false, false);
                }
            }
        }
    }

}
