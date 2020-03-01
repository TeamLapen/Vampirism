package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
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
public class ChurchAltarBlock extends HorizontalBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final static String regName = "church_altar";
    private static final VoxelShape SHAPEX = makeShape();
    private static final VoxelShape SHAPEZ = UtilLib.rotateShape(SHAPEX, UtilLib.RotationAmount.NINETY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 5, 15, 1, 12);
        VoxelShape b = Block.makeCuboidShape(7, 1, 7, 9, 12, 11);
        VoxelShape c = Block.makeCuboidShape(1, 9, 3, 15, 14, 13);
        VoxelShape r = VoxelShapes.or(a, b);
        return VoxelShapes.or(r, c);
    }


    public ChurchAltarBlock() {
        super(Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
        setRegistryName(REFERENCE.MODID, regName);
    }


    @Nonnull
    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        Direction dir = blockState.get(FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) return SHAPEX;
        return SHAPEZ;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean isNormalCube(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!player.isAlive()) return ActionResultType.PASS;
        IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
        ItemStack heldItem = player.getHeldItem(hand);
        if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return ActionResultType.SUCCESS;
        } else if (!heldItem.isEmpty()) {
            if (ModItems.holy_salt_water.equals(heldItem.getItem())) {
                if (world.isRemote) return ActionResultType.SUCCESS;
                boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && handler.getCurrentFactionPlayer().map(s -> s.getSkillHandler()).map(s -> s.isSkillEnabled(HunterSkills.holy_water_enhanced)).orElse(false);
                ItemStack newStack = new ItemStack(enhanced ? ModItems.holy_water_bottle_enhanced : ModItems.holy_water_bottle_normal, heldItem.getCount());
                player.setHeldItem(hand, newStack);
                return ActionResultType.SUCCESS;
            } else if (ModItems.pure_salt.equals(heldItem.getItem())) {
                if (world.isRemote) return ActionResultType.SUCCESS;
                player.setHeldItem(hand, new ItemStack(ModItems.holy_salt, heldItem.getCount()));
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
