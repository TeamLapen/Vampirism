package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.advancements.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;

/**
 * Actual advancements are loaded from assets (JSON)
 * Criteria are registered here
 */
public class ModAdvancements {

    public static final TriggerFaction TRIGGER_FACTION = register(new TriggerFaction());
    public static final VampireActionTrigger TRIGGER_VAMPIRE_ACTION = register(new VampireActionTrigger());
    public static final HunterActionTrigger TRIGGER_HUNTER_ACTION = register(new HunterActionTrigger());
    public static final SkillUnlockedTrigger TRIGGER_SKILL_UNLOCKED = register(new SkillUnlockedTrigger());
    public static final MinionTaskTrigger TRIGGER_MINION_ACTION = register(new MinionTaskTrigger());


    private static <Z extends ICriterionInstance, T extends ICriterionTrigger<Z>> T register(T trigger) {
        return CriteriaTriggers.register(trigger);
    }

    /**
     * Does nothing itself, but static initializer perform registration
     */
    static void registerAdvancements() {

    }
}
