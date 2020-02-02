package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismBlock {
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final static String name = "med_chair";


    public MedChairBlock() {
        super(name, Properties.create(Material.IRON).hardnessAndResistance(1));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(PART, EnumPart.TOP));

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem().equals(ModItems.injection_garlic)) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player);
            IPlayableFaction faction = handler.getCurrentFaction();
            if (handler.canJoin(faction)) {
                if (world.isRemote) {
                    VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
                } else {
                    handler.joinFaction(VReference.HUNTER_FACTION);
                    player.addPotionEffect(new EffectInstance(ModEffects.poison, 200, 1));
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.inventory.deleteStack(stack);
                }
            } else if (faction != null) {
                if (!world.isRemote) {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.med_chair_other_faction", faction.getName()));
                }

            }
        } else {
            if (world.isRemote)
                player.sendMessage(new TranslationTextComponent("text.vampirism.need_item_to_use", new TranslationTextComponent((new ItemStack(ModItems.injection_garlic).getTranslationKey()))));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (state.getBlock() != newState.getBlock()) {
            Direction dir = state.get(FACING);
            BlockPos other;
            if (state.get(PART) == EnumPart.TOP) {
                other = pos.offset(dir);
                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.item_med_chair, 1)));
            } else {
                other = pos.offset(dir.getOpposite());
            }
            world.removeBlock(other, isMoving);
        }
    }

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

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public String getName() {
            return name;
        }


    }
}
