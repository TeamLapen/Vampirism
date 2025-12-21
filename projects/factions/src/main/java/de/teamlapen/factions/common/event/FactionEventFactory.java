package de.teamlapen.factions.common.event;

import de.teamlapen.factions.api.event.ActionEvent;
import de.teamlapen.factions.api.event.FactionVillageEvent;
import de.teamlapen.factions.api.event.PlayerFactionEvent;
import de.teamlapen.factions.api.event.SkillEvents;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.factions.actions.ILastingAction;
import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.factions.api.factions.skills.ISkillHandler;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.world.ITotem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FactionEventFactory {

    public static boolean fireVillagerCaptureEventPre(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        FactionVillageEvent.VillagerCaptureFinish.Pre event = new FactionVillageEvent.VillagerCaptureFinish.Pre(totem, villagerIn, forced);
        NeoForge.EVENT_BUS.post(event);
        return event.isEntityConversionDisabled();
    }

    public static @NotNull Villager fireSpawnNewVillagerEvent(@NotNull ITotem totem, @Nullable LivingEntity oldEntity, @NotNull Villager newEntity, boolean replaceOld) {
        FactionVillageEvent.SpawnNewVillager event = new FactionVillageEvent.SpawnNewVillager(totem, oldEntity, newEntity, replaceOld);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewVillager();
    }

    public static void fireMakeAggressive(@NotNull ITotem totem, @NotNull Villager entity) {
        NeoForge.EVENT_BUS.post(new FactionVillageEvent.MakeAggressive(totem, entity));
    }

    @Nullable
    public static LivingEntity fireCreateCaptureEntityEvent(@NotNull ITotem totem, Holder<? extends IFaction<?>> faction) {
        FactionVillageEvent.SpawnCaptureEntityEvent event = new FactionVillageEvent.SpawnCaptureEntityEvent(totem, faction);
        NeoForge.EVENT_BUS.post(event);
        return event.getEntity();
    }

    public static @NotNull Pair<Float, Float> fireDefineRaidStrengthEvent(@NotNull ITotem totem, int badOmenLevel, float defendStrength, float attackStrength) {
        FactionVillageEvent.DefineRaidStrength event = new FactionVillageEvent.DefineRaidStrength(totem, badOmenLevel, defendStrength, attackStrength);
        NeoForge.EVENT_BUS.post(event);
        return Pair.of(event.getDefendStrength(), event.getAttackStrength());
    }

    public static void fireVillageAreaChangedEvent(@NotNull ITotem totem, @Nullable AABB area) {
        NeoForge.EVENT_BUS.post(new FactionVillageEvent.AreaChangedEvent(totem, area));
    }

    public static void fireVillageTotemRemovedEvent(@NotNull ITotem totem) {
        NeoForge.EVENT_BUS.post(new FactionVillageEvent.RemovedEvent(totem));
    }

    public static void fireVillageCaptureBreakEvent(@NotNull ITotem totem) {
        NeoForge.EVENT_BUS.post(new FactionVillageEvent.BreakCaptureEvent(totem));
    }

    public static Map<LivingEntity, FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent.Action> fireReplaceEntitiesOnCaptureEvent(@NotNull ITotem totem, boolean forced) {
        return NeoForge.EVENT_BUS.post(new FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent(totem, forced)).getEntitiesScheduledForReplacement();
    }

    public static PlayerFactionEvent.CanJoinFaction.Behavior fireCanJoinFactionEvent(@NotNull IFactionPlayerHandler playerHandler, Holder<? extends IPlayableFaction<?>> currentFaction, Holder<? extends IPlayableFaction<?>> newFaction) {
        PlayerFactionEvent.CanJoinFaction event = new PlayerFactionEvent.CanJoinFaction(playerHandler, currentFaction, newFaction);
        NeoForge.EVENT_BUS.post(event);
        return event.getBehavior();
    }

    public static boolean fireChangeLevelOrFactionEvent(@NotNull IFactionPlayerHandler player, Holder<? extends IPlayableFaction<?>> currentFaction, int currentLevel, Holder<? extends IPlayableFaction<?>> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChangePre event = new PlayerFactionEvent.FactionLevelChangePre(player, currentFaction, currentLevel, newFaction, newLevel);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    public static void fireFactionLevelChangedEvent(@NotNull IFactionPlayerHandler player, Holder<? extends IPlayableFaction<?>> oldFaction, int oldLevel, Holder<? extends IPlayableFaction<?>> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChanged event = new PlayerFactionEvent.FactionLevelChanged(player, oldFaction, oldLevel, newFaction, newLevel);
        NeoForge.EVENT_BUS.post(event);
    }

    public static void fireLevelChangedEvent(@NotNull IFactionPlayerHandler player, de.teamlapen.factions.api.factions.LevelingChange change) {
        PlayerFactionEvent.LevelChanged event = new PlayerFactionEvent.LevelChanged(player, change);
        NeoForge.EVENT_BUS.post(event);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends ISkillPlayer<T>> ActionEvent.ActionActivatedEvent<T> fireActionActivatedEvent(@NotNull T factionPlayer, @NotNull Holder<? extends IAction<T>> action, int cooldown, int duration) {
        ActionEvent.ActionActivatedEvent<T> event = new ActionEvent.ActionActivatedEvent<>(factionPlayer, (Holder<IAction<T>>) action, cooldown, duration);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ISkillPlayer<T>> ActionEvent.ActionDeactivatedEvent<T> fireActionDeactivatedEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ILastingAction<T>> action, int remainingDuration, int cooldown, boolean ignoreCooldown, boolean fullCooldown) {
        ActionEvent.ActionDeactivatedEvent<T> event = new ActionEvent.ActionDeactivatedEvent<>(factionPlayer, (Holder<ILastingAction<T>>) action, remainingDuration, cooldown, ignoreCooldown, fullCooldown);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ISkillPlayer<T>> ActionEvent.ActionUpdateEvent<T> fireActionUpdateEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ILastingAction<T>> action, int remainingDuration) {
        ActionEvent.ActionUpdateEvent<T> event = new ActionEvent.ActionUpdateEvent<>(factionPlayer, (Holder<ILastingAction<T>>) action, remainingDuration);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static <T extends ISkillPlayer<T>> ISkillHandler.Result fireSkillUnlockCheckEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ISkill<?>> skill) {
        var event = new SkillEvents.SkillUnlockCheckEvent<>(factionPlayer, skill);
        NeoForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static <T extends ISkillPlayer<T>> void fireSkillDisabledEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ISkill<?>> skill) {
        var event = new SkillEvents.SkillDisableEvent<>(factionPlayer, skill);
        NeoForge.EVENT_BUS.post(event);
    }

    public static <T extends ISkillPlayer<T>> void fireSkillEnableEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ISkill<?>> skill, Holder<ISkillTree> skillTree, boolean fromLoading) {
        var event = new SkillEvents.SkillEnableEvent<>(factionPlayer, skill, skillTree, fromLoading);
        NeoForge.EVENT_BUS.post(event);
    }
}
