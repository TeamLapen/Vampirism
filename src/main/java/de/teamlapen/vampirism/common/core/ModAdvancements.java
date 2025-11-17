package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.advancements.critereon.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAdvancements {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, FactionCriterionTrigger> TRIGGER_FACTION = TRIGGERS.register("faction", FactionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, VampireActionCriterionTrigger> TRIGGER_VAMPIRE_ACTION = TRIGGERS.register("vampire_action", VampireActionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, HunterActionCriterionTrigger> TRIGGER_HUNTER_ACTION = TRIGGERS.register("hunter_action", HunterActionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SkillUnlockedCriterionTrigger> TRIGGER_SKILL_UNLOCKED = TRIGGERS.register("skill_unlocked", SkillUnlockedCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, MinionTaskCriterionTrigger> TRIGGER_MINION_ACTION = TRIGGERS.register("minion_action", MinionTaskCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, CuredVampireVillagerCriterionTrigger> TRIGGER_CURED_VAMPIRE_VILLAGER = TRIGGERS.register("cured_vampire_villager", CuredVampireVillagerCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, MapFoundCriterionTrigger> TRIGGER_MAP_FOUND = TRIGGERS.register("map_found", MapFoundCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> TRIGGER_MOTHER_WIN = TRIGGERS.register("mother_win", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ActionCriterionTrigger> ACTION_TRIGGER = TRIGGERS.register("action", ActionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> TRIGGER_BLOOD_FOOD_CONSUMED = TRIGGERS.register("blood_food_consumed", PlayerTrigger::new);

    static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }
}
