package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * Centralizes the calculation and appliance of different sorts of damages or similar.
 * E.g. used for garlic or holy water
 */
public class DamageHandler {

    /**
     * Applies all holy water effects to the given entity
     *
     * @param vampire   The affected vampire
     * @param strength  The used strength
     * @param distSq    The squared distance from the impact point
     * @param directHit If the entity was hit directly
     */
    public static void affectVampireGarlicSplash(IVampire vampire, EnumStrength strength, double distSq, boolean directHit) {
        if (vampire.doesResistGarlic(strength)) return;
        if (distSq < 16.0D) {
            double affect = 1.0D - Math.sqrt(distSq) / 4.0D;

            if (directHit) {
                affect = 1.0D;
            }

            EntityLivingBase entity = vampire.getRepresentingEntity();
            entity.addPotionEffect(new PotionEffect(ModPotions.garlic, 5 * 20));
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) (affect * 10 * 20), 1));
            if (strength == EnumStrength.MEDIUM || strength == EnumStrength.STRONG) {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (affect * 10 * 20), 1));
                if (strength == EnumStrength.STRONG) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) (affect * 5 * 20)));
                }
            }
            if (vampire instanceof IVampirePlayer) {
                IActionHandler<IVampirePlayer> actionHandler = ((IVampirePlayer) vampire).getActionHandler();
                if (actionHandler.isActionActive(VampireActions.disguiseAction)) {
                    actionHandler.toggleAction(VampireActions.disguiseAction);
                }
            }
        }
    }

    public static void affectVampireGarlicAmbient(IVampire vampire, EnumStrength strength) {
        EntityLivingBase entity = vampire.getRepresentingEntity();
        entity.addPotionEffect(new PotionEffect(ModPotions.garlic, 5 * 20));
        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5 * 20));
        if (strength == EnumStrength.MEDIUM || strength == EnumStrength.STRONG) {
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5 * 20));
            if (strength == EnumStrength.STRONG) {
                entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 5 * 20));
            }
        }
        if (vampire instanceof IVampirePlayer) {
            IActionHandler<IVampirePlayer> actionHandler = ((IVampirePlayer) vampire).getActionHandler();
            if (actionHandler.isActionActive(VampireActions.disguiseAction)) {
                actionHandler.toggleAction(VampireActions.disguiseAction);
            }
        }
    }


    /**
     * Applies all holy water effects to the given entity.
     * Used if a holy water splash bottle affects an entity.
     * Affects vampires and undead (less).
     *
     * @param entity    The affected entity
     * @param strength  The used strength
     * @param distSq    The squared distance from the center point
     * @param directHit If the entity was hit directly
     */
    public static void affectEntityHolyWaterSplash(EntityLivingBase entity, EnumStrength strength, double distSq, boolean directHit) {
        boolean vampire = Helper.isVampire(entity);
        if (entity.canBeHitWithPotion() && (vampire || EnumCreatureAttribute.UNDEAD.equals(entity.getCreatureAttribute()))) {
            if (distSq < 16.0D) {
                double affect = 1.0D - Math.sqrt(distSq) / 4.0D;

                if (directHit) {
                    affect = 1.0D;
                }
                if (!vampire) {
                    affect *= 0.5D;
                }


                double amount = (affect * (Balance.general.HOLY_WATER_SPLASH_DAMAGE * (strength == EnumStrength.WEAK ? 1 : strength == EnumStrength.MEDIUM ? Balance.general.HOLY_WATER_TIER_DAMAGE_INC : (Balance.general.HOLY_WATER_TIER_DAMAGE_INC * Balance.general.HOLY_WATER_TIER_DAMAGE_INC))) + 0.5D);
                if (entity instanceof EntityPlayer) {
                    int l = VampirePlayer.get((EntityPlayer) entity).getLevel();
                    amount = scaleDamageWithLevel(l, amount * 0.8, amount * 1.1);
                }
                entity.attackEntityFrom(VReference.HOLY_WATER, (float) amount);
            }
        }
        if (vampire && entity instanceof EntityPlayer) {
            IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get((EntityPlayer) entity).getActionHandler();
            if (actionHandler.isActionActive(VampireActions.disguiseAction)) {
                actionHandler.toggleAction(VampireActions.disguiseAction);
            }
            if (actionHandler.isActionActive(VampireActions.invisibilityAction)) {
                actionHandler.toggleAction(VampireActions.invisibilityAction);
            }
        }
    }

    /**
     * Scales a damage value depending on the players vampire level.
     * Scales linear between min and max
     *
     * @param level     The players level
     * @param minDamage
     * @param maxDamage
     * @return
     */
    private static double scaleDamageWithLevel(int level, double minDamage, double maxDamage) {
        return minDamage + level / (double) REFERENCE.HIGHEST_VAMPIRE_LEVEL * (maxDamage - minDamage);
    }

}
