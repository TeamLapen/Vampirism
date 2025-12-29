package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.api.world.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.common.advancements.critereon.HunterActionCriterionTrigger;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

/**
 * Does almost no damage, but can one hit kill vampire from behind when used by skilled hunters
 */
public class StakeItem extends VampirismSwordItem {

    public StakeItem(Item.Properties properties) {
        super(ToolMaterial.WOOD, 1, -1, properties.component(ModDataComponents.DROP_VAMPIRE_SOUL, Unit.INSTANCE));
    }

    public static boolean canKillInstantly(LivingEntity target, LivingEntity attacker) {
        boolean instaKillLowHealth = false;
        if (attacker instanceof Player player && attacker.isAlive()) {
            instaKillLowHealth = FactionPlayerHandler.get(player).getCurrentSkillPlayer().filter(ac -> IFaction.is(ModFactions.HUNTER, ac.getFaction())).map(s -> s.getSkillHandler().isSkillEnabled(HunterSkills.STAKE1)).orElse(false);
        } else if (attacker instanceof IAdvancedHunter) {
            instaKillLowHealth = true;// make more out of this
        }
        if (instaKillLowHealth && target.getHealth() <= (ModConfig.balance().hsInstantKill1MaxHealth.get() * target.getMaxHealth())) {
            return !ModConfig.balance().hsInstantKill1FromBehind.get() || !UtilLib.canReallySee(target, attacker, true);

        }
        return false;
    }


    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level() instanceof ServerLevel level) {
            if (target instanceof IVampireMob || (target instanceof Player && Helper.isVampire(((Player) target)))) {
                if (canKillInstantly(target, attacker)) {
                    DamageHandler.hurtModded(level, target, sources -> sources.stake(attacker), 10000F);
                    if (attacker instanceof ServerPlayer player) {
                        player.awardStat(ModStats.KILLED_WITH_STAKE.get());
                        ModAdvancements.TRIGGER_HUNTER_ACTION.get().trigger(player, HunterActionCriterionTrigger.Action.STAKE);

                    }
                    target.level().playSound(null, target.getX(), target.getY() + 0.5 * target.getEyeHeight(), target.getZ(), ModSounds.STAKE.get(), SoundSource.PLAYERS, 1.5f, 0.7f);
                }

            }
        }
    }
}
