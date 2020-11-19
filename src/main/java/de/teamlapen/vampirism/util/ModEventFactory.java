package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.event.FactionEvent;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModEventFactory {

    public static boolean fireVillagerCaptureEventPre(@Nonnull ITotem totem, @Nonnull List<VillagerEntity> villagerIn, boolean forced) {
        VampirismVillageEvent.VillagerCaptureFinish.Pre event = new VampirismVillageEvent.VillagerCaptureFinish.Pre(totem, villagerIn, forced);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult().equals(Event.Result.DENY);
    }

    public static void fireVillagerCaptureEventPost(@Nonnull ITotem totem, @Nonnull List<VillagerEntity> villagerIn, boolean forced) {
        MinecraftForge.EVENT_BUS.post(new VampirismVillageEvent.VillagerCaptureFinish.Post(totem, villagerIn, forced));
    }

    public static VillagerEntity fireSpawnNewVillagerEvent(@Nonnull ITotem totem, @Nullable MobEntity oldEntity, @Nonnull VillagerEntity newEntity, boolean replaceOld, boolean converted) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(totem, oldEntity, newEntity, replaceOld, converted);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewVillager();
    }

    public static void fireReplaceVillageBlockEvent(@Nonnull ITotem totem, @Nonnull BlockState b, @Nonnull BlockPos pos) {
        VampirismVillageEvent.ReplaceBlock event = new VampirismVillageEvent.ReplaceBlock(totem, b, pos);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static Event.Result fireCanJoinFactionEvent(@Nonnull IFactionPlayerHandler playerHandler, @Nullable IPlayableFaction<?> currentFaction, IPlayableFaction<?> newFaction) {
        FactionEvent.CanJoinFaction event = new FactionEvent.CanJoinFaction(playerHandler, currentFaction, newFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean fireChangeLevelOrFactionEvent(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction currentFaction, int currentLevel, @Nullable IPlayableFaction newFaction, int newLevel) {
        FactionEvent.ChangeLevelOrFaction event = new FactionEvent.ChangeLevelOrFaction(player, currentFaction, currentLevel, newFaction, newLevel);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean fireMakeAggressive(@Nonnull ITotem totem, @Nonnull VillagerEntity entity) {
        VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(totem, entity);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }
}