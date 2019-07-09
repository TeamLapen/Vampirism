package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.tileentity.TileTotem;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Top of a two block multiblock structure.
 * Is destroyed if lower block is broken.
 * Can only be broken by player if tile entity allows it
 * <p>
 * Has both model renderer (with color/tint) and TESR (used for beam)
 */
public class TotemTopBlock extends VampirismBlockContainer {

    protected static final AxisAlignedBB BBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.7D, 1.0D); //TODO 1.13 shape


    private final static String regName = "totem_top";

    public TotemTopBlock() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(40, 2000).sound(SoundType.STONE));

    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileTotem();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public float getExplosionResistance() {
        return Float.MAX_VALUE;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTotem) {
            ((TileTotem) tile).updateTotem();
            world.addBlockEvent(pos, this, 1, 0); //Notify client about render update
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) return true;
        IPlayableFaction f = FactionPlayerHandler.get(player).getCurrentFaction();
        TileTotem t = getTile(world, pos);
        if (f != null && t != null && world.getBlockState(pos.down()).getBlock().equals(ModBlocks.totem_base)) {
            t.initiateCapture(f, player);
            return true;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
        super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
    }



    @Nullable
    private TileTotem getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTotem) return (TileTotem) tile;
        return null;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileTotem tile = getTile(world, pos);
        if (tile != null) {
            if (!tile.canPlayerRemoveBlock(player)) {
                return false;
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
