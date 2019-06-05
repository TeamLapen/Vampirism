package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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

    public static ResourceLocation fireCaptureEntityEvent(@Nullable IVampirismVillage village, @Nonnull IFaction faction) {
        VampirismVillageEvent.CaptureEntity event = new VampirismVillageEvent.CaptureEntity(faction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getEntity();
    }

    public static VampirismVillageEvent.SpawnNewVillager fireSpawnNewVillager(VampirismVillage village, EntityVillager seed, boolean converted, IPlayableFaction controllingFaction) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(village, seed, converted, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean fireReplaceVillageBlock(VampirismVillage village, World world, IBlockState b, IPlayableFaction controllingFaction) {
        VampirismVillageEvent.ReplaceBlock event = new VampirismVillageEvent.ReplaceBlock(village, world, b, controllingFaction);
        if (event.getResult().equals(Result.DENY))
            return false;
        return true;
    }
}
