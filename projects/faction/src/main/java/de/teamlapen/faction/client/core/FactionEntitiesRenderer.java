package de.teamlapen.faction.client.core;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.npc.VillagerModel;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

public class FactionEntitiesRenderer {

    public static final ModelLayerLocation TASK_MASTER = new ModelLayerLocation(FIdentifier.mod("task_master"), "main");

    public static void onRegisterLayers(EntityRenderersEvent.@NotNull RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FactionEntitiesRenderer.TASK_MASTER, () -> LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64));

    }
}
