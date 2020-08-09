package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.container.HunterTableContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Table for hunter "education/leveling"
 */
public class HunterTableBlock extends VampirismBlock {
    public static final String name = "hunter_table";
    public static final ITextComponent containerName = new TranslationTextComponent("container.hunter_table");
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<TABLE_VARIANT> VARIANT = EnumProperty.create("variant", TABLE_VARIANT.class);
    private static final VoxelShape SOUTH = makeShape();
    private static final VoxelShape WEST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape NORTH = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape EAST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0, 0, 0, 2, 10, 2);
        VoxelShape b = Block.makeCuboidShape(14, 0, 0, 16, 10, 2);
        VoxelShape c = Block.makeCuboidShape(0, 0, 14, 2, 10, 16);
        VoxelShape d = Block.makeCuboidShape(14, 0, 14, 16, 10, 16);

        VoxelShape e = Block.makeCuboidShape(1, 8, 1, 15, 10, 15);
        VoxelShape f = Block.makeCuboidShape(8.5, 10, 3.5, 13.5, 11, 10);

        VoxelShape d1 = VoxelShapes.or(a, b);
        VoxelShape d2 = VoxelShapes.or(c, d);

        VoxelShape d3 = VoxelShapes.or(d1, d2);
        VoxelShape f1 = VoxelShapes.or(e, f);

        VoxelShape g = VoxelShapes.or(d3, f1);
        return g;
    }

    public HunterTableBlock() {
        super(name, Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(VARIANT, TABLE_VARIANT.SIMPLE));
    }



    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
        }
        return NORTH;
    }

    public static TABLE_VARIANT getTierFor(boolean weapon_table, boolean potion_table, boolean cauldron) {
        return weapon_table ? (potion_table ? (cauldron ? TABLE_VARIANT.COMPLETE : TABLE_VARIANT.WEAPON_POTION) : (cauldron ? TABLE_VARIANT.WEAPON_CAULDRON : TABLE_VARIANT.WEAPON)) : (potion_table ? (cauldron ? TABLE_VARIANT.POTION_CAULDRON : TABLE_VARIANT.POTION) : (cauldron ? TABLE_VARIANT.CAULDRON : TABLE_VARIANT.SIMPLE));
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction facing = context.getPlacementHorizontalFacing();
        return this.getDefaultState().with(FACING, facing).with(VARIANT, determineTier(context.getWorld(), context.getPos(), facing));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            if (player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> new HunterTableContainer(id, playerInventory, IWorldPosCallable.of(playerIn.world, pos)), new TranslationTextComponent("container.crafting")), pos);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT);
    }

    protected TABLE_VARIANT determineTier(IWorldReader world, BlockPos pos, Direction facing) {
        Block behind = world.getBlockState(pos.offset(facing)).getBlock();
        Block left = world.getBlockState(pos.offset(facing.rotateY())).getBlock();
        Block right = world.getBlockState(pos.offset(facing.rotateYCCW())).getBlock();
        Block front = world.getBlockState(pos.offset(facing.getOpposite())).getBlock();
        boolean weapon_table = left == ModBlocks.weapon_table || right == ModBlocks.weapon_table || behind == ModBlocks.weapon_table || front == ModBlocks.weapon_table;
        boolean potion_table = left == ModBlocks.potion_table || right == ModBlocks.potion_table || behind == ModBlocks.potion_table || front == ModBlocks.potion_table;
        boolean cauldron = left == ModBlocks.alchemical_cauldron || right == ModBlocks.alchemical_cauldron || behind == ModBlocks.alchemical_cauldron || front == ModBlocks.alchemical_cauldron;

        return getTierFor(weapon_table, potion_table, cauldron);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (fromPos.getY() != pos.getY()) return;
        TABLE_VARIANT newVariant = determineTier(worldIn, pos, state.get(FACING));
        if (newVariant != state.get(VARIANT)) {
            worldIn.setBlockState(pos, state.with(VARIANT, newVariant), 2);
        }
    }

    public enum TABLE_VARIANT implements IStringSerializable {
        SIMPLE("simple", 0), WEAPON("weapon", 1), CAULDRON("cauldron", 1), POTION("potion", 1), WEAPON_CAULDRON("weapon_cauldron", 2), WEAPON_POTION("weapon_potion", 2), POTION_CAULDRON("potion_cauldron", 2), COMPLETE("complete", 3);
        public final String name;
        public final int tier;

        TABLE_VARIANT(String n, int tier) {
            this.name = n;
            this.tier = tier;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
