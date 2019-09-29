package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.event.FactionEvent;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModEventFactory {

    public static boolean fireVillagerCaptureEvent(@Nonnull List<VillagerEntity> villagerIn, @Nullable IPlayableFaction<?> controllingFactionIn, @Nonnull IPlayableFaction<?> capturingFactionIn, @Nonnull AxisAlignedBB affectedArea) {
        VampirismVillageEvent.VillagerCaptureFinish event = new VampirismVillageEvent.VillagerCaptureFinish(villagerIn, controllingFactionIn, capturingFactionIn, affectedArea);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult().equals(Event.Result.DENY);
    }

    public static EntityType<? extends MobEntity> fireSpawnCaptureEntityEvent(@Nonnull IFaction<?> faction) {
        VampirismVillageEvent.SpawnCaptureEntity event = new VampirismVillageEvent.SpawnCaptureEntity(faction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getEntity();
    }

    public static VampirismVillageEvent.SpawnNewVillager fireSpawnNewVillagerEvent(@Nonnull VillagerEntity seed, boolean converted, IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(seed, converted, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void fireReplaceVillageBlockEvent(@Nonnull World world, @Nonnull BlockState b, @Nonnull BlockPos pos, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.ReplaceBlock event = new VampirismVillageEvent.ReplaceBlock(world, b, pos, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean fireInitiateCaptureEvent(@Nonnull World world, @Nullable IPlayableFaction<?> controllingFaction, @Nonnull IPlayableFaction<?> capturingFaction) {
        VampirismVillageEvent.InitiateCapture event = new VampirismVillageEvent.InitiateCapture(world, controllingFaction, capturingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.getResult().equals(Event.Result.DENY);
    }

    public static VampirismVillageEvent.SpawnFactionVillager fireSpawnFactionVillagerEvent(@Nonnull VillagerEntity seed, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.SpawnFactionVillager event = new VampirismVillageEvent.SpawnFactionVillager(seed, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void fireUpdateBoundingBoxEvent(@Nonnull MutableBoundingBox bb) {
        VampirismVillageEvent.UpdateBoundingBox event = new VampirismVillageEvent.UpdateBoundingBox(bb);
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
}