package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * Does almost no damage, but can one hit kill vampire from behind when used by skilled hunters
 */
public class ItemStake extends VampirismItemWeapon {
    private final static String regName = "stake";

    public ItemStake() {
        super(regName, ToolMaterial.WOOD, -3.5F, 0.5F);
    }


    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!attacker.getEntityWorld().isRemote) {
            if (target instanceof IVampireMob) {
                boolean instaKillFromBehind = false;
                boolean instaKillLowHealth = false;
                if (attacker instanceof EntityPlayer) {
                    IFactionPlayer factionPlayer = FactionPlayerHandler.get((EntityPlayer) attacker).getCurrentFactionPlayer();
                    if (factionPlayer != null && factionPlayer.getFaction().equals(VReference.HUNTER_FACTION)) {
                        ISkillHandler skillHandler = factionPlayer.getSkillHandler();
                        if (skillHandler.isSkillEnabled(HunterSkills.stake2)) {
                            instaKillFromBehind = true;
                        }
                        if (skillHandler.isSkillEnabled(HunterSkills.stake1)) {
                            instaKillLowHealth = true;
                        }
                    }
                } else if (attacker instanceof IAdvancedHunter) {
                    instaKillLowHealth = true;//TODO make more out of this
                }
                boolean instaKill = false;
                if (instaKillFromBehind && !UtilLib.canReallySee(target, attacker, true)) {
                    if (!(Balance.hps.INSTANT_KILL_SKILL_2_ONLY_NPC && target instanceof EntityPlayer) && target.getMaxHealth() < Balance.hps.INSTANT_KILL_SKILL_2_MAX_HEALTH) {
                        instaKill = true;
                    }
                } else if (instaKillLowHealth && target.getHealth() <= (Balance.hps.INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC * target.getMaxHealth())) {
                    if (!Balance.hps.INSTANT_KILL_SKILL_1_FROM_BEHIND || !UtilLib.canReallySee(target, attacker, true)) {
                        instaKill = true;
                    }

                }

                if (instaKill) {
                    DamageSource dmg = attacker instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer) attacker) : DamageSource.causeMobDamage(attacker);
                    target.attackEntityFrom(dmg, 10000F);
                    if (attacker instanceof EntityPlayer) {
                        //TODO ((EntityPlayer) attacker).addStat(Achievements.stake);
                    }
                }

            }
        }

        return super.hitEntity(stack, target, attacker);
    }
}
