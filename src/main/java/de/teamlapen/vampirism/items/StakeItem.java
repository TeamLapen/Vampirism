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
import de.teamlapen.vampirism.util.Helper;
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

    public static boolean canKillInstant(LivingEntity target, LivingEntity attacker) {
        boolean instaKillFromBehind = false;
        boolean instaKillLowHealth = false;
        if (attacker instanceof PlayerEntity && attacker.isAlive()) {
            @Nullable IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer((PlayerEntity) attacker).orElse(null);
            if (factionPlayer != null && factionPlayer.getFaction().equals(VReference.HUNTER_FACTION)) {
                ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                if (skillHandler.isSkillEnabled(HunterSkills.STAKE2.get())) {
                    instaKillFromBehind = true;
                }
                if (skillHandler.isSkillEnabled(HunterSkills.STAKE1.get())) {
                    instaKillLowHealth = true;
                }
            }
        } else if (attacker instanceof IAdvancedHunter) {
            instaKillLowHealth = true;// make more out of this
        }
        if (instaKillFromBehind && !UtilLib.canReallySee(target, attacker, true)) {
            return !(VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get() && target instanceof PlayerEntity) && target.getMaxHealth() < VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get();
        } else if (instaKillLowHealth && target.getHealth() <= (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * target.getMaxHealth())) {
            return !VampirismConfig.BALANCE.hsInstantKill1FromBehind.get() || !UtilLib.canReallySee(target, attacker, true);

        }
        return false;
    }

    public StakeItem() {
        super(ItemTier.WOOD, 1, -1, new Properties().tab(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getCommandSenderWorld().isClientSide) {
            if (target instanceof IVampireMob || (target instanceof PlayerEntity && Helper.isVampire(((PlayerEntity) target)))) {
                if (canKillInstant(target, attacker)) {
                    DamageSource dmg = attacker instanceof PlayerEntity ? DamageSource.playerAttack((PlayerEntity) attacker) : DamageSource.mobAttack(attacker);
                    dmg = dmg.bypassArmor();
                    target.hurt(dmg, 10000F);
                    if (attacker instanceof ServerPlayerEntity) {
                        ModAdvancements.TRIGGER_HUNTER_ACTION.trigger((ServerPlayerEntity) attacker, HunterActionTrigger.Action.STAKE);
                    }
                }

            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
