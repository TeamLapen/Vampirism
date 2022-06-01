package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Placed in some churches
 */
public class ChurchAltarBlock extends VampirismHorizontalBlock {
    private static final VoxelShape SHAPEX = makeShape();
    private static final VoxelShape SHAPEZ = UtilLib.rotateShape(SHAPEX, UtilLib.RotationAmount.NINETY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 5, 15, 1, 12);
        VoxelShape b = Block.box(7, 1, 7, 9, 12, 11);
        VoxelShape c = Block.box(1, 9, 3, 15, 14, 13);
        VoxelShape r = VoxelShapes.or(a, b);
        return VoxelShapes.or(r, c);
    }


    public ChurchAltarBlock() {
        super(Properties.of(Material.WOOD).strength(0.5f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }


    @Nonnull
    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction dir = blockState.getValue(FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) return SHAPEX;
        return SHAPEZ;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }


    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!player.isAlive()) return ActionResultType.PASS;
        ItemStack heldItem = player.getItemInHand(hand);
        return FactionPlayerHandler.getOpt(player).map(handler -> {
            if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
                VampirismMod.proxy.displayRevertBackScreen();
                return ActionResultType.SUCCESS;
            } else if (!heldItem.isEmpty()) {
                if (ModItems.HOLY_SALT_WATER.get().equals(heldItem.getItem())) {
                    if (world.isClientSide) return ActionResultType.SUCCESS;
                    boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && handler.getCurrentFactionPlayer().map(s -> s.getSkillHandler()).map(s -> s.isSkillEnabled(HunterSkills.HOLY_WATER_ENHANCED.get())).orElse(false);
                    ItemStack newStack = new ItemStack(enhanced ? ModItems.HOLY_WATER_BOTTLE_ENHANCED.get() : ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), heldItem.getCount());
                    player.setItemInHand(hand, newStack);
                    return ActionResultType.SUCCESS;
                } else if (ModItems.PURE_SALT.get().equals(heldItem.getItem())) {
                    if (world.isClientSide) return ActionResultType.SUCCESS;
                    player.setItemInHand(hand, new ItemStack(ModItems.HOLY_SALT.get(), heldItem.getCount()));
                }
            }
            return ActionResultType.PASS;
        }).orElse(ActionResultType.PASS);
    }
}
