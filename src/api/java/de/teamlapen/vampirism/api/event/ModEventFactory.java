package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModEventFactory {

    public static boolean fireVillagerCaptureEvent(@Nullable IVampirismVillage village, @Nonnull List<EntityVillager> villagerIn, @Nullable IPlayableFaction<?> controllingFactionIn, @Nonnull IPlayableFaction<?> capturingFactionIn) {
        VampirismVillageEvent.VillagerCaptureFinish event = new VampirismVillageEvent.VillagerCaptureFinish(village, villagerIn, controllingFactionIn, capturingFactionIn);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult().equals(Result.DENY))
            return true;
        return false;
    }

    public static ResourceLocation fireCaptureEntityEvent(@Nullable IVampirismVillage village, @Nonnull IFaction<?> faction) {
        VampirismVillageEvent.CaptureEntity event = new VampirismVillageEvent.CaptureEntity(village, faction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getEntity();
    }

    public static VampirismVillageEvent.SpawnNewVillager fireSpawnNewVillagerEvent(@Nullable IVampirismVillage village, @Nonnull EntityVillager seed, boolean converted, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(village, seed, converted, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void fireReplaceVillageBlockEvent(@Nullable IVampirismVillage village, @Nonnull World world, @Nonnull IBlockState b, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.ReplaceBlock event = new VampirismVillageEvent.ReplaceBlock(village, world, b, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean fireInitateCaptureEvent(@Nullable IVampirismVillage village, @Nonnull World world, @Nullable IPlayableFaction<?> controllingFaction, @Nonnull IPlayableFaction<?> capturingFaction) {
        VampirismVillageEvent.InitiateCapture event = new VampirismVillageEvent.InitiateCapture(village, world, controllingFaction, capturingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult().equals(Result.DENY))
            return false;
        return true;
    }

    public static VampirismVillageEvent.SpawnFactionVillager fireSpawnFactionVillagerEvent(@Nullable IVampirismVillage village, @Nonnull EntityVillager seed, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.SpawnFactionVillager event = new VampirismVillageEvent.SpawnFactionVillager(village, seed, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void fireRegisterVillageBBEvent(@Nullable IVampirismVillage village,@Nonnull StructureBoundingBox bb) {
        VampirismVillageEvent.RegisterVillageBoundingBoxEvent event = new VampirismVillageEvent.RegisterVillageBoundingBoxEvent(village, bb);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
