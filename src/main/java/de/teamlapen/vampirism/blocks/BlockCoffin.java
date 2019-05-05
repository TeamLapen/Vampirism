package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Block coffin.
 */
public class BlockCoffin extends VampirismBlockContainer {

    public static final String name = "block_coffin";
    public final static Material material = Material.WOOD;
    public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
    private final static String TAG = "BlockCoffin";

    public static boolean isOccupied(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).getValue(OCCUPIED);
    }

    public static void setCoffinOccupied(World world, BlockPos pos, boolean value) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.withProperty(OCCUPIED, value), 4);
    }

    public static boolean isHead(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).getValue(PART) == EnumPartType.HEAD;
    }

    public BlockCoffin() {
        super(name, material);
        this.setCreativeTab(null);
        this.setDefaultState(this.blockState.getBaseState().withProperty(OCCUPIED, Boolean.TRUE).withProperty(PART, EnumPartType.FOOT));
        this.setHasFacing();
        setHardness(0.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCoffin();
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (state.getValue(PART) == EnumPartType.FOOT) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    public IBlockState getActualState(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        if (state.getValue(PART) == EnumPartType.FOOT) {
            IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this) {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }

        return state;
    }

    @Nonnull
    @Override
    public EnumFacing getBedDirection(@Nonnull IBlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return getActualState(state, world, pos).getValue(FACING);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, @Nonnull IBlockState state) {
        return new ItemStack(ModItems.item_coffin);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(PART) == EnumPartType.HEAD ? null : ModItems.item_coffin;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(PART) == EnumPartType.HEAD) {
            i |= 8;

            if (state.getValue(OCCUPIED)) {
                i |= 4;
            }
        }

        return i;
    }



    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PART, EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, (meta & 4) > 0) : this.getDefaultState().withProperty(PART, EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    @Override
    public boolean isBed(IBlockState state, IBlockReader world, BlockPos pos, Entity player) {
        return true;
    }

    @Override
    public boolean isBedFoot(IBlockReader world, @Nonnull BlockPos pos) {
        return getActualState(world.getBlockState(pos), world, pos).getValue(PART) == EnumPartType.FOOT;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == EnumPartType.HEAD) {
            if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
                worldIn.removeBlock(pos);
            }
        } else if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            worldIn.removeBlock(pos);

            if (!worldIn.isRemote) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (worldIn.isRemote) {
            return true;
        } else {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye) {
                TileCoffin tile = (TileCoffin) worldIn.getTileEntity(pos);
                TileEntity other = state.getValue(PART) == EnumPartType.HEAD ? worldIn.getTileEntity(pos.offset(state.getValue(FACING).getOpposite())) : worldIn.getTileEntity(pos.offset(state.getValue(FACING)));
                if (!(other instanceof TileCoffin)) {
                    return true;
                }
                tile.changeColor(heldItem.getMetadata());
                ((TileCoffin) other).changeColor(heldItem.getMetadata());
                if (!playerIn.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
                return true;
            }
            if (state.getValue(PART) != EnumPartType.HEAD) {
                pos = pos.offset(state.getValue(FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (VampirePlayer.get(playerIn).getLevel() == 0) {
                playerIn.sendMessage(new TextComponentTranslation("text.vampirism.coffin.cant_use"));
                return true;
            }


            if (worldIn.provider.canRespawnHere() && worldIn.getBiome(pos) != Biomes.HELL) {
                if (state.getValue(OCCUPIED)) {
                    EntityPlayer entityplayer = this.getPlayerInCoffin(worldIn, pos);

                    if (entityplayer != null) {
                        playerIn.sendMessage(new TextComponentTranslation("text.vampirism.coffin.occupied"));
                        return true;
                    }

                    state = state.withProperty(OCCUPIED, Boolean.FALSE);
                    worldIn.setBlockState(pos, state, 2);
                }

                IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability(playerIn);

                EntityPlayer.SleepResult entityplayer$enumstatus = vampire.trySleep(pos);

                if (entityplayer$enumstatus == EntityPlayer.SleepResult.OK) {
                    state = state.withProperty(OCCUPIED, Boolean.TRUE);
                    worldIn.setBlockState(pos, state, 2);
                    return true;
                } else {
                    if (entityplayer$enumstatus == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                        playerIn.sendMessage(new TextComponentTranslation("text.vampirism.coffin.no_sleep"));
                    } else if (entityplayer$enumstatus == EntityPlayer.SleepResult.NOT_SAFE) {
                        playerIn.sendMessage(new TextComponentTranslation("tile.bed.notSafe"));
                    }

                    return true;
                }
            } else {
                playerIn.sendMessage(new TextComponentTranslation("text.vampirism.coffin.wrong_dimension"));
                return true;
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && state.getValue(PART) == EnumPartType.HEAD) {
            BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());

            if (worldIn.getBlockState(blockpos).getBlock() == this) {
                worldIn.removeBlock(blockpos);
            }
        }
    }

    @Override
    public void setBedOccupied(IBlockReader world, BlockPos pos, EntityPlayer player, boolean occupied) {
        if (world instanceof World) {
            IBlockState state = world.getBlockState(pos);
            state = state.getBlock().getActualState(state, world, pos);
            state = state.withProperty(OCCUPIED, occupied);//In forge 12.16.0.1859-1.9 the vanilla method of this is even wrong, setting it always to true
            ((World) world).setBlockState(pos, state, 2);
        }
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, PART, OCCUPIED);
    }

    /**
     * Finds the player that is currently sleeping in this coffin
     *
     * @param worldIn
     * @param pos
     * @return
     */
    private @Nullable
    EntityPlayer getPlayerInCoffin(World worldIn, BlockPos pos) {
        for (EntityPlayer entityplayer : worldIn.playerEntities) {
            if (VampirePlayer.get(entityplayer).isPlayerSleeping() && entityplayer.bedLocation.equals(pos)) {
                return entityplayer;
            }
        }

        return null;
    }


    public enum EnumPartType implements IStringSerializable {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        EnumPartType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }


}
