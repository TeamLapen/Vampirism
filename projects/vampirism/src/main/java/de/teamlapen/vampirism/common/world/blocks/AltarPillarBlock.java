package de.teamlapen.vampirism.common.world.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class AltarPillarBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<EnumPillarType> PILLAR_TYPE = EnumProperty.create("type", EnumPillarType.class);
    public static final BooleanProperty GLUED = BooleanProperty.create("glued");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public AltarPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PILLAR_TYPE, EnumPillarType.NONE).setValue(GLUED, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);

        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PILLAR_TYPE, GLUED, WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.SLIME_BALL)) {
            if (state.getValue(PILLAR_TYPE) != EnumPillarType.NONE && !isGlued(state)) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state.setValue(GLUED, true));
                }

                ParticleUtils.spawnParticlesOnBlockFaces(level, pos, ParticleTypes.ITEM_SLIME, UniformInt.of(3, 5));
                level.playLocalSound(pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F, false);

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (state.getValue(PILLAR_TYPE) == EnumPillarType.NONE && !isGlued(state)) {
            for (EnumPillarType type : EnumPillarType.values()) {
                if (type == EnumPillarType.NONE) continue;

                if (stack.getItem().equals(type.fillerBlock.asItem())) {
                    if (!level.isClientSide()) {
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }

                        level.setBlockAndUpdate(pos, state.setValue(PILLAR_TYPE, type));

                        SoundEvent sound = type.fillerBlock.getSoundType(type.fillerBlock.defaultBlockState(), level, pos, player).getPlaceSound();
                        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        EnumPillarType type = state.getValue(PILLAR_TYPE);

        if (type != EnumPillarType.NONE && !isGlued(state)) {
            if (!level.isClientSide()) {
                Block fillerBlock = type.fillerBlock;

                if (!player.getAbilities().instabuild && !player.getInventory().add(new ItemStack(fillerBlock))) {
                    player.drop(new ItemStack(fillerBlock), false);
                }

                level.setBlockAndUpdate(pos, state.setValue(PILLAR_TYPE, EnumPillarType.NONE));

                SoundEvent sound = fillerBlock.getSoundType(fillerBlock.defaultBlockState(), level, pos, player).getBreakSound();
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.AXE_WAX_OFF && state.getValue(GLUED)) {
            if (!simulate && !context.getLevel().isClientSide()) {
                ParticleUtils.spawnParticlesOnBlockFaces(context.getLevel(), context.getClickedPos(), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
            }

            return state.setValue(GLUED, false);
        }

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public static boolean isGlued(BlockState state) {
        return state.getValue(GLUED);
    }

    public enum EnumPillarType implements StringRepresentable, IExtensibleEnum {
        NONE("none", 0, Blocks.AIR),
        STONE("stone", 1, Blocks.STONE_BRICKS),
        BONE("bone", 1.5F, Blocks.BONE_BLOCK),
        IRON("iron", 2, Blocks.IRON_BLOCK),
        GOLD("gold", 3, Blocks.GOLD_BLOCK);

        public final String name;
        public final Block fillerBlock;
        private final float value;

        EnumPillarType(String name, float value, Block fillerBlock) {
            this.name = name;
            this.fillerBlock = fillerBlock;
            this.value = value;
        }

        public String getName() {
            return getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public float getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
