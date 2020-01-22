package de.teamlapen.vampirism.blocks;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Top of a two block multiblock structure.
 * Is destroyed if lower block is broken.
 * Can only be broken by player if tile entity allows it
 * <p>
 * Has both model renderer (with color/tint) and TESR (used for beam)
 */
public class TotemTopBlock extends VampirismBlockContainer {
    private static final VoxelShape shape = makeShape();
    private final static String regName = "totem_top";
    private static final Map<ResourceLocation, TotemTopBlock> factionTotems = Maps.newHashMap();

    public static TotemTopBlock getTotem(ResourceLocation faction) {
        return factionTotems.get(faction);
    }

    public static Block[] getTotems() {
        return factionTotems.values().toArray(new Block[0]);
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(3, 0, 3, 13, 10, 13);
        VoxelShape b = Block.makeCuboidShape(1, 1, 1, 15, 9, 15);
        return VoxelShapes.or(a, b);
    }

    public final ResourceLocation faction;

    @Deprecated
    public TotemTopBlock() {
        super(regName, Properties.create(Material.ROCK).hardnessAndResistance(40, 2000).sound(SoundType.STONE));
        this.faction = new ResourceLocation("none");
        factionTotems.put(this.faction, this);
    }

    /**
     * @param faction faction must be faction registryname;
     */
    public TotemTopBlock(ResourceLocation faction) {
        super(regName + "_" + faction.getNamespace() + "_" + faction.getPath(), Properties.create(Material.ROCK).hardnessAndResistance(40, 2000).sound(SoundType.STONE));
        this.faction = faction;
        factionTotems.put(this.faction, this);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public float getExplosionResistance() {
        return Float.MAX_VALUE;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return ModTiles.totem.create();
    }



    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isRemote) return;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TotemTileEntity) {
            ((TotemTileEntity) tile).updateTileStatus();
            worldIn.addBlockEvent(pos, this, 1, 0); //Notify client about render update
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) return ActionResultType.SUCCESS;
        TotemTileEntity t = getTile(world, pos);
        if (t != null && world.getBlockState(pos.down()).getBlock().equals(ModBlocks.totem_base)) {
            t.initiateCapture(player);
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TotemTileEntity tile = getTile(world, pos);
        if (tile != null) {
            if (!tile.canPlayerRemoveBlock(player)) {
                return false;
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Nullable
    private TotemTileEntity getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TotemTileEntity) return (TotemTileEntity) tile;
        return null;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(newState.getBlock() instanceof TotemTopBlock)) {
            worldIn.removeTileEntity(pos);
        }
    }
}
