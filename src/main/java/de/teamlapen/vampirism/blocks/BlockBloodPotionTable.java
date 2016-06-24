package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class BlockBloodPotionTable extends VampirismBlock {

    private final static String regName = "bloodPotionTable";


    public BlockBloodPotionTable() {
        super(regName, Material.IRON);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (!worldIn.isRemote) {
            if (canUse(playerIn))
                playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_BLOOD_POTION_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            else {
                playerIn.addChatComponentMessage(new TextComponentTranslation("tile.vampirism." + regName + ".cannot_use"));
            }
        }

        return true;
    }

    private boolean canUse(EntityPlayer player) {
        IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction != null && faction.equals(VReference.HUNTER_FACTION)) {
            if (faction.getPlayerCapability(player).getSkillHandler().isSkillEnabled(HunterSkills.bloodPotionTable)) {//TODO
                return true;
            }
        }
        return false;
    }
}
