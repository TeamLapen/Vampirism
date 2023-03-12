package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;


/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismHorizontalBlock {
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    private static final VoxelShape SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
    private static final VoxelShape SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);
    private final VoxelShape NORTH1;
    private final VoxelShape EAST1;
    private final VoxelShape SOUTH1;
    private final VoxelShape WEST1;
    private final VoxelShape NORTH2;
    private final VoxelShape EAST2;
    private final VoxelShape SOUTH2;
    private final VoxelShape WEST2;

    public MedChairBlock() {
        super(Properties.of(Material.METAL).strength(1).noOcclusion());
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

    @Override
    @Nonnull
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (player.isAlive()) {
            ItemStack stack = player.getItemInHand(hand);
            if (handleInjections(player, world, stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.inventory.removeItem(stack);
                }
            }
        } else if (world.isClientSide) {
            player.displayClientMessage(new TranslationTextComponent("text.vampirism.need_item_to_use", new TranslationTextComponent((new ItemStack(ModItems.INJECTION_GARLIC.get()).getDescriptionId()))), true);
        }
        return ActionResultType.SUCCESS;
    }

    private boolean handleGarlicInjection(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (handler.canJoin(VReference.HUNTER_FACTION)) {
            if (world.isClientSide) {
                VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(VReference.HUNTER_FACTION);
                player.addEffect(new EffectInstance(ModEffects.POISON.get(), 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (!world.isClientSide) {
                player.sendMessage(new TranslationTextComponent("text.vampirism.med_chair_other_faction", currentFaction.getName()), Util.NIL_UUID);
            }
        }
        return false;
    }

    private boolean handleInjections(PlayerEntity player, World world, ItemStack stack) {
        return FactionPlayerHandler.getOpt(player).map(handler -> {
            IPlayableFaction<?> faction = handler.getCurrentFaction();
            if (stack.getItem().equals(ModItems.INJECTION_GARLIC.get())) {
                return handleGarlicInjection(player, world, handler, faction);
            }
            if (stack.getItem().equals(ModItems.INJECTION_SANGUINARE.get())) {
                return handleSanguinareInjection(player, handler, faction);
            }
            return false;
        }).orElse(false);
    }

    private boolean handleSanguinareInjection(@Nonnull PlayerEntity player, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (VReference.VAMPIRE_FACTION.equals(currentFaction)) {
            player.displayClientMessage(new TranslationTextComponent("text.vampirism.already_vampire"), false);
            return false;
        }
        if (VReference.HUNTER_FACTION.equals(currentFaction)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return true;
        }
        if (currentFaction == null) {
            if (handler.canJoin(VReference.VAMPIRE_FACTION)) {
                if (VampirismConfig.SERVER.disableFangInfection.get()) {
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    SanguinareEffect.addRandom(player, true);
                    player.addEffect(new EffectInstance(ModEffects.POISON.get(), 60));
                    return true;
                }
            }
        }
        return false;
    }

    public enum EnumPart implements IStringSerializable {
        TOP("top", 0), BOTTOM("bottom", 1);

        public static EnumPart fromMeta(int meta) {
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

                @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return getSerializedName();
        }


    }

    public BlockRenderType getRenderShape( BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }


    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        boolean main = state.getValue(PART) == EnumPart.BOTTOM;
        switch (state.getValue(FACING)) {
            case NORTH:
                return main ? NORTH1 : NORTH2;
            case EAST:
                return main ? EAST1 : EAST2;
            case SOUTH:
                return main ? SOUTH1 : SOUTH2;
            case WEST:
                return main ? WEST1 : WEST2;
        }
        return NORTH1;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction enumfacing = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing.getOpposite()) : null;
    }

    @Override
    public boolean isPathfindable(BlockState pState, IBlockReader pLevel, BlockPos pPos, PathType pType) {
        return false;
    }

    @Override
    public BlockState mirror( BlockState state,  Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public void playerWillDestroy( World world, BlockPos blockPos,  BlockState blockState,  PlayerEntity player) {
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

        super.playerWillDestroy(world, blockPos, blockState, player);
    }

    @Override
    public  BlockState rotate( BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy( World world, BlockPos pos,  BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
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


    @Override
    public BlockState updateShape( BlockState stateIn,  Direction facing,  BlockState facingState,  IWorld worldIn,  BlockPos currentPos, BlockPos facingPos) {
        if (facing == getOtherBlockDirection(stateIn)) {
            return facingState.getBlock() == this && facingState.getValue(PART) != stateIn.getValue(PART) ? updateFromOther(stateIn, facingState) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer. Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    protected  Direction getOtherBlockDirection( BlockState blockState) {
        return blockState.getValue(PART) == EnumPart.BOTTOM ? blockState.getValue(FACING).getOpposite() : blockState.getValue(FACING);
    }

    protected BlockState updateFromOther(BlockState thisState, BlockState otherState) {
        return thisState;
    }
}
