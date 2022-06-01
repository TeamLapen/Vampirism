package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class WeaponTableBlock extends VampirismHorizontalBlock {
    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;
    public static final IntegerProperty LAVA = IntegerProperty.create("lava", 0, MAX_LAVA);
    private static final ITextComponent name = new TranslationTextComponent("gui.vampirism.hunter_weapon_table");

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(3, 0, 0, 13, 2, 8);
        VoxelShape b = Block.box(4, 2, 1, 12, 3, 7);
        VoxelShape c = Block.box(5, 3, 2, 11, 6, 6);
        VoxelShape d = Block.box(3, 6, 0, 13, 9.5, 8);

        VoxelShape e = Block.box(0, 1, 9, 7, 2, 16);
        VoxelShape e1 = Block.box(0, 0, 9, 2, 1, 11);
        VoxelShape e2 = Block.box(5, 0, 9, 7, 1, 11);
        VoxelShape e3 = Block.box(0, 0, 14, 2, 1, 16);
        VoxelShape e4 = Block.box(5, 0, 14, 7, 1, 16);

        VoxelShape e5 = Block.box(0, 1, 9, 1, 7, 16);
        VoxelShape e6 = Block.box(0, 1, 9, 7, 7, 10);
        VoxelShape e7 = Block.box(7, 1, 16, 0, 7, 15);
        VoxelShape e8 = Block.box(7, 1, 16, 6, 7, 9);

        VoxelShape f = Block.box(10, 0, 11, 15, 3, 14);
        VoxelShape g = Block.box(12, 3, 12, 13, 10, 13);

        return VoxelShapes.or(a, b, c, d, e, e1, e2, e3, e4, e5, e6, e7, e8, f, g);
    }

    public WeaponTableBlock() {
        super(Properties.of(Material.METAL).strength(3).noOcclusion(), makeShape());
        this.registerDefaultState(this.getStateDefinition().any().setValue(LAVA, 0).setValue(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> new WeaponTableContainer(id, playerInventory, IWorldPosCallable.create(worldIn, pos)), name);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            int fluid = world.getBlockState(pos).getValue(LAVA);
            boolean flag = false;
            ItemStack heldItem = player.getItemInHand(hand);
            if (fluid < MAX_LAVA) {
                LazyOptional<IFluidHandlerItem> opt = FluidUtil.getFluidHandler(heldItem);
                flag = opt.map(fluidHandler -> {
                    FluidStack missing = new FluidStack(Fluids.LAVA, (MAX_LAVA - fluid) * MB_PER_META);
                    FluidStack drainable = fluidHandler.drain(missing, IFluidHandler.FluidAction.SIMULATE);
                    if (drainable.isEmpty()) { //Buckets can only provide {@link Fluid.BUCKET_VOLUME} at a time, so try this too. Additional lava is wasted though
                        missing.setAmount(FluidAttributes.BUCKET_VOLUME);
                        drainable = fluidHandler.drain(missing, IFluidHandler.FluidAction.SIMULATE);
                    }
                    if (drainable.getAmount() >= MB_PER_META) {
                        FluidStack drained = fluidHandler.drain(missing, IFluidHandler.FluidAction.EXECUTE);
                        if (drained.getAmount() > 0) {
                            world.setBlockAndUpdate(pos, state.setValue(LAVA, Math.min(MAX_LAVA, fluid + drained.getAmount() / MB_PER_META)));
                            player.setItemInHand(hand, fluidHandler.getContainer());
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
            }
            if (!flag) {

                if (canUse(player) && player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> new WeaponTableContainer(id, playerInventory, IWorldPosCallable.create(playerIn.level, pos)), name), pos);
                } else {
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.weapon_table.cannot_use"), true);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LAVA, FACING);
    }

    /**
     * @return If the given player is allowed to use this.
     */
    private boolean canUse(PlayerEntity player) {
        if (Helper.isHunter(player)) {
            return HunterPlayer.getOpt(player).map(HunterPlayer::getSkillHandler).map(handler -> handler.isSkillEnabled(HunterSkills.WEAPON_TABLE.get())).orElse(false);
        }
        return false;
    }
}
