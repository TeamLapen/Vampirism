package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.container.HunterTableContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Table for hunter "education/leveling"
 */
public class HunterTableBlock extends VampirismHorizontalBlock {
    public static final EnumProperty<TABLE_VARIANT> VARIANT = EnumProperty.create("variant", TABLE_VARIANT.class);
    private static final VoxelShape SOUTH = makeShape();
    private static final VoxelShape WEST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape NORTH = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape EAST = UtilLib.rotateShape(SOUTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    private static @NotNull VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 2, 10, 2);
        VoxelShape b = Block.box(14, 0, 0, 16, 10, 2);
        VoxelShape c = Block.box(0, 0, 14, 2, 10, 16);
        VoxelShape d = Block.box(14, 0, 14, 16, 10, 16);

        VoxelShape e = Block.box(1, 8, 1, 15, 10, 15);
        VoxelShape f = Block.box(8.5, 10, 3.5, 13.5, 11, 10);

        VoxelShape d1 = Shapes.or(a, b);
        VoxelShape d2 = Shapes.or(c, d);

        VoxelShape d3 = Shapes.or(d1, d2);
        VoxelShape f1 = Shapes.or(e, f);

        return Shapes.or(d3, f1);
    }

    public static @NotNull TABLE_VARIANT getTierFor(boolean weapon_table, boolean potion_table, boolean cauldron) {
        return weapon_table ? (potion_table ? (cauldron ? TABLE_VARIANT.COMPLETE : TABLE_VARIANT.WEAPON_POTION) : (cauldron ? TABLE_VARIANT.WEAPON_CAULDRON : TABLE_VARIANT.WEAPON)) : (potion_table ? (cauldron ? TABLE_VARIANT.POTION_CAULDRON : TABLE_VARIANT.POTION) : (cauldron ? TABLE_VARIANT.CAULDRON : TABLE_VARIANT.SIMPLE));
    }


    public HunterTableBlock() {
        super(Properties.of(Material.WOOD).strength(0.5f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VARIANT, TABLE_VARIANT.SIMPLE));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, facing).setValue(VARIANT, determineTier(context.getLevel(), context.getClickedPos(), facing));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        if (fromPos.getY() != pos.getY()) return;
        TABLE_VARIANT newVariant = determineTier(worldIn, pos, state.getValue(FACING));
        if (newVariant != state.getValue(VARIANT)) {
            worldIn.setBlock(pos, state.setValue(VARIANT, newVariant), 2);
        }
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, playerInventory, playerIn) -> new HunterTableContainer(id, playerInventory, ContainerLevelAccess.create(playerIn.level, pos)), Component.translatable("container.crafting")), pos);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT);
    }

    protected TABLE_VARIANT determineTier(@NotNull LevelReader world, @NotNull BlockPos pos, @NotNull Direction facing) {
        Block behind = world.getBlockState(pos.relative(facing)).getBlock();
        Block left = world.getBlockState(pos.relative(facing.getClockWise())).getBlock();
        Block right = world.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock();
        Block front = world.getBlockState(pos.relative(facing.getOpposite())).getBlock();
        boolean weapon_table = left == ModBlocks.WEAPON_TABLE.get() || right == ModBlocks.WEAPON_TABLE.get() || behind == ModBlocks.WEAPON_TABLE.get() || front == ModBlocks.WEAPON_TABLE.get();
        boolean potion_table = left == ModBlocks.POTION_TABLE.get() || right == ModBlocks.POTION_TABLE.get() || behind == ModBlocks.POTION_TABLE.get() || front == ModBlocks.POTION_TABLE.get();
        boolean cauldron = left == ModBlocks.ALCHEMICAL_CAULDRON.get() || right == ModBlocks.ALCHEMICAL_CAULDRON.get() || behind == ModBlocks.ALCHEMICAL_CAULDRON.get() || front == ModBlocks.ALCHEMICAL_CAULDRON.get();

        return getTierFor(weapon_table, potion_table, cauldron);
    }

    public enum TABLE_VARIANT implements StringRepresentable {
        SIMPLE("simple", 0), WEAPON("weapon", 1), CAULDRON("cauldron", 1), POTION("potion", 1), WEAPON_CAULDRON("weapon_cauldron", 2), WEAPON_POTION("weapon_potion", 2), POTION_CAULDRON("potion_cauldron", 2), COMPLETE("complete", 3);
        public final String name;
        public final int tier;

        TABLE_VARIANT(String n, int tier) {
            this.name = n;
            this.tier = tier;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
