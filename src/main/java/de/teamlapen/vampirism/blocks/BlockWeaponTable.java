package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;


public class BlockWeaponTable extends VampirismBlock {
    public final static String regName = "weapon_table";
    public static final int MAX_LAVA = 5;
    public static final int MB_PER_META = 200;
    public static final IntegerProperty LAVA = IntegerProperty.create("lava", 0, MAX_LAVA);
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 0.93, 0.6, 1); //TODO 1.13 shape

    public BlockWeaponTable() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(3));
        this.setDefaultState(this.getStateContainer().getBaseState().with(LAVA, 0));

    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            int lava = state.get(LAVA);
            boolean flag = false;
            ItemStack heldItem = player.getHeldItem(hand);
            if (lava < MAX_LAVA) {
                LazyOptional<IFluidHandlerItem> opt = FluidUtil.getFluidHandler(heldItem);
                opt.ifPresent(fluidHandler -> {
                    FluidStack missing = new FluidStack(Fluids.LAVA, (MAX_LAVA - lava) * MB_PER_META); //TODO Fluid
                    FluidStack drainable = fluidHandler.drain(missing, false);
                    if (drainable != null && drainable.amount >= MB_PER_META) {
                        FluidStack drained = fluidHandler.drain(missing, true);
                        if (drained != null) {
                            IBlockState changed = state.with(LAVA, Math.min(MAX_LAVA, lava + drained.amount / MB_PER_META));
                            world.setBlockState(pos, changed);
                            player.setHeldItem(hand, fluidHandler.getContainer());
                        }
                    }
                });
                if (opt.isPresent()) {
                    flag = true;
                }




            }
            if (!flag) {

                if (canUse(player))
                    player.openGui(VampirismMod.instance, ModGuiHandler.ID_WEAPON_TABLE, world, pos.getX(), pos.getY(), pos.getZ());//TODO open Gui
                else {
                    player.sendMessage(new TextComponentTranslation("tile.vampirism." + regName + ".cannot_use"));
                }
            }
        }
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }


    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(LAVA);
    }



    /**
     * @return If the given player is allowed to use this.
     */
    private boolean canUse(EntityPlayer player) {
        IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction != null && faction.equals(VReference.HUNTER_FACTION)) {
            return faction.getPlayerCapability(player).getSkillHandler().isSkillEnabled(HunterSkills.weapon_table);
        }
        return false;
    }
}
