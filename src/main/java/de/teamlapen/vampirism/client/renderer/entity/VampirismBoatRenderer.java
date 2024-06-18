package de.teamlapen.vampirism.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.entity.IVampirismBoat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

public class VampirismBoatRenderer extends BoatRenderer {
    private final Map<IVampirismBoat.BoatType, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public VampirismBoatRenderer(EntityRendererProvider.@NotNull Context context, boolean hasChest) {
        super(context, hasChest);
        this.boatResources = Stream.of(IVampirismBoat.BoatType.values()).collect(ImmutableMap.toImmutableMap((type) -> type, (type) -> {
            return Pair.of(ResourceLocation.parse(getTextureLocation(type, hasChest)), this.createBoatModel(context, type, hasChest));
        }));
    }

    private @NotNull BoatModel createBoatModel(EntityRendererProvider.@NotNull Context context, IVampirismBoat.@NotNull BoatType type, boolean hasChest) {
        return hasChest ? new ChestBoatModel(context.bakeLayer(ModEntitiesRender.createChestBoatModelName(type))) : new BoatModel(context.bakeLayer(ModEntitiesRender.createBoatModelName(type)));
    }

    public @NotNull String getTextureLocation(IVampirismBoat.@NotNull BoatType type, boolean hasChest) {
        return hasChest ? REFERENCE.MODID + ":textures/entity/chest_boat/" + type.getName() + ".png" : REFERENCE.MODID + ":textures/entity/boat/" + type.getName() + ".png";
    }

    @NotNull
    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(@NotNull Boat boat) {
        return this.boatResources.get(((IVampirismBoat) boat).getBType());
    }
}
