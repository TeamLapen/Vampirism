package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.vampirism.common.blocks.base.BaseHorizontalBlock;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.inventory.WeaponTableMenu;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.IntStream;

public class WeaponTableBlock extends BaseHorizontalBlock {

    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;

    public static final IntegerProperty LAVA = IntegerProperty.create("lava", 0, MAX_LAVA);

    private static final VoxelShape SHAPE = Shapes.or(Block.box(3, 0, 0, 13, 2, 8), Block.box(4, 2, 1, 12, 3, 7), Block.box(5, 3, 2, 11, 6, 6), Block.box(3, 6, 0, 13, 9.5, 8), Block.box(0, 1, 9, 7, 2, 16), Block.box(0, 0, 9, 2, 1, 11), Block.box(5, 0, 9, 7, 1, 11), Block.box(0, 0, 14, 2, 1, 16), Block.box(5, 0, 14, 7, 1, 16), Block.box(0, 1, 9, 1, 7, 16), Block.box(0, 1, 9, 7, 7, 10), Block.box(0, 1, 15, 7, 7, 16), Block.box(6, 1, 9, 7, 7, 16), Block.box(10, 0, 11, 15, 3, 14), Block.box(12, 3, 12, 13, 10, 13));

    private static final Component NAME = Component.translatable("gui.vampirism.hunter_weapon_table");

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
        if (FluidUtil.getFluidHandler(heldItem).stream().flatMap(handler -> IntStream.range(0, handler.getTanks()).mapToObj(handler::getFluidInTank)).anyMatch(fluid -> fluid.is(Fluids.LAVA))) {
            if (level instanceof ServerLevel) {
                if (!Helper.isHunter(player)) {
                    player.displayClientMessage(Component.translatable("text.vampirism.unfamiliar"), true);
                    return InteractionResult.CONSUME;
                }

                int fluid = level.getBlockState(pos).getValue(LAVA);
                boolean flag = false;
                if (fluid < MAX_LAVA) {
                    Optional<IFluidHandlerItem> opt = FluidUtil.getFluidHandler(heldItem);
                    flag = opt.map(fluidHandler -> {
                        FluidStack missing = new FluidStack(Fluids.LAVA, (MAX_LAVA - fluid) * MB_PER_META);
                        FluidStack drainable = fluidHandler.drain(missing, IFluidHandler.FluidAction.SIMULATE);
                        if (drainable.isEmpty()) { // Buckets can only provide {@link Fluid.BUCKET_VOLUME} at a time, so try this too. Additional lava is wasted though
                            missing.setAmount(FluidType.BUCKET_VOLUME);
                            drainable = fluidHandler.drain(missing, IFluidHandler.FluidAction.SIMULATE);
                        }
                        if (drainable.getAmount() >= MB_PER_META) {
                            FluidStack drained = fluidHandler.drain(missing, IFluidHandler.FluidAction.EXECUTE);
                            if (drained.getAmount() > 0) {
                                level.setBlockAndUpdate(pos, state.setValue(LAVA, Math.min(MAX_LAVA, fluid + drained.getAmount() / MB_PER_META)));
                                player.setItemInHand(hand, fluidHandler.getContainer());

                                return true;
                            }
                        }

                        return false;
                    }).orElse(false);
                }

                if (flag) {
                    return InteractionResult.SUCCESS_SERVER;
                }
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (canUse(player)) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> new WeaponTableMenu(id, playerInventory, ContainerLevelAccess.create(playerIn.level(), pos)), NAME), pos);
            } else {
                player.displayClientMessage(Component.translatable("text.vampirism.not_learned"), true);
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
}
