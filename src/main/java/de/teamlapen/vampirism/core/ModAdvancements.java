package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.advancements.HunterActionTrigger;
import de.teamlapen.vampirism.advancements.TriggerFaction;
import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

/**
 * Actual advancements are loaded from assets (JSON)
 * Criteria are registered here
 */
public class ModAdvancements {

    public static final TriggerFaction TRIGGER_FACTION = register(new TriggerFaction());
    public static final VampireActionTrigger TRIGGER_VAMPIRE_ACTION = register(new VampireActionTrigger());
    public static final HunterActionTrigger TRIGGER_HUNTER_ACTION = register(new HunterActionTrigger());


    private static <T extends ICriterionTrigger> T register(T trigger) {
        return CriteriaTriggers.register(trigger);
    }

    /**
     * Does nothing itself, but static initializer perform registration
     */
    public static void registerAdvancements() {

    }
}
