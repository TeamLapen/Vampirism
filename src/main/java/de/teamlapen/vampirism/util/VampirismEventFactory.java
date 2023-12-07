package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VampirismEventFactory {

    public static boolean fireVillagerCaptureEventPre(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        VampirismVillageEvent.VillagerCaptureFinish.Pre event = new VampirismVillageEvent.VillagerCaptureFinish.Pre(totem, villagerIn, forced);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult().equals(Event.Result.DENY);
    }

    public static void fireVillagerCaptureEventPost(@NotNull ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
        MinecraftForge.EVENT_BUS.post(new VampirismVillageEvent.VillagerCaptureFinish.Post(totem, villagerIn, forced));
    }

    public static @NotNull Villager fireSpawnNewVillagerEvent(@NotNull ITotem totem, @Nullable Mob oldEntity, @NotNull Villager newEntity, boolean replaceOld) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(totem, oldEntity, newEntity, replaceOld);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewVillager();
    }

    public static Event.Result fireCanJoinFactionEvent(@NotNull IFactionPlayerHandler playerHandler, @Nullable IPlayableFaction<?> currentFaction, IPlayableFaction<?> newFaction) {
        PlayerFactionEvent.CanJoinFaction event = new PlayerFactionEvent.CanJoinFaction(playerHandler, currentFaction, newFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean fireChangeLevelOrFactionEvent(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> currentFaction, int currentLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChangePre event = new PlayerFactionEvent.FactionLevelChangePre(player, currentFaction, currentLevel, newFaction, newLevel);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void fireFactionLevelChangedEvent(@NotNull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> oldFaction, int oldLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChanged event = new PlayerFactionEvent.FactionLevelChanged(player, oldFaction, oldLevel, newFaction, newLevel);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean fireMakeAggressive(@NotNull ITotem totem, @NotNull Villager entity) {
        VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(totem, entity);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static @NotNull Pair<Float, Float> fireDefineRaidStrengthEvent(@NotNull ITotem totem, int badOmenLevel, float defendStrength, float attackStrength) {
        VampirismVillageEvent.DefineRaidStrength event = new VampirismVillageEvent.DefineRaidStrength(totem, badOmenLevel, defendStrength, attackStrength);
        MinecraftForge.EVENT_BUS.post(event);
        return Pair.of(event.getDefendStrength(), event.getAttackStrength());
    }

    public static @NotNull BloodDrinkEvent.PlayerDrinkBloodEvent fireVampirePlayerDrinkBloodEvent(@NotNull IVampirePlayer vampirePlayer, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.PlayerDrinkBloodEvent event = new BloodDrinkEvent.PlayerDrinkBloodEvent(vampirePlayer, amount, saturationAmount, useRemaining, bloodSource);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static @NotNull BloodDrinkEvent.EntityDrinkBloodEvent fireVampireDrinkBlood(@NotNull IVampire vampire, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.EntityDrinkBloodEvent event = new BloodDrinkEvent.EntityDrinkBloodEvent(vampire, amount, saturationAmount, useRemaining, bloodSource);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    public static @NotNull ActionEvent.ActionActivatedEvent fireActionActivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int cooldown, int duration) {
        ActionEvent.ActionActivatedEvent event = new ActionEvent.ActionActivatedEvent(factionPlayer, action, cooldown, duration);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    public static int fireActionDeactivatedEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int remainingDuration, int cooldown) {
        ActionEvent.ActionDeactivatedEvent event = new ActionEvent.ActionDeactivatedEvent(factionPlayer, action, remainingDuration, cooldown);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCooldown();
    }
    public static Event.Result fireActionUpdateEvent(@NotNull IFactionPlayer<?> factionPlayer, @NotNull IAction<?> action, int remainingDuration) {
        ActionEvent.ActionUpdateEvent event = new ActionEvent.ActionUpdateEvent(factionPlayer, action, remainingDuration);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

}