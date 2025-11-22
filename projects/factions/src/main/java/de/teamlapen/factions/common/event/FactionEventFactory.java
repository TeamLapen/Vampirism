package de.teamlapen.factions.common.event;

import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.ILastingAction;
import de.teamlapen.factions.api.event.ActionEvent;
import de.teamlapen.factions.api.event.FactionVillageEvent;
import de.teamlapen.factions.api.event.PlayerFactionEvent;
import de.teamlapen.factions.api.event.SkillEvents;
import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillHandler;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.api.world.ITotem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FactionEventFactory {

    public static boolean fireVillagerCaptureEventPre(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        FactionVillageEvent.VillagerCaptureFinish.Pre event = new FactionVillageEvent.VillagerCaptureFinish.Pre(totem, villagerIn, forced);
        NeoForge.EVENT_BUS.post(event);
        return event.isEntityConversionDisabled();
    }

    public static void fireVillagerCaptureEventPost(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        NeoForge.EVENT_BUS.post(new FactionVillageEvent.VillagerCaptureFinish.Post(totem, villagerIn, forced));
    }

    public static @NotNull Villager fireSpawnNewVillagerEvent(@NotNull ITotem totem, @Nullable Mob oldEntity, @NotNull Villager newEntity, boolean replaceOld) {
        FactionVillageEvent.SpawnNewVillager event = new FactionVillageEvent.SpawnNewVillager(totem, oldEntity, newEntity, replaceOld);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewVillager();
    }

    public static boolean fireMakeAggressive(@NotNull ITotem totem, @NotNull Villager entity) {
        FactionVillageEvent.MakeAggressive event = new FactionVillageEvent.MakeAggressive(totem, entity);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static @NotNull Pair<Float, Float> fireDefineRaidStrengthEvent(@NotNull ITotem totem, int badOmenLevel, float defendStrength, float attackStrength) {
        FactionVillageEvent.DefineRaidStrength event = new FactionVillageEvent.DefineRaidStrength(totem, badOmenLevel, defendStrength, attackStrength);
        NeoForge.EVENT_BUS.post(event);
        return Pair.of(event.getDefendStrength(), event.getAttackStrength());
    }

    public static PlayerFactionEvent.CanJoinFaction.Behavior fireCanJoinFactionEvent(@NotNull IFactionPlayerHandler playerHandler, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction, Holder<? extends IPlayableFaction<?>> newFaction) {
        PlayerFactionEvent.CanJoinFaction event = new PlayerFactionEvent.CanJoinFaction(playerHandler, (Holder<IPlayableFaction<?>>) currentFaction, (Holder<IPlayableFaction<?>>) newFaction);
        NeoForge.EVENT_BUS.post(event);
        return event.getBehavior();
    }

    public static boolean fireChangeLevelOrFactionEvent(@NotNull IFactionPlayerHandler player, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction, int currentLevel, @Nullable Holder<? extends IPlayableFaction<?>> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChangePre event = new PlayerFactionEvent.FactionLevelChangePre(player, (Holder<IPlayableFaction<?>>) currentFaction, currentLevel, (Holder<IPlayableFaction<?>>) newFaction, newLevel);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    public static void fireFactionLevelChangedEvent(@NotNull IFactionPlayerHandler player, @Nullable Holder<? extends IPlayableFaction<?>> oldFaction, int oldLevel, @Nullable Holder<? extends IPlayableFaction<?>> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChanged event = new PlayerFactionEvent.FactionLevelChanged(player, (Holder<IPlayableFaction<?>>) oldFaction, oldLevel, (Holder<IPlayableFaction<?>>) newFaction, newLevel);
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
