package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.common.core.FactionRefinements;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.faction.common.world.refinements.Refinement;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRefinements {
    public static final DeferredRegister<IRefinement> REFINEMENTS = DeferredRegister.create(FactionRegistries.Keys.REFINEMENT, REFERENCE.MODID);

    public static final DeferredHolder<IRefinement, Refinement> HALF_INVULNERABLE = REFINEMENTS.register("half_invulnerable", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> TELEPORT_DISTANCE = REFINEMENTS.register("teleport_distance", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SWORD_FINISHER = REFINEMENTS.register("sword_finisher", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SUMMON_BATS = REFINEMENTS.register("summon_bats", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> RAGE_FURY = REFINEMENTS.register("rage_fury", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> REGENERATION = REFINEMENTS.register("regeneration", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SUN_SCREEN = REFINEMENTS.register("sun_screen", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_PENETRATION = REFINEMENTS.register("dark_blood_projectile_penetration", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_MULTI_SHOT = REFINEMENTS.register("dark_blood_projectile_multi_shot", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_AOE = REFINEMENTS.register("dark_blood_projectile_aoe", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_DAMAGE = REFINEMENTS.register("dark_blood_projectile_damage", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_SPEED = REFINEMENTS.register("dark_blood_projectile_speed", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> VISTA = REFINEMENTS.register("vista", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> FREEZE_DURATION = REFINEMENTS.register("freeze_duration", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> BLOOD_CHARGE_SPEED = REFINEMENTS.register("blood_charge_speed", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SWORD_TRAINED_AMOUNT = REFINEMENTS.register("sword_trained_amount", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> CRUCIFIX_RESISTANT = REFINEMENTS.register("crucifix_resistant", Refinement::new);


    static void register(IEventBus bus) {
        REFINEMENTS.register(bus);
    }
}
