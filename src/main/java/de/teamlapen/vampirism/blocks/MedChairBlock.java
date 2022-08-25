package de.teamlapen.vampirism.blocks;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
    private final VoxelShape SHAPE_TOP;
    private final VoxelShape SHAPE_BOTTOM;


    public MedChairBlock() {
        super(Properties.of(Material.METAL).strength(1).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, EnumPart.BOTTOM));
        SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
        SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(worldIn, pos, state, entity, itemStack);
        if (!worldIn.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite());
            worldIn.setBlock(blockpos, state.setValue(PART, EnumPart.TOP).setValue(FACING, state.getValue(HORIZONTAL_FACING)), 3);
            worldIn.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(worldIn, pos, 3);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return state.getValue(PART) == EnumPart.BOTTOM ? SHAPE_BOTTOM : SHAPE_TOP;
    }

    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (p_196271_2_ == getDirectionToOther(p_196271_1_.getValue(PART), p_196271_1_.getValue(FACING))) {
            return p_196271_3_.is(this) && p_196271_3_.getValue(PART) != p_196271_1_.getValue(PART) ? p_196271_1_ : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
        }
    }

    private static Direction getDirectionToOther(EnumPart type, Direction facing) {
        return type == EnumPart.TOP ? facing : facing.getOpposite();
    }

    @Override
    public void playerWillDestroy(@Nonnull World worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        //If in creative mode, also destroy the top block. Otherwise, it will be destroyed due to updateShape and an item will drop
        if (!worldIn.isClientSide && player.isCreative()) {
            EnumPart part = state.getValue(PART);
            if(part == EnumPart.BOTTOM){
                BlockPos other = pos.relative(getDirectionToOther(state.getValue(PART), state.getValue(FACING)));
                BlockState otherState = worldIn.getBlockState(other);
                if (otherState.getBlock() == this && otherState.getValue(PART) == EnumPart.TOP) {
                    worldIn.setBlock(other, Blocks.AIR.defaultBlockState(), 35);
                    worldIn.levelEvent(player, 2001, other, Block.getId(otherState));
                }
            }
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nonnull
    @Override
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

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
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

    private boolean handleZombieBloodInjection(@Nonnull PlayerEntity player) {
        player.addEffect(new EffectInstance(ModEffects.POISON.get(), 200));
        return true;
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

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return getSerializedName();
        }


    }
}
