package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.common.core.FactionRefinements;
import de.teamlapen.faction.common.world.refinements.Refinement;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class ModRefinements {
    public static final ResourceKey<IRefinement> HALF_INVULNERABLE = key("half_invulnerable");
    public static final ResourceKey<IRefinement> TELEPORT_DISTANCE = key("teleport_distance");
    public static final ResourceKey<IRefinement> SWORD_FINISHER = key("sword_finisher");
    public static final ResourceKey<IRefinement> SUMMON_BATS = key("summon_bats");
    public static final ResourceKey<IRefinement> RAGE_FURY = key("rage_fury");
    public static final ResourceKey<IRefinement> REGENERATION = key("regeneration");
    public static final ResourceKey<IRefinement> SUN_SCREEN = key("sun_screen");
    public static final ResourceKey<IRefinement> DARK_BLOOD_PROJECTILE_PENETRATION = key("dark_blood_projectile_penetration");
    public static final ResourceKey<IRefinement> DARK_BLOOD_PROJECTILE_MULTI_SHOT = key("dark_blood_projectile_multi_shot");
    public static final ResourceKey<IRefinement> DARK_BLOOD_PROJECTILE_AOE = key("dark_blood_projectile_aoe");
    public static final ResourceKey<IRefinement> DARK_BLOOD_PROJECTILE_DAMAGE = key("dark_blood_projectile_damage");
    public static final ResourceKey<IRefinement> DARK_BLOOD_PROJECTILE_SPEED = key("dark_blood_projectile_speed");
    public static final ResourceKey<IRefinement> VISTA = key("vista");
    public static final ResourceKey<IRefinement> FREEZE_DURATION = key("freeze_duration");
    public static final ResourceKey<IRefinement> BLOOD_CHARGE_SPEED = key("blood_charge_speed");
    public static final ResourceKey<IRefinement> SWORD_TRAINED_AMOUNT = key("sword_trained_amount");
    public static final ResourceKey<IRefinement> CRUCIFIX_RESISTANT = key("crucifix_resistant");

    static void createRefinements(BootstrapContext<IRefinement> context) {
        // faction refinements are referenced by the vampirism refinement sets, so they need to be present during datagen.
        // Only entries of the vampirism namespace are written by the datagen
        FactionRefinements.createRefinements(context);
        context.register(HALF_INVULNERABLE, Refinement.of(HALF_INVULNERABLE));
        context.register(TELEPORT_DISTANCE, Refinement.of(TELEPORT_DISTANCE));
        context.register(SWORD_FINISHER, Refinement.of(SWORD_FINISHER));
        context.register(SUMMON_BATS, Refinement.of(SUMMON_BATS));
        context.register(RAGE_FURY, Refinement.of(RAGE_FURY));
        context.register(REGENERATION, Refinement.of(REGENERATION));
        context.register(SUN_SCREEN, Refinement.of(SUN_SCREEN));
        context.register(DARK_BLOOD_PROJECTILE_PENETRATION, Refinement.of(DARK_BLOOD_PROJECTILE_PENETRATION));
        context.register(DARK_BLOOD_PROJECTILE_MULTI_SHOT, Refinement.of(DARK_BLOOD_PROJECTILE_MULTI_SHOT));
        context.register(DARK_BLOOD_PROJECTILE_AOE, Refinement.of(DARK_BLOOD_PROJECTILE_AOE));
        context.register(DARK_BLOOD_PROJECTILE_DAMAGE, Refinement.of(DARK_BLOOD_PROJECTILE_DAMAGE));
        context.register(DARK_BLOOD_PROJECTILE_SPEED, Refinement.of(DARK_BLOOD_PROJECTILE_SPEED));
        context.register(VISTA, Refinement.of(VISTA));
        context.register(FREEZE_DURATION, Refinement.of(FREEZE_DURATION));
        context.register(BLOOD_CHARGE_SPEED, Refinement.of(BLOOD_CHARGE_SPEED));
        context.register(SWORD_TRAINED_AMOUNT, Refinement.of(SWORD_TRAINED_AMOUNT));
        context.register(CRUCIFIX_RESISTANT, Refinement.of(CRUCIFIX_RESISTANT));
    }

    private static ResourceKey<IRefinement> key(String path) {
        return ResourceKey.create(FactionRegistries.Keys.REFINEMENT, VIdentifier.mod(path));
    }
}
