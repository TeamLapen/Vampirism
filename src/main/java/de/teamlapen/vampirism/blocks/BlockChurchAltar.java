package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Placed in some churches
 */
public class BlockChurchAltar extends VampirismBlock {

    private final static String regName = "church_altar";

    public BlockChurchAltar() {
        super(regName, Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setHasFacing();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(playerIn);
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
            playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_REVERT_BACK, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
            return true;
        } else if (!ItemStackUtil.isEmpty(heldItem)) {
            if (ModItems.holy_salt_water.equals(heldItem.getItem())) {
                if (worldIn.isRemote) return true;
                boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && ((IHunterPlayer) handler.getCurrentFactionPlayer()).getSkillHandler().isSkillEnabled(HunterSkills.holy_water_enhanced);
                ItemStack newStack = new ItemStack(ModItems.holy_water_bottle, ItemStackUtil.getCount(heldItem));
                ModItems.holy_water_bottle.setTier(newStack, enhanced ? IItemWithTier.TIER.ENHANCED : IItemWithTier.TIER.NORMAL);
                playerIn.setHeldItem(hand, newStack);
                return true;
            } else if (ModItems.pure_salt.equals(heldItem.getItem())) {
                if (worldIn.isRemote) return true;
                playerIn.setHeldItem(hand, new ItemStack(ModItems.holy_salt, ItemStackUtil.getCount(heldItem)));
            }
        }
        return false;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
