package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.*;
import de.teamlapen.vampirism.mixin.PlayerAdvancementsAccessor;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Actual advancements are loaded from assets (JSON)
 * Criteria are registered here
 */
public class ModAdvancements {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, FactionCriterionTrigger> TRIGGER_FACTION = TRIGGERS.register("faction", FactionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, VampireActionCriterionTrigger> TRIGGER_VAMPIRE_ACTION = TRIGGERS.register("vampire_action", VampireActionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, HunterActionCriterionTrigger> TRIGGER_HUNTER_ACTION = TRIGGERS.register("hunter_action", HunterActionCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SkillUnlockedCriterionTrigger> TRIGGER_SKILL_UNLOCKED = TRIGGERS.register("skill_unlocked", SkillUnlockedCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, MinionTaskCriterionTrigger> TRIGGER_MINION_ACTION = TRIGGERS.register("minion_action", MinionTaskCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, CuredVampireVillagerCriterionTrigger> TRIGGER_CURED_VAMPIRE_VILLAGER = TRIGGERS.register("cured_vampire_villager", CuredVampireVillagerCriterionTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> TRIGGER_MOTHER_WIN = TRIGGERS.register("mother_win", PlayerTrigger::new);

    public static final EntitySubPredicate.Type FACTION = new EntitySubPredicate.Type(FactionSubPredicate.CODEC);

    static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }

    public static void revoke(PlayerTrigger trigger, ServerPlayer player) {
        PlayerAdvancements advancements = player.getAdvancements();
        ((PlayerAdvancementsAccessor) advancements).getAdvancements().entrySet().stream().filter(entry -> !entry.getValue().isDone()).forEach(advancementProgressEntry -> {
            if(advancementProgressEntry.getKey().value().criteria().values().stream().anyMatch(pair -> pair.trigger().equals(trigger))) {
                advancementProgressEntry.getValue().getCompletedCriteria().forEach(a -> advancements.revoke(advancementProgressEntry.getKey(), a));
            }
        });
    }
}
