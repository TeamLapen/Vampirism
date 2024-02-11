package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public static PlayerFactionEvent.CanJoinFaction.Behavior fireCanJoinFactionEvent(@NotNull IFactionPlayerHandler playerHandler, @Nullable IPlayableFaction<?> currentFaction, IPlayableFaction<?> newFaction) {
        PlayerFactionEvent.CanJoinFaction event = new PlayerFactionEvent.CanJoinFaction(playerHandler, currentFaction, newFaction);
        NeoForge.EVENT_BUS.post(event);
        return event.getBehavior();
    }

    public static boolean fireChangeLevelOrFactionEvent(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> currentFaction, int currentLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChangePre event = new PlayerFactionEvent.FactionLevelChangePre(player, currentFaction, currentLevel, newFaction, newLevel);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    public static void fireFactionLevelChangedEvent(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> oldFaction, int oldLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChanged event = new PlayerFactionEvent.FactionLevelChanged(player, oldFaction, oldLevel, newFaction, newLevel);
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
    public static @NotNull ActionEvent.ActionActivatedEvent fireActionActivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int cooldown, int duration) {
        ActionEvent.ActionActivatedEvent event = new ActionEvent.ActionActivatedEvent(factionPlayer, action, cooldown, duration);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }
    public static int fireActionDeactivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int remainingDuration, int cooldown) {
        ActionEvent.ActionDeactivatedEvent event = new ActionEvent.ActionDeactivatedEvent(factionPlayer, action, remainingDuration, cooldown);
        NeoForge.EVENT_BUS.post(event);
        return event.getCooldown();
    }

    public static ActionEvent.ActionUpdateEvent fireActionUpdateEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull ILastingAction<?> action, int remainingDuration) {
        ActionEvent.ActionUpdateEvent event = new ActionEvent.ActionUpdateEvent(factionPlayer, action, remainingDuration);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

}