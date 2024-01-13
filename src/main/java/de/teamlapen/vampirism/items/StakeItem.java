package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.advancements.critereon.HunterActionCriterionTrigger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Does almost no damage, but can one hit kill vampire from behind when used by skilled hunters
 */
public class StakeItem extends VampirismSwordItem implements IVampireFinisher {
    public static boolean canKillInstant(@NotNull LivingEntity target, LivingEntity attacker) {
        boolean instaKillLowHealth = false;
        if (attacker instanceof Player && attacker.isAlive()) {
            @Nullable IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer((Player) attacker).orElse(null);
            if (factionPlayer != null && factionPlayer.getFaction().equals(VReference.HUNTER_FACTION)) {
                ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                if (skillHandler.isSkillEnabled(HunterSkills.STAKE1.get())) {
                    instaKillLowHealth = true;
                }
            }
        } else if (attacker instanceof IAdvancedHunter) {
            instaKillLowHealth = true;// make more out of this
        }
        if (instaKillLowHealth && target.getHealth() <= (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * target.getMaxHealth())) {
            return !VampirismConfig.BALANCE.hsInstantKill1FromBehind.get() || !UtilLib.canReallySee(target, attacker, true);

        }
        return false;
    }

    public StakeItem() {
        super(Tiers.WOOD, 1, -1, new Properties());
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (!attacker.getCommandSenderWorld().isClientSide) {
            if (target instanceof IVampireMob || (target instanceof Player && Helper.isVampire(((Player) target)))) {
                if (canKillInstant(target, attacker)) {
                    DamageHandler.hurtModded(target, sources -> sources.stake(attacker), 10000F);
                    if (attacker instanceof ServerPlayer player) {
                        player.awardStat(ModStats.KILLED_WITH_STAKE.get());
                        ModAdvancements.TRIGGER_HUNTER_ACTION.get().trigger(player, HunterActionCriterionTrigger.Action.STAKE);

                    }
                    target.getCommandSenderWorld().playSound(null, target.getX(), target.getY()+0.5*target.getEyeHeight(), target.getZ(), ModSounds.STAKE.get(), SoundSource.PLAYERS, 1.5f, 0.7f);
                }

            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
