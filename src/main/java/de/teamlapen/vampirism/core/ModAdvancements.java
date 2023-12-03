package de.teamlapen.vampirism.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.*;
import de.teamlapen.vampirism.mixin.EntitySubPredicateTypesAccessor;
import de.teamlapen.vampirism.mixin.PlayerAdvancementsAccessor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Actual advancements are loaded from assets (JSON)
 * Criteria are registered here
 */
public class ModAdvancements {

    public static final FactionCriterionTrigger TRIGGER_FACTION = register(new FactionCriterionTrigger());
    public static final VampireActionCriterionTrigger TRIGGER_VAMPIRE_ACTION = register(new VampireActionCriterionTrigger());
    public static final HunterActionCriterionTrigger TRIGGER_HUNTER_ACTION = register(new HunterActionCriterionTrigger());
    public static final SkillUnlockedCriterionTrigger TRIGGER_SKILL_UNLOCKED = register(new SkillUnlockedCriterionTrigger());
    public static final MinionTaskCriterionTrigger TRIGGER_MINION_ACTION = register(new MinionTaskCriterionTrigger());
    public static final CuredVampireVillagerCriterionTrigger TRIGGER_CURED_VAMPIRE_VILLAGER = register(new CuredVampireVillagerCriterionTrigger());
    public static final PlayerTrigger TRIGGER_MOTHER_WIN = register(new PlayerTrigger(new ResourceLocation(REFERENCE.MODID, "mother_win")));


    private static <Z extends CriterionTriggerInstance, T extends CriterionTrigger<Z>> @NotNull T register(@NotNull T trigger) {
        return CriteriaTriggers.register(trigger);
    }

    /**
     * Does nothing itself, but static initializer perform registration
     */
    @SuppressWarnings("EmptyMethod")
    static void registerAdvancementTrigger() {

    }

    public static void revoke(PlayerTrigger trigger, ServerPlayer player) {
        PlayerAdvancements advancements = player.getAdvancements();
        ((PlayerAdvancementsAccessor) advancements).getAdvancements().entrySet().stream().filter(entry -> !entry.getValue().isDone()).forEach(advancementProgressEntry -> {
            if(advancementProgressEntry.getKey().getCriteria().values().stream().anyMatch(pair -> {
                CriterionTriggerInstance triggerInstance = pair.getTrigger();
                return triggerInstance != null && triggerInstance.getCriterion().equals(trigger.getId());
            })) {
                advancementProgressEntry.getValue().getCompletedCriteria().forEach(a -> advancements.revoke(advancementProgressEntry.getKey(), a));
            }
        });
    }

    public static void registerSubPredicatesUnsafe() {
        BiMap<String, EntitySubPredicate.Type> types = EntitySubPredicate.Types.TYPES;
        types = HashBiMap.create(types);
        types.put("vampirism:faction", FactionSubPredicate::fromJson);
        EntitySubPredicateTypesAccessor.setTypes(types);
    }
}
