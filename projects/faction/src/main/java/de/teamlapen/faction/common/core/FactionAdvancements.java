package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.advancements.criterion.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionAdvancements {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, FactionCriterionTrigger> TRIGGER_FACTION = TRIGGERS.register("faction", FactionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, LordCriterionTrigger> TRIGGER_LORD = TRIGGERS.register("lord", LordCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SkillUnlockedCriterionTrigger> TRIGGER_SKILL_UNLOCKED = TRIGGERS.register("skill_unlocked", SkillUnlockedCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, MinionTaskCriterionTrigger> TRIGGER_MINION_ACTION = TRIGGERS.register("minion_action", MinionTaskCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ActionCriterionTrigger> ACTION_TRIGGER = TRIGGERS.register("action", ActionCriterionTrigger::new);

    static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }

}
