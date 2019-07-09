package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


public class BloodPotionTableBlock extends VampirismBlock {

    private final static String regName = "blood_potion_table";


    public BloodPotionTableBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(1f));
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }


    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            if (canUse(player)) {
                //player.openGui(VampirismMod.instance, ModGuiHandler.ID_BLOOD_POTION_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ()); 1.14
            }
            else {
                player.sendMessage(new TranslationTextComponent("tile.vampirism." + regName + ".cannot_use"));
            }
        }

        return true;
    }


    private boolean canUse(PlayerEntity player) {
        IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction != null && faction.equals(VReference.HUNTER_FACTION)) {
            return faction.getPlayerCapability(player).getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_table);
        }
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
