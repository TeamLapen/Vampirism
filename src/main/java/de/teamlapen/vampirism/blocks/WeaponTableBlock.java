package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.inventory.WeaponTableMenu;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeaponTableBlock extends VampirismHorizontalBlock {
    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;
    public static final IntegerProperty LAVA = IntegerProperty.create("lava", 0, MAX_LAVA);
    private static final Component name = Component.translatable("gui.vampirism.hunter_weapon_table");

    private static @NotNull VoxelShape makeShape() {
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
        VoxelShape e7 = Block.box(0, 1, 15, 7, 7, 16);
        VoxelShape e8 = Block.box(6, 1, 9, 7, 7, 16);

        VoxelShape f = Block.box(10, 0, 11, 15, 3, 14);
        VoxelShape g = Block.box(12, 3, 12, 13, 10, 13);

        return Shapes.or(a, b, c, d, e, e1, e2, e3, e4, e5, e6, e7, e8, f, g);
    }

    public WeaponTableBlock() {
        super(Properties.of(Material.METAL).strength(3).noOcclusion(), makeShape());
        this.registerDefaultState(this.getStateDefinition().any().setValue(LAVA, 0).setValue(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((id, playerInventory, playerEntity) -> new WeaponTableMenu(id, playerInventory, ContainerLevelAccess.create(worldIn, pos)), name);
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
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
                        missing.setAmount(FluidType.BUCKET_VOLUME);
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

                if (canUse(player) && player instanceof ServerPlayer) {
                    NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((id, playerInventory, playerIn) -> new WeaponTableMenu(id, playerInventory, ContainerLevelAccess.create(playerIn.level, pos)), name), pos);
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.weapon_table.cannot_use"), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(LAVA, FACING);
    }

    /**
     * @return If the given player is allowed to use this.
     */
    private boolean canUse(@NotNull Player player) {
        if (Helper.isHunter(player)) {
            return HunterPlayer.getOpt(player).map(HunterPlayer::getSkillHandler).map(handler -> handler.isSkillEnabled(HunterSkills.WEAPON_TABLE.get())).orElse(false);
        }
        return false;
    }
}
