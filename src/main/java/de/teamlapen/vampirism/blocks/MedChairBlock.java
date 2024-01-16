package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.RevertBackMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismHorizontalBlock {
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    private static final @NotNull VoxelShape SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
    private static final @NotNull VoxelShape SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);
    private final VoxelShape NORTH1;
    private final @NotNull VoxelShape EAST1;
    private final @NotNull VoxelShape SOUTH1;
    private final @NotNull VoxelShape WEST1;
    private final VoxelShape NORTH2;
    private final @NotNull VoxelShape EAST2;
    private final @NotNull VoxelShape SOUTH2;
    private final @NotNull VoxelShape WEST2;

    public MedChairBlock() {
        super(Properties.of().mapColor(MapColor.METAL).pushReaction(PushReaction.DESTROY).strength(1).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, EnumPart.BOTTOM));
        NORTH1 = SHAPE_BOTTOM;
        EAST1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.NINETY);
        SOUTH1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
        NORTH2 = SHAPE_TOP;
        EAST2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.NINETY);
        SOUTH2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.isAlive()) {
            ItemStack stack = player.getItemInHand(hand);
            player.awardStat(ModStats.interact_with_injection_chair.get());
            if (handleInjections(player, world, stack, pos)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().removeItem(stack);
                }
            }
        } else if (world.isClientSide) {
            player.displayClientMessage(Component.translatable("text.vampirism.need_item_to_use", Component.translatable((new ItemStack(ModItems.INJECTION_GARLIC.get()).getDescriptionId()))), true);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean handleGarlicInjection(@NotNull Player player, @NotNull Level world, @NotNull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (handler.canJoin(VReference.HUNTER_FACTION)) {
            if (world.isClientSide) {
                VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(VReference.HUNTER_FACTION);
                player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (!world.isClientSide) {
                player.sendSystemMessage(Component.translatable("text.vampirism.med_chair_other_faction", currentFaction.getName()));
            }
        }
        return false;
    }

    private boolean handleInjections(@NotNull Player player, @NotNull Level world, @NotNull ItemStack stack, @NotNull BlockPos pos) {
        return FactionPlayerHandler.getOpt(player).map(handler -> {
            IPlayableFaction<?> faction = handler.getCurrentFaction();
            if (stack.getItem().equals(ModItems.INJECTION_GARLIC.get())) {
                return handleGarlicInjection(player, world, handler, faction);
            }
            if (stack.getItem().equals(ModItems.INJECTION_SANGUINARE.get())) {
                return handleSanguinareInjection(world, pos, player, handler, faction);
            }
            return false;
        }).orElse(false);

    }

    private boolean handleSanguinareInjection(@NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (VReference.VAMPIRE_FACTION.equals(currentFaction)) {
            player.displayClientMessage(Component.translatable("text.vampirism.already_vampire"), false);
            return false;
        }
        if (VReference.HUNTER_FACTION.equals(currentFaction)) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(new MenuConstructor() {
                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                        return new RevertBackMenu(i, inventory, ContainerLevelAccess.create(level, pos));
                    }
                }, Component.empty()));
            }
            return false;
        }
        if (currentFaction == null) {
            if (handler.canJoin(VReference.VAMPIRE_FACTION)) {
                if (VampirismConfig.SERVER.disableFangInfection.get()) {
                    player.displayClientMessage(Component.translatable("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    SanguinareEffect.addRandom(player, true, true);
                    player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 60));
                    return true;
                }
            }
        }
        return false;
    }

    public enum EnumPart implements StringRepresentable {
        TOP("top", 0), BOTTOM("bottom", 1);

        public static @NotNull EnumPart fromMeta(int meta) {
            if (meta == 1) {
                return BOTTOM;
            }
            return TOP;
        }

        public final String name;
        public final int meta;

        EnumPart(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public @NotNull String toString() {
            return getSerializedName();
        }


    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }


    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        boolean main = state.getValue(PART) == EnumPart.BOTTOM;
        return switch (state.getValue(FACING)) {
            case NORTH -> main ? NORTH1 : NORTH2;
            case EAST -> main ? EAST1 : EAST2;
            case SOUTH -> main ? SOUTH1 : SOUTH2;
            case WEST -> main ? WEST1 : WEST2;
            default -> NORTH1;
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction enumfacing = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing.getOpposite()) : null;
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState playerWillDestroy(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull BlockState blockState, @NotNull Player player) {
        if (!world.isClientSide && player.isCreative()) {
            EnumPart part = blockState.getValue(PART);
            if (part == EnumPart.TOP) {
                BlockPos blockpos = blockPos.relative(getOtherBlockDirection(blockState));
                BlockState otherState = world.getBlockState(blockpos);
                if (otherState.getBlock() == this && otherState.getValue(PART) == EnumPart.BOTTOM) {
                    world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    world.levelEvent(player, 2001, blockpos, Block.getId(otherState));
                }
            }
        }

        return super.playerWillDestroy(world, blockPos, blockState, player);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide) {
            BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
            BlockState otherState = state.setValue(PART, EnumPart.TOP);
            otherState = otherState.setValue(FACING, otherState.getValue(FACING));
            world.setBlock(blockpos, otherState, 3);
            world.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, 3);
        }

    }

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (facing == getOtherBlockDirection(stateIn)) {
            return facingState.getBlock() == this && facingState.getValue(PART) != stateIn.getValue(PART) ? updateFromOther(stateIn, facingState) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    protected @NotNull Direction getOtherBlockDirection(@NotNull BlockState blockState) {
        return blockState.getValue(PART) == EnumPart.BOTTOM ? blockState.getValue(FACING).getOpposite() : blockState.getValue(FACING);
    }

    protected BlockState updateFromOther(BlockState thisState, BlockState otherState) {
        return thisState;
    }
}
