package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class WeaponTableBlock extends VampirismBlock {
    private static final ITextComponent name = new TranslationTextComponent("container.weaponTable");
    public final static String regName = "weapon_table";
    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public static final IntegerProperty LAVA = IntegerProperty.create("lava", 0, MAX_LAVA);
    private static final VoxelShape NORTH = makeShape();
    private static final VoxelShape EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
    private static final VoxelShape SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
    private static final VoxelShape WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);

    public WeaponTableBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(3));
        this.setDefaultState(this.getStateContainer().getBaseState().with(LAVA, 0).with(FACING, Direction.NORTH));

    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
        }
        return NORTH;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            int lava = state.get(LAVA);
            boolean flag = false;
            ItemStack heldItem = player.getHeldItem(hand);
//            if (lava < MAX_LAVA) { TODO 1.14 Fluid
//                LazyOptional<IFluidHandlerItem> opt = FluidUtil.getFluidHandler(heldItem);
//                opt.ifPresent(fluidHandler -> {
//                    FluidStack missing = new FluidStack(Fluids.LAVA, (MAX_LAVA - lava) * MB_PER_META);
//                    FluidStack drainable = fluidHandler.drain(missing, false);
//                    if (drainable != null && drainable.amount >= MB_PER_META) {
//                        FluidStack drained = fluidHandler.drain(missing, true);
//                        if (drained != null) {
//                            IBlockState changed = state.with(LAVA, Math.min(MAX_LAVA, lava + drained.amount / MB_PER_META));
//                            world.setBlockState(pos, changed);
//                            player.setHeldItem(hand, fluidHandler.getContainer());
//                        }
//                    }
//                });
//                if (opt.isPresent()) {
//                    flag = true;
//                }
//            }
            if (!flag) {

                if (canUse(player)) {
                    player.openContainer(state.getContainer(world, pos));
                }
                else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.weapon_table.cannot_use"));
                }
            }
        }
        return true;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> {
            return new WeaponTableContainer(id, playerInventory, IWorldPosCallable.of(worldIn, pos));
        }, name);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }



    /**
     * @return If the given player is allowed to use this.
     */
    private boolean canUse(PlayerEntity player) {
        IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction != null && faction.equals(VReference.HUNTER_FACTION)) {
            return faction.getPlayerCapability(player).getSkillHandler().isSkillEnabled(HunterSkills.weapon_table);
        }
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LAVA, FACING);
    }


    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(3, 0, 0, 13, 2, 8);
        VoxelShape b = Block.makeCuboidShape(4, 2, 1, 12, 3, 7);
        VoxelShape c = Block.makeCuboidShape(5, 3, 2, 11, 6, 6);
        VoxelShape d = Block.makeCuboidShape(3, 6, 0, 13, 9.5, 8);

        VoxelShape e = Block.makeCuboidShape(0, 1, 9, 7, 2, 16);
        VoxelShape e1 = Block.makeCuboidShape(0, 0, 9, 2, 1, 11);
        VoxelShape e2 = Block.makeCuboidShape(5, 0, 9, 7, 1, 11);
        VoxelShape e3 = Block.makeCuboidShape(0, 0, 14, 2, 1, 16);
        VoxelShape e4 = Block.makeCuboidShape(5, 0, 14, 7, 1, 16);

        VoxelShape e5 = Block.makeCuboidShape(0, 1, 9, 1, 7, 16);
        VoxelShape e6 = Block.makeCuboidShape(0, 1, 9, 7, 7, 10);
        VoxelShape e7 = Block.makeCuboidShape(7, 1, 16, 0, 7, 15);
        VoxelShape e8 = Block.makeCuboidShape(7, 1, 16, 6, 7, 9);

        VoxelShape f = Block.makeCuboidShape(10, 0, 11, 15, 3, 14);
        VoxelShape g = Block.makeCuboidShape(12, 3, 12, 13, 10, 13);

        return VoxelShapes.or(a, b, c, d, e, e1, e2, e3, e4, e5, e6, e7, e8, f, g);
    }
}
