package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.inventory.HunterTableMenu;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class HunterTableBlock extends VampirismHorizontalBlock {

    public static final EnumProperty<TableVariant> VARIANT = EnumProperty.create("variant", TableVariant.class);

    private static final VoxelShape SHAPE = Stream.of(Block.box(1, 0, 1, 15, 8, 15), Block.box(0, 8, 0, 16, 10, 16), Block.box(2, 10, 6, 8, 13, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public HunterTableBlock(Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VARIANT, TableVariant.SIMPLE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, facing).setValue(VARIANT, determineTier(context.getLevel(), context.getClickedPos(), facing));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @Nullable Orientation orientation, boolean isMoving) {
        TableVariant newVariant = determineTier(worldIn, pos, state.getValue(FACING));
        if (newVariant != state.getValue(VARIANT)) {
            worldIn.setBlock(pos, state.setValue(VARIANT, newVariant), 2);
        }
    }

    @NotNull
    @Override
    public InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(ModStats.INTERACT_WITH_RESEARCH_TABLE.get());
                if (Helper.isHunter(serverPlayer)) {
                    player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> new HunterTableMenu(id, playerInventory, ContainerLevelAccess.create(playerIn.level(), pos)), Component.translatable("container.crafting")), pos);
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.unfamiliar"), true);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT);
    }

    private TableVariant determineTier(@NotNull LevelReader world, @NotNull BlockPos pos, @NotNull Direction facing) {
        List<Block> relativeBlocks = List.of(
                world.getBlockState(pos.relative(facing)).getBlock(),
                world.getBlockState(pos.relative(facing.getClockWise())).getBlock(),
                world.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock(),
                world.getBlockState(pos.relative(facing.getOpposite())).getBlock()
        );

        boolean weaponTable = relativeBlocks.contains(ModBlocks.WEAPON_TABLE.get());
        boolean cauldron = relativeBlocks.contains(ModBlocks.ALCHEMICAL_CAULDRON.get());
        boolean potionTable = relativeBlocks.contains(ModBlocks.POTION_TABLE.get());

        int points = (weaponTable ? 1 : 0) + (cauldron ? 2 : 0) + (potionTable ? 4 : 0);

        return TableVariant.getByPoints(points);
    }

    public enum TableVariant implements StringRepresentable {
        SIMPLE("simple", 0, 0),
        WEAPON("weapon", 1, 1),
        CAULDRON("cauldron", 1, 2),
        POTION("potion", 1, 4),
        WEAPON_CAULDRON("weapon_cauldron", 2, 3),
        WEAPON_POTION("weapon_potion", 2, 5),
        POTION_CAULDRON("potion_cauldron", 2, 6),
        COMPLETE("complete", 3, 7);

        public final String name;
        public final int tier;
        public final int requiredPoints;

        TableVariant(String name, int tier, int requiredPoints) {
            this.name = name;
            this.tier = tier;
            this.requiredPoints = requiredPoints;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public static TableVariant getByPoints(int points) {
            for (TableVariant variant : values()) {
                if (variant.requiredPoints == points) {
                    return variant;
                }
            }
            return SIMPLE;
        }
    }
}
