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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Does almost no damage, but can one hit kill vampire from behind when used by skilled hunters
 */
public class StakeItem extends VampirismItemWeapon implements IVampireFinisher, IFactionExclusiveItem {
    public static boolean canKillInstant(LivingEntity target, LivingEntity attacker) {
        boolean instaKillFromBehind = false;
        boolean instaKillLowHealth = false;
        if (attacker instanceof Player && attacker.isAlive()) {
            @Nullable IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getOpt((Player) attacker).resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer).orElse(null);
            if (factionPlayer != null && factionPlayer.getFaction().equals(VReference.HUNTER_FACTION)) {
                ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
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
            return !(VampirismConfig.BALANCE.hsInstantKill2OnlyNPC.get() && target instanceof Player) && target.getMaxHealth() < VampirismConfig.BALANCE.hsInstantKill2MaxHealth.get();
        } else if (instaKillLowHealth && target.getHealth() <= (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * target.getMaxHealth())) {
            return !VampirismConfig.BALANCE.hsInstantKill1FromBehind.get() || !UtilLib.canReallySee(target, attacker, true);

        }
        return false;
    }

    public StakeItem() {
        super(Tiers.WOOD, 1, -1, new Properties().tab(VampirismMod.creativeTab));
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, LivingEntity attacker) {
        if (!attacker.getCommandSenderWorld().isClientSide) {
            if (target instanceof IVampireMob || (target instanceof Player && Helper.isVampire(((Player) target)))) {
                if (canKillInstant(target, attacker)) {
                    DamageSource dmg = attacker instanceof Player ? DamageSource.playerAttack((Player) attacker) : DamageSource.mobAttack(attacker);
                    dmg = dmg.bypassArmor();
                    target.hurt(dmg, 10000F);
                    if (attacker instanceof ServerPlayer) {
                        ModAdvancements.TRIGGER_HUNTER_ACTION.trigger((ServerPlayer) attacker, HunterActionTrigger.Action.STAKE);
                    }
                }

            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
