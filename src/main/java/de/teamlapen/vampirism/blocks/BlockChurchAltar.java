package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Placed in some churches
 */
public class BlockChurchAltar extends VampirismBlock {

    private final static String regName = "church_altar";
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;


    public BlockChurchAltar() {
        super(regName, Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));

    }


    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext ctx) {
        return super.getStateForPlacement(ctx).with(FACING, ctx.getPlacementHorizontalFacing().getOpposite());
    }


    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player);
        ItemStack heldItem = player.getHeldItem(hand);
        if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
            //player.openGui(VampirismMod.instance, ModGuiHandler.ID_REVERT_BACK, world, (int) player.posX, (int) player.posY, (int) player.posZ);//TODO 1.14
            return true;
        } else if (!heldItem.isEmpty()) {
            if (ModItems.holy_salt_water.equals(heldItem.getItem())) {
                if (world.isRemote) return true;
                boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && ((IHunterPlayer) handler.getCurrentFactionPlayer()).getSkillHandler().isSkillEnabled(HunterSkills.holy_water_enhanced);
                ItemStack newStack = new ItemStack(enhanced ? ModItems.holy_water_bottle_enhanced : ModItems.holy_water_bottle_normal, heldItem.getCount());
                player.setHeldItem(hand, newStack);
                return true;
            } else if (ModItems.pure_salt.equals(heldItem.getItem())) {
                if (world.isRemote) return true;
                player.setHeldItem(hand, new ItemStack(ModItems.holy_salt, heldItem.getCount()));
            }
        }
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
