package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.actions.IActionHandlerEntity;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.world.ModDamageSources;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

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
    public static void affectVampireGarlicSplash(@NotNull IVampire vampire, @NotNull EnumStrength strength, double distSq, boolean directHit) {
        if (vampire.doesResistGarlic(strength)) return;
        if (distSq < 16.0D) {
            double affect = 1.0D - Math.sqrt(distSq) / 4.0D;

            if (directHit) {
                affect = 1.0D;
            }

            affectVampireGarlic(vampire, strength, (float) (10 * affect), false);
        }
    }

    public static void affectVampireGarlicDirect(@NotNull IVampire vampire, @NotNull EnumStrength strength) {
        affectVampireGarlic(vampire, strength, 20, false);
    }

    private static void affectVampireGarlic(@NotNull IVampire vampire, @NotNull EnumStrength strength, float multiplier, boolean ambient) {
        if (strength == EnumStrength.NONE) return;
        LivingEntity entity = vampire.getRepresentingEntity();
        entity.addEffect(new MobEffectInstance(ModEffects.GARLIC.get(), (int) (multiplier * 20), strength.getStrength() - 1, ambient, true));
        if (entity instanceof Player && ((Player) entity).getAbilities().instabuild) return;
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int) (multiplier * 20), 1, ambient, false));
        if (strength == EnumStrength.MEDIUM || strength == EnumStrength.STRONG) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (multiplier * 20), 1, ambient, false));
            if (strength == EnumStrength.STRONG) {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) (multiplier / 2 * 20), 0, ambient, false));
            }
        }
        if (vampire instanceof IVampirePlayer) {
            IActionHandler<IVampirePlayer> actionHandler = ((IVampirePlayer) vampire).getActionHandler();
            actionHandler.deactivateAction(VampireActions.DISGUISE_VAMPIRE.get());
        }
    }

    /**
     * @param vampire  The affected vampire
     * @param strength The strength of the ambient garlic
     * @param ticks    A tick related value like ticksExisted
     */
    public static void affectVampireGarlicAmbient(@NotNull IVampire vampire, @NotNull EnumStrength strength, int ticks) {
        if (ticks % 37 == 7) {
            affectVampireGarlic(vampire, strength, 5, true);
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
    public static void affectEntityHolyWaterSplash(@NotNull LivingEntity entity, @NotNull EnumStrength strength, double distSq, boolean directHit) {
        affectEntityHolyWaterSplash(entity, strength, distSq, directHit, null);
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
     * @param source    The throwing entity
     */
    public static void affectEntityHolyWaterSplash(@NotNull LivingEntity entity, @NotNull EnumStrength strength, double distSq, boolean directHit, @Nullable LivingEntity source) {
        if (!entity.isAlive()) return;
        boolean vampire = Helper.isVampire(entity);
        if (entity.isAffectedByPotions() && (vampire || MobType.UNDEAD.equals(entity.getMobType()))) {
            if (distSq < 16.0D) {
                double affect = 1.0D - Math.sqrt(distSq) / 4.0D;

                if (directHit) {
                    affect = 1.0D;
                }
                if (!vampire) {
                    affect *= 0.5D;
                }


                double amount = (affect * (VampirismConfig.BALANCE.holyWaterSplashDamage.get() * (strength == EnumStrength.WEAK ? 1 : strength == EnumStrength.MEDIUM ? VampirismConfig.BALANCE.holyWaterTierDamageInc.get() : (VampirismConfig.BALANCE.holyWaterTierDamageInc.get() * VampirismConfig.BALANCE.holyWaterTierDamageInc.get()))) + 0.5D);
                if (entity instanceof Player) {
                    int l = VampirismPlayerAttributes.get((Player) entity).vampireLevel;
                    amount = scaleDamageWithLevel(l, REFERENCE.HIGHEST_VAMPIRE_LEVEL, amount * 0.8, amount * 1.3);
                } else if (entity instanceof VampireBaronEntity) {
                    int l = ((VampireBaronEntity) entity).getEntityLevel();
                    amount = scaleDamageWithLevel(l, VampireBaronEntity.MAX_LEVEL, amount * 0.8, amount * 2);
                }
                hurtModded(entity, ModDamageSources::holyWater, (float) amount);
            }
        }
        if (vampire && entity instanceof Player) {
            VampirePlayer.getOpt((Player) entity).map(VampirePlayer::getActionHandler).ifPresent(actionHandler -> {
                actionHandler.deactivateAction(VampireActions.DISGUISE_VAMPIRE.get());
                actionHandler.deactivateAction(VampireActions.VAMPIRE_INVISIBILITY.get());
            });
        } else if (vampire && entity instanceof IEntityActionUser) {
            IActionHandlerEntity h = ((IEntityActionUser) entity).getActionHandler();
            if (h.isActionActive(EntityActions.ENTITY_INVISIBLE.get())) {
                h.deactivateAction();
            }
        }
        if (vampire) {
            if (strength.isStrongerThan(EnumStrength.WEAK)) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, VampirismConfig.BALANCE.holyWaterNauseaDuration.get(), 2));
            }
            if (strength.isStrongerThan(EnumStrength.MEDIUM)) {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, VampirismConfig.BALANCE.holyWaterBlindnessDuration.get(), 1));
            }
        }
    }

    /**
     * Scales a damage value depending on the players vampire level.
     * Scales linear between min and max
     *
     * @param level The players level
     */
    public static double scaleDamageWithLevel(int level, int maxLevel, double minDamage, double maxDamage) {
        return minDamage + level / (double) maxLevel * (maxDamage - minDamage);
    }

    public static @NotNull Optional<DamageSource> getDamageSource(@NotNull Level world, @NotNull Function<ModDamageSources, DamageSource> sourceFunc) {
        return VampirismWorld.getOpt(world).map(VampirismWorld::damageSources).map(sourceFunc);
    }

    public static boolean hurtModded(@NotNull Entity entity, @NotNull Function<ModDamageSources, DamageSource> sourceFunc, float amount) {
        return getDamageSource(entity.level, sourceFunc).map(source -> entity.hurt(source, amount)).orElse(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean hurtVanilla(@NotNull Entity entity, @NotNull Function<DamageSources, DamageSource> sourceFunc, float amount) {
        DamageSource source = sourceFunc.apply(entity.level.damageSources());
        return entity.hurt(source, amount);
    }

    public static boolean kill(@NotNull Entity entity, int damage) {
        return hurtVanilla(entity, DamageSources::generic, damage);
    }
}
