package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.container.HunterTableContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Table for hunter "education/leveling"
 */
public class HunterTableBlock extends VampirismHorizontalBlock {
    public static final EnumProperty<TABLE_VARIANT> VARIANT = EnumProperty.create("variant", TABLE_VARIANT.class);
    private static final VoxelShape SOUTH = makeShape();
    private static final VoxelShape WEST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape NORTH = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape EAST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 2, 10, 2);
        VoxelShape b = Block.box(14, 0, 0, 16, 10, 2);
        VoxelShape c = Block.box(0, 0, 14, 2, 10, 16);
        VoxelShape d = Block.box(14, 0, 14, 16, 10, 16);

        VoxelShape e = Block.box(1, 8, 1, 15, 10, 15);
        VoxelShape f = Block.box(8.5, 10, 3.5, 13.5, 11, 10);

        VoxelShape d1 = VoxelShapes.or(a, b);
        VoxelShape d2 = VoxelShapes.or(c, d);

        VoxelShape d3 = VoxelShapes.or(d1, d2);
        VoxelShape f1 = VoxelShapes.or(e, f);

        return VoxelShapes.or(d3, f1);
    }

    public static TABLE_VARIANT getTierFor(boolean weapon_table, boolean potion_table, boolean cauldron) {
        return weapon_table ? (potion_table ? (cauldron ? TABLE_VARIANT.COMPLETE : TABLE_VARIANT.WEAPON_POTION) : (cauldron ? TABLE_VARIANT.WEAPON_CAULDRON : TABLE_VARIANT.WEAPON)) : (potion_table ? (cauldron ? TABLE_VARIANT.POTION_CAULDRON : TABLE_VARIANT.POTION) : (cauldron ? TABLE_VARIANT.CAULDRON : TABLE_VARIANT.SIMPLE));
    }


    public HunterTableBlock() {
        super(Properties.of(Material.WOOD).strength(0.5f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VARIANT, TABLE_VARIANT.SIMPLE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction facing = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, facing).setValue(VARIANT, determineTier(context.getLevel(), context.getClickedPos(), facing));
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (fromPos.getY() != pos.getY()) return;
        TABLE_VARIANT newVariant = determineTier(worldIn, pos, state.getValue(FACING));
        if (newVariant != state.getValue(VARIANT)) {
            worldIn.setBlock(pos, state.setValue(VARIANT, newVariant), 2);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide) {
            if (player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> new HunterTableContainer(id, playerInventory, IWorldPosCallable.create(playerIn.level, pos)), new TranslationTextComponent("container.crafting")), pos);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT);
    }

    protected TABLE_VARIANT determineTier(IWorldReader world, BlockPos pos, Direction facing) {
        Block behind = world.getBlockState(pos.relative(facing)).getBlock();
        Block left = world.getBlockState(pos.relative(facing.getClockWise())).getBlock();
        Block right = world.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock();
        Block front = world.getBlockState(pos.relative(facing.getOpposite())).getBlock();
        boolean weapon_table = left == ModBlocks.WEAPON_TABLE.get() || right == ModBlocks.WEAPON_TABLE.get() || behind == ModBlocks.WEAPON_TABLE.get() || front == ModBlocks.WEAPON_TABLE.get();
        boolean potion_table = left == ModBlocks.POTION_TABLE.get() || right == ModBlocks.POTION_TABLE.get() || behind == ModBlocks.POTION_TABLE.get() || front == ModBlocks.POTION_TABLE.get();
        boolean cauldron = left == ModBlocks.ALCHEMICAL_CAULDRON.get() || right == ModBlocks.ALCHEMICAL_CAULDRON.get() || behind == ModBlocks.ALCHEMICAL_CAULDRON.get() || front == ModBlocks.ALCHEMICAL_CAULDRON.get();

        return getTierFor(weapon_table, potion_table, cauldron);
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
        public String getSerializedName() {
            return name;
        }
    }
}
