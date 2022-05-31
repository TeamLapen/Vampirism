package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top of a two block multiblock structure.
 * Is destroyed if lower block is broken.
 * Can only be broken by player if tile entity allows it
 * <p>
 * Has both model renderer (with color/tint) and TESR (used for beam)
 */
public class TotemTopBlock extends ContainerBlock {
    private static final List<TotemTopBlock> blocks = new ArrayList<>();
    private static final VoxelShape shape = makeShape();

    public static List<TotemTopBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(3, 0, 3, 13, 10, 13);
        VoxelShape b = Block.box(1, 1, 1, 15, 9, 15);
        return VoxelShapes.or(a, b);
    }
    public final ResourceLocation faction;
    private final boolean crafted;

    /**
     * @param faction faction must be faction registryname;
     */
    public TotemTopBlock(boolean crafted, ResourceLocation faction) {
        super(Properties.of(Material.STONE).strength(12, 2000).sound(SoundType.STONE));
        this.faction = faction;
        this.crafted = crafted;
        blocks.add(this);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public float getExplosionResistance() {
        return Float.MAX_VALUE;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) return;
        TileEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TotemTileEntity) {
            ((TotemTileEntity) tile).updateTileStatus();
            worldIn.blockEvent(pos, this, 1, 0); //Notify client about render update
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    public boolean isCrafted() {
        return crafted;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader worldIn) {
        return ModTiles.TOTEM.get().create();
    }

    @Override
    public void onRemove(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(newState.getBlock() instanceof TotemTopBlock)) {
            worldIn.removeBlockEntity(pos);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) return ActionResultType.SUCCESS;
        TotemTileEntity t = getTile(world, pos);
        if (t != null && world.getBlockState(pos.below()).getBlock().equals(ModBlocks.TOTEM_BASE.get())) {
            t.initiateCapture(player);
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        TotemTileEntity tile = getTile(world, pos);
        if (tile != null) {
            if (!tile.canPlayerRemoveBlock(player)) {
                return false;
            }
        }
        if (super.removedByPlayer(state, world, pos, player, willHarvest, fluid)) {
            if (tile != null && tile.getControllingFaction() != null) {
                tile.notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.village_abandoned"));
            }
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private TotemTileEntity getTile(World world, BlockPos pos) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TotemTileEntity) return (TotemTileEntity) tile;
        return null;
    }
}
