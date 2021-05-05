package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismBlock {
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final static String name = "med_chair";
    private final VoxelShape SHAPE_TOP;
    private final VoxelShape SHAPE_BOTTOM;


    public MedChairBlock() {
        super(name, Properties.create(Material.IRON).hardnessAndResistance(1).notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(PART, EnumPart.TOP));
        SHAPE_TOP = makeCuboidShape(2, 6, 0, 14, 16, 16);
        SHAPE_BOTTOM = makeCuboidShape(1, 1, 0, 15, 10, 16);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }


    @Nonnull
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(ModItems.item_med_chair);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return state.get(PART) == EnumPart.BOTTOM ? SHAPE_BOTTOM : SHAPE_TOP;
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (player.isAlive()) {
            ItemStack stack = player.getHeldItem(hand);
            if (handleInjections(player, world, stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.inventory.deleteStack(stack);
                }
            }
        } else if (world.isRemote) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.need_item_to_use", new TranslationTextComponent((new ItemStack(ModItems.injection_garlic).getTranslationKey()))), true);
        }
        return ActionResultType.SUCCESS;
    }

    private boolean handleInjections(PlayerEntity player, World world, ItemStack stack) {
        IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
        IPlayableFaction<?> faction = handler.getCurrentFaction();
        if (stack.getItem().equals(ModItems.injection_garlic)) {
            return handleGarlicInjection(player, world, handler, faction);
        }
        if (stack.getItem().equals(ModItems.injection_sanguinare)) {
            return handleSanguinareInjection(player, handler, faction);
        }
        if (stack.getItem().equals(ModItems.injection_zombie_blood)) {
            return handleZombieBloodInjection(player);
        }
        return false;
    }

    private boolean handleGarlicInjection(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction){
        if (handler.canJoin(VReference.HUNTER_FACTION)) {
            if (world.isRemote) {
                VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(VReference.HUNTER_FACTION);
                player.addPotionEffect(new EffectInstance(ModEffects.poison, 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (!world.isRemote) {
                player.sendMessage(new TranslationTextComponent("text.vampirism.med_chair_other_faction", currentFaction.getName()), Util.DUMMY_UUID);
            }
        }
        return false;
    }

    private boolean handleSanguinareInjection(@Nonnull PlayerEntity player, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (VReference.VAMPIRE_FACTION.equals(currentFaction)) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.already_vampire"), false);
            return false;
        }
        if (VReference.HUNTER_FACTION.equals(currentFaction)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return true;
        }
        if (currentFaction == null) {
            if (handler.canJoin(VReference.VAMPIRE_FACTION)) {
                if (VampirismConfig.SERVER.disableFangInfection.get()) {
                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    PotionSanguinare.addRandom(player, true);
                    player.addPotionEffect(new EffectInstance(ModEffects.poison, 60));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleZombieBloodInjection(@Nonnull PlayerEntity player) {
        player.addPotionEffect(new EffectInstance(ModEffects.poison, 200));
        return true;
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, @Nonnull PlayerEntity player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable TileEntity te, @Nonnull ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(@Nonnull World worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        EnumPart part = state.get(PART);
        BlockPos other;
        Direction dir = state.get(FACING);
        if (state.get(PART) == EnumPart.TOP) {
            other = pos.offset(dir);
        } else {
            other = pos.offset(dir.getOpposite());
        }
        BlockState otherState = worldIn.getBlockState(other);
        if (otherState.getBlock() == this && otherState.get(PART) != part) {
            worldIn.setBlockState(other, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, other, Block.getStateId(otherState));
            if (!worldIn.isRemote && !player.isCreative()) {
                ItemStack itemstack = player.getHeldItemMainhand();
                spawnDrops(state, worldIn, pos, null, player, itemstack);
                spawnDrops(otherState, worldIn, other, null, player, itemstack);
            }
            player.addStat(Stats.BLOCK_MINED.get(this));
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Nonnull
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
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
        public String getString() {
            return name;
        }

        @Override
        public String toString() {
            return getString();
        }


    }
}
