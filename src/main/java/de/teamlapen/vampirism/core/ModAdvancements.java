package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.advancements.critereon.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import org.jetbrains.annotations.NotNull;

/**
 * Actual advancements are loaded from assets (JSON)
 * Criteria are registered here
 */
public class ModAdvancements {

    public static final TriggerCriterionTrigger TRIGGER_FACTION = register(new TriggerCriterionTrigger());
    public static final VampireActionCriterionTrigger TRIGGER_VAMPIRE_ACTION = register(new VampireActionCriterionTrigger());
    public static final HunterActionCriterionTrigger TRIGGER_HUNTER_ACTION = register(new HunterActionCriterionTrigger());
    public static final SkillUnlockedCriterionTrigger TRIGGER_SKILL_UNLOCKED = register(new SkillUnlockedCriterionTrigger());
    public static final MinionTaskCriterionTrigger TRIGGER_MINION_ACTION = register(new MinionTaskCriterionTrigger());
    public static final CuredVampireVillagerCriterionTrigger TRIGGER_CURED_VAMPIRE_VILLAGER = register(new CuredVampireVillagerCriterionTrigger());


    private static <Z extends CriterionTriggerInstance, T extends CriterionTrigger<Z>> @NotNull T register(@NotNull T trigger) {
        return CriteriaTriggers.register(trigger);
    }

    /**
     * Does nothing itself, but static initializer perform registration
     */
    @SuppressWarnings("EmptyMethod")
    static void registerAdvancementTrigger() {

    }
}
