package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;


public class BlockBloodPotionTable extends VampirismBlock {

    private final static String regName = "blood_potion_table";


    public BlockBloodPotionTable() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(1f));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (canUse(player)) {
                //player.openGui(VampirismMod.instance, ModGuiHandler.ID_BLOOD_POTION_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ()); 1.14
            }
            else {
                player.sendMessage(new TextComponentTranslation("tile.vampirism." + regName + ".cannot_use"));
            }
        }

        return true;
    }


    private boolean canUse(EntityPlayer player) {
        IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction != null && faction.equals(VReference.HUNTER_FACTION)) {
            return faction.getPlayerCapability(player).getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_table);
        }
        return false;
    }
}
