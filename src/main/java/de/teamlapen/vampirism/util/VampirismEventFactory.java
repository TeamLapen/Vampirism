package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.event.ActionEvent;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Internal
public class VampirismEventFactory {

    public static boolean fireVillagerCaptureEventPre(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        VampirismVillageEvent.VillagerCaptureFinish.Pre event = new VampirismVillageEvent.VillagerCaptureFinish.Pre(totem, villagerIn, forced);
        NeoForge.EVENT_BUS.post(event);
        return event.isEntityConversionDisabled();
    }

    public static void fireVillagerCaptureEventPost(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        NeoForge.EVENT_BUS.post(new VampirismVillageEvent.VillagerCaptureFinish.Post(totem, villagerIn, forced));
    }

    public static @NotNull Villager fireSpawnNewVillagerEvent(@NotNull ITotem totem, @Nullable Mob oldEntity, @NotNull Villager newEntity, boolean replaceOld) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(totem, oldEntity, newEntity, replaceOld);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewVillager();
    }

    public static PlayerFactionEvent.CanJoinFaction.Behavior fireCanJoinFactionEvent(@NotNull IFactionPlayerHandler playerHandler, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction, Holder<? extends IPlayableFaction<?>> newFaction) {
        PlayerFactionEvent.CanJoinFaction event = new PlayerFactionEvent.CanJoinFaction(playerHandler, (Holder<IPlayableFaction<?>>) currentFaction, (Holder<IPlayableFaction<?>>) newFaction);
        NeoForge.EVENT_BUS.post(event);
        return event.getBehavior();
    }

    public static boolean fireChangeLevelOrFactionEvent(@NotNull IFactionPlayerHandler player, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction, int currentLevel, @Nullable Holder<? extends IPlayableFaction<?>> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChangePre event = new PlayerFactionEvent.FactionLevelChangePre(player, (Holder<IPlayableFaction<?>>) currentFaction, currentLevel,(Holder<IPlayableFaction<?>>) newFaction, newLevel);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    public static void fireFactionLevelChangedEvent(@NotNull IFactionPlayerHandler player, @Nullable Holder<? extends IPlayableFaction<?>> oldFaction, int oldLevel, @Nullable Holder<? extends IPlayableFaction<?>> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChanged event = new PlayerFactionEvent.FactionLevelChanged(player, (Holder<IPlayableFaction<?>>) oldFaction, oldLevel, (Holder<IPlayableFaction<?>>) newFaction, newLevel);
        NeoForge.EVENT_BUS.post(event);
    }

    public static boolean fireMakeAggressive(@NotNull ITotem totem, @NotNull Villager entity) {
        VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(totem, entity);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static @NotNull Pair<Float, Float> fireDefineRaidStrengthEvent(@NotNull ITotem totem, int badOmenLevel, float defendStrength, float attackStrength) {
        VampirismVillageEvent.DefineRaidStrength event = new VampirismVillageEvent.DefineRaidStrength(totem, badOmenLevel, defendStrength, attackStrength);
        NeoForge.EVENT_BUS.post(event);
        return Pair.of(event.getDefendStrength(), event.getAttackStrength());
    }

    public static @NotNull BloodDrinkEvent.PlayerDrinkBloodEvent fireVampirePlayerDrinkBloodEvent(@NotNull IVampirePlayer vampirePlayer, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.PlayerDrinkBloodEvent event = new BloodDrinkEvent.PlayerDrinkBloodEvent(vampirePlayer, amount, saturationAmount, useRemaining, bloodSource);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static @NotNull BloodDrinkEvent.EntityDrinkBloodEvent fireVampireDrinkBlood(@NotNull IVampire vampire, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.EntityDrinkBloodEvent event = new BloodDrinkEvent.EntityDrinkBloodEvent(vampire, amount, saturationAmount, useRemaining, bloodSource);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> ActionEvent.ActionActivatedEvent<T> fireActionActivatedEvent(@NotNull T factionPlayer, @NotNull Holder<? extends IAction<T>> action, int cooldown, int duration) {
        ActionEvent.ActionActivatedEvent<T> event = new ActionEvent.ActionActivatedEvent<>(factionPlayer, (Holder<IAction<T>>) action, cooldown, duration);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>& ISkillPlayer<T>> ActionEvent.ActionDeactivatedEvent<T> fireActionDeactivatedEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ILastingAction<T>> action, int remainingDuration, int cooldown, boolean ignoreCooldown, boolean fullCooldown) {
        ActionEvent.ActionDeactivatedEvent<T> event = new ActionEvent.ActionDeactivatedEvent<>(factionPlayer, (Holder<ILastingAction<T>>) action, remainingDuration, cooldown, ignoreCooldown, fullCooldown);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>& ISkillPlayer<T>> ActionEvent.ActionUpdateEvent<T> fireActionUpdateEvent(@NotNull T factionPlayer, @NotNull Holder<? extends ILastingAction<T>> action, int remainingDuration) {
        ActionEvent.ActionUpdateEvent<T> event = new ActionEvent.ActionUpdateEvent<>(factionPlayer, (Holder<ILastingAction<T>>) action, remainingDuration);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

}