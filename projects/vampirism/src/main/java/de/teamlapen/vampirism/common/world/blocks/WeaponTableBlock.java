package de.teamlapen.vampirism.common.world.blocks;

import com.google.common.collect.MapMaker;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.faction.common.world.blocks.base.BaseHorizontalBlock;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.inventory.WeaponTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class WeaponTableBlock extends BaseHorizontalBlock {

    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;

    public static final IntegerProperty LAVA = IntegerProperty.create("lava", 0, MAX_LAVA);

    private static final VoxelShape SHAPE = Shapes.or(Block.box(3, 0, 0, 13, 2, 8), Block.box(4, 2, 1, 12, 3, 7), Block.box(5, 3, 2, 11, 6, 6), Block.box(3, 6, 0, 13, 9.5, 8), Block.box(0, 1, 9, 7, 2, 16), Block.box(0, 0, 9, 2, 1, 11), Block.box(5, 0, 9, 7, 1, 11), Block.box(0, 0, 14, 2, 1, 16), Block.box(5, 0, 14, 7, 1, 16), Block.box(0, 1, 9, 1, 7, 16), Block.box(0, 1, 9, 7, 7, 10), Block.box(0, 1, 15, 7, 7, 16), Block.box(6, 1, 9, 7, 7, 16), Block.box(10, 0, 11, 15, 3, 14), Block.box(12, 3, 12, 13, 10, 13));

    private static final Component NAME = Component.translatable("container.vampirism.weapon_table");

    public WeaponTableBlock(Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LAVA) == 0 ? 0 : state.getValue(LAVA) * 2 + 5), SHAPE);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LAVA, 0).setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, playerInventory, playerEntity) -> new WeaponTableMenu(id, playerInventory, ContainerLevelAccess.create(level, pos)), NAME);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        var capability = heldItem.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerInteraction(player, hand));
        if (capability != null && ResourceHandlerUtil.contains(capability, FluidResource.of(Fluids.LAVA))) {
            var moved = ResourceHandlerUtil.move(capability, level.getCapability(Capabilities.Fluid.BLOCK, pos, null), x -> x.is(Fluids.LAVA), MAX_LAVA * MB_PER_META, null);
            if (moved > 0) {
                return InteractionResult.SUCCESS_SERVER;
            } else {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (canUse(player)) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> new WeaponTableMenu(id, playerInventory, ContainerLevelAccess.create(playerIn.level(), pos)), NAME), pos);
            } else {
                player.displayClientMessage(Helper.isHunter(player) ? FactionRestriction.MESSAGE_MISSING_SKILLS : FactionRestriction.getFactionRestrictionMessage(ModFactions.HUNTER.get()), true);
            }
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAVA);
    }

    /**
     * @return If the given player is allowed to use this.
     */
    private boolean canUse(Player player) {
        if (Helper.isHunter(player)) {
            return HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.WEAPON_TABLE);
        }
        return false;
    }

    private record WrapperLocation(Level level, BlockPos pos) {
        public BlockState getBlockState() {
            return this.level.getBlockState(pos);
        }

        public void setBlockState(BlockState state) { this.level.setBlock(this.pos, state, 0);}

        public void sendUpdate() {
            var state = getBlockState();
            this.level.sendBlockUpdated(pos, state, state,3);
        }
    }

    private static final Map<WrapperLocation, WeaponTableResourceHandler> wrappers = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();

    public static ResourceHandler<FluidResource> getResourceHandler(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
        var location = new WrapperLocation(level, pos.immutable());
        return wrappers.computeIfAbsent(location, WeaponTableResourceHandler::new);
    }

    private static class WeaponTableResourceHandler extends SnapshotJournal<BlockState> implements ResourceHandler<FluidResource> {

        private final WrapperLocation location;

        public WeaponTableResourceHandler(WrapperLocation location) {
            this.location = location;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            Objects.checkIndex(index, size());

            BlockState state = location.getBlockState();
            return FluidResource.of(state.getValue(WeaponTableBlock.LAVA) > 0 ? Fluids.LAVA : Fluids.EMPTY);
        }

        @Override
        public long getAmountAsLong(int index) {
            Objects.checkIndex(index, size());

            BlockState blockState = location.getBlockState();

            return blockState.getValue(WeaponTableBlock.LAVA) * MB_PER_META;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            Objects.checkIndex(index, size());
            return MAX_LAVA * MB_PER_META;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            Objects.checkIndex(index, size());
            TransferPreconditions.checkNonEmpty(resource);

            return resource.is(Fluids.LAVA);
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            Objects.checkIndex(index, size());
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

            BlockState blockState = location.getBlockState();
            int currentCount = blockState.getValue(WeaponTableBlock.LAVA);

            int toAdd = amount / MB_PER_META;
            int actualAdd = Math.min(MAX_LAVA - currentCount, toAdd);

            updateSnapshots(transaction);

            this.location.setBlockState(blockState.setValue(WeaponTableBlock.LAVA, currentCount + actualAdd));

            return actualAdd * MB_PER_META;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            Objects.checkIndex(index, size());
            return 0;
        }

        @Override
        protected BlockState createSnapshot() {
            return this.location.getBlockState();
        }

        @Override
        protected void revertToSnapshot(@Nullable BlockState snapshot) {
            if (snapshot != null) {
                this.location.setBlockState(snapshot);
            }
        }

        @Override
        protected void onRootCommit(@Nullable BlockState originalState) {
            this.location.sendUpdate();
        }
    }
}
