package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModActivities {

    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, REFERENCE.MODID);

    public static final DeferredHolder<Activity, Activity> DRACULA_PHASE_1 = create("dracula.phase1");
    public static final DeferredHolder<Activity, Activity> DRACULA_PHASE_2 = create("dracula.phase2");
    public static final DeferredHolder<Activity, Activity> DRACULA_PHASE_3 = create("dracula.phase3");

    public static final DeferredHolder<Activity, Activity> DRACULA_REGENERATION = create("dracula.regeneration");
    public static final DeferredHolder<Activity, Activity> DRACULA_FLYING_SWORD = create("dracula.flying_sword");
    public static final DeferredHolder<Activity, Activity> DRACULA_FLYING_NEEDLE = create("dracula.flying_needle");
    public static final DeferredHolder<Activity, Activity> DRACULA_SUMMON_PROTECTOR = create("dracula.summon_protector");
    public static final DeferredHolder<Activity, Activity> DRACULA_SUMMON_BATS = create("dracula.summon_bats");
    public static final DeferredHolder<Activity, Activity> DRACULA_BACKSTAB = create("dracula.backstab");
    public static final DeferredHolder<Activity, Activity> DRACULA_BLOOD_PROJECTILES = create("dracula.blood_projectiles");

    private static DeferredHolder<Activity, Activity> create(String name) {
        return ACTIVITIES.register(name, () -> new Activity(VIdentifier.modString(name)));
    }

    static void register(IEventBus bus) {
        ACTIVITIES.register(bus);
    }
}
