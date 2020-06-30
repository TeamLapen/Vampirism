package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.HunterActionTrigger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Does almost no damage, but can one hit kill vampire from behind when used by skilled hunters
 */
public class StakeItem extends VampirismItemWeapon implements IVampireFinisher, IFactionExclusiveItem {
    private final static String regName = "stake";

    public StakeItem() {
        super(regName, ItemTier.WOOD, 1, -1, new Properties().group(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }


    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getEntityWorld().isRemote) {
            if (target instanceof IVampireMob) {
                if (canKillInstant(target, attacker)) {
                    DamageSource dmg = attacker instanceof PlayerEntity ? DamageSource.causePlayerDamage((PlayerEntity) attacker) : DamageSource.causeMobDamage(attacker);
                    dmg = dmg.setDamageBypassesArmor();
                    target.attackEntityFrom(dmg, 10000F);
                    if (attacker instanceof ServerPlayerEntity) {
                        ModAdvancements.TRIGGER_HUNTER_ACTION.trigger((ServerPlayerEntity) attacker, HunterActionTrigger.Action.STAKE);
                    }
                }

            }
        }

        return super.hitEntity(stack, target, attacker);
    }

    public static boolean canKillInstant(LivingEntity target, LivingEntity attacker) {
        boolean instaKillFromBehind = false;
        boolean instaKillLowHealth = false;
        if (attacker instanceof PlayerEntity && attacker.isAlive()) {
            @Nullable IFactionPlayer factionPlayer = FactionPlayerHandler.get((PlayerEntity) attacker).getCurrentFactionPlayer().orElse(null);
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
            instaKillLowHealth = true;// make more out of this
        }
        if (instaKillFromBehind && !UtilLib.canReallySee(target, attacker, true)) {
            if (!(VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get() && target instanceof PlayerEntity) && target.getMaxHealth() < VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get()) {
                return true;
            }
        } else if (instaKillLowHealth && target.getHealth() <= (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * target.getMaxHealth())) {
            if (!VampirismConfig.BALANCE.hsInstantKill1FromBehind.get() || !UtilLib.canReallySee(target, attacker, true)) {
                return true;
            }

        }
        return false;
    }
}
