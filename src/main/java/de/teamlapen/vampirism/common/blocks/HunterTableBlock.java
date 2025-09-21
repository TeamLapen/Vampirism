package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.vampirism.common.blocks.base.BaseHorizontalBlock;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.inventory.HunterTableMenu;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class HunterTableBlock extends BaseHorizontalBlock {

    public static final BooleanProperty WEAPON_TABLE = BooleanProperty.create("weapon_table");
    public static final BooleanProperty ALCHEMICAL_CAULDRON = BooleanProperty.create("alchemical_cauldron");
    public static final BooleanProperty POTION_TABLE = BooleanProperty.create("potion_table");

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 8, 0, 16, 10, 16),
            Block.box(1, 0, 1, 4, 8, 4),
            Block.box(12, 0, 1, 15, 8, 4),
            Block.box(12, 0, 12, 15, 8, 15),
            Block.box(1, 0, 12, 4, 8, 15),
            Block.box(2, 10, 6, 8, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public HunterTableBlock(Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WEAPON_TABLE, false).setValue(ALCHEMICAL_CAULDRON, false).setValue(POTION_TABLE, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateVariantValues(context.getLevel(), context.getClickedPos(), this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        BlockState newState = updateVariantValues(level, pos, state);
        if (!newState.equals(state)) {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WEAPON_TABLE, ALCHEMICAL_CAULDRON, POTION_TABLE);
    }

    private static BlockState updateVariantValues(LevelReader level, BlockPos pos, BlockState state) {
        List<Block> relativeBlocks = Stream.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).map(direction -> level.getBlockState(pos.relative(direction)).getBlock()).toList();

        return state.setValue(WEAPON_TABLE, relativeBlocks.contains(ModBlocks.WEAPON_TABLE.get()))
                .setValue(ALCHEMICAL_CAULDRON, relativeBlocks.contains(ModBlocks.ALCHEMICAL_CAULDRON.get()))
                .setValue(POTION_TABLE, relativeBlocks.contains(ModBlocks.POTION_TABLE.get()));
    }

    public static int getVariantValue(BlockState state) {
        int value = 0;
        if (state.hasProperty(WEAPON_TABLE) && state.getValue(WEAPON_TABLE)) value++;
        if (state.hasProperty(ALCHEMICAL_CAULDRON) && state.getValue(ALCHEMICAL_CAULDRON)) value++;
        if (state.hasProperty(POTION_TABLE) && state.getValue(POTION_TABLE)) value++;
        return value;
    }
}
