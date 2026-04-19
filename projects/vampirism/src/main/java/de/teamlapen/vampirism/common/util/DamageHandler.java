package de.teamlapen.vampirism.common.util;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.tags.ModActionTags;
import de.teamlapen.vampirism.common.world.attachments.LevelDamage;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaronEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    public static void affectVampireGarlic(@NotNull IVampire vampire, @NotNull EnumStrength strength, float multiplier, boolean ambient) {
        if (strength == EnumStrength.NONE) return;
        LivingEntity entity = vampire.asEntity();
        entity.addEffect(new MobEffectInstance(ModEffects.GARLIC, (int) (multiplier * 20), strength.getStrength() - 1, ambient, true));
        if (entity instanceof Player && ((Player) entity).getAbilities().instabuild) return;
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int) (multiplier * 20), 1, ambient, false));
        if (strength == EnumStrength.MEDIUM || strength == EnumStrength.STRONG) {
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int) (multiplier * 20), 1, ambient, false));
            if (strength == EnumStrength.STRONG) {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) (multiplier / 2 * 20), 0, ambient, false));
            }
        }
        if (vampire instanceof IVampirePlayer) {
            IActionHandler<IVampirePlayer> actionHandler = ((IVampirePlayer) vampire).getActionHandler();
            actionHandler.deactivateAction(VampireActions.DISGUISE_VAMPIRE);
        }
    }

    /**
     * Affects the entity with garlic in case it is a vampire.
     */
    public static void tryAffectEntityGarlic(@NotNull LivingEntity entity, @NotNull EnumStrength strength, float multiplier, boolean ambient) {
        if (Helper.isVampire(entity)) {
            IVampire vampire = null;
            if (entity instanceof IVampire) {
                vampire = (IVampire) entity;
            } else if (entity instanceof Player player) {
                vampire = VampirePlayer.get(player);
            }
            if (vampire != null) {
                affectVampireGarlic(vampire, strength, multiplier, ambient);
            }
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
        if (!(entity.level() instanceof ServerLevel level)) return;
        if (!entity.isAlive()) return;
        boolean vampire = Helper.isVampire(entity);
        if (entity.isAffectedByPotions() && (vampire || entity.typeHolder().is(EntityTypeTags.UNDEAD))) {
            if (distSq < 16.0D) {
                double affect = 1.0D - Math.sqrt(distSq) / 4.0D;

                if (directHit) {
                    affect = 1.0D;
                }
                if (!vampire) {
                    affect *= 0.5D;
                }


                double amount = (affect * (ModConfig.balance().holyWaterSplashDamage.get() * (strength == EnumStrength.WEAK ? 1 : strength == EnumStrength.MEDIUM ? ModConfig.balance().holyWaterTierDamageInc.get() : (ModConfig.balance().holyWaterTierDamageInc.get() * ModConfig.balance().holyWaterTierDamageInc.get()))) + 0.5D);
                if (entity instanceof Player player) {
                    int l = VampirePlayer.get(player).getLevel();
                    amount = scaleDamageWithLevel(l, REFERENCE.HIGHEST_VAMPIRE_LEVEL, amount * 0.8, amount * 1.3);
                } else if (entity instanceof VampireBaronEntity) {
                    int l = ((VampireBaronEntity) entity).getEntityLevel();
                    amount = scaleDamageWithLevel(l, VampireBaronEntity.MAX_LEVEL, amount * 0.8, amount * 2);
                }
                hurtModded(level, entity, ModDamageSources::holyWater, (float) amount);
            }
        }
        if (vampire && entity instanceof Player player) {
            IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get(player).getActionHandler();
            var tag = switch (strength) {
                case WEAK -> ModActionTags.DISABLE_BY_NORMAL_HOLY_WATER;
                case MEDIUM -> ModActionTags.DISABLE_BY_ENHANCED_HOLY_WATER;
                case STRONG -> ModActionTags.DISABLE_BY_ULTIMATE_HOLY_WATER;
                default -> null;
            };
            if (tag != null) {
                actionHandler.getActiveActions().stream().filter(action -> IAction.is(action, tag)).forEach(actionHandler::deactivateAction);
            }
        }
        if (vampire) {
            if (strength.isStrongerThan(EnumStrength.WEAK)) {
                entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, ModConfig.balance().holyWaterNauseaDuration.get(), 2));
            }
            if (strength.isStrongerThan(EnumStrength.MEDIUM)) {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ModConfig.balance().holyWaterBlindnessDuration.get(), 1));
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
        return LevelDamage.getOpt(world).map(sourceFunc);
    }

    public static boolean hurtModded(ServerLevel level, @NotNull Entity entity, @NotNull Function<ModDamageSources, DamageSource> sourceFunc, float amount) {
        return getDamageSource(level, sourceFunc).map(source -> entity.hurtServer(level, source, amount)).orElse(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean hurtVanilla(ServerLevel level, @NotNull Entity entity, @NotNull Function<DamageSources, DamageSource> sourceFunc, float amount) {
        DamageSource source = sourceFunc.apply(level.damageSources());
        return entity.hurtServer(level, source, amount);
    }

    public static boolean kill(ServerLevel level, @NotNull Entity entity, int damage) {
        return hurtVanilla(level, entity, DamageSources::generic, damage);
    }

    public static boolean kill(ServerLevel level, @NotNull Entity entity, Function<DamageSources, DamageSource> sourceFunc, int damage) {
        return hurtVanilla(level, entity, sourceFunc, damage);
    }
}
