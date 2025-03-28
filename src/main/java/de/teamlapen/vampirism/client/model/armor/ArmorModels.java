package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ArmorModels {

    @Unmodifiable
    private static final Map<ModelLayerLocation, Function<ModelPart, VampirismArmorModel>> MODEL_CONSTRUCTORS;
    @Unmodifiable
    private Map<ModelLayerLocation, VampirismArmorModel> models = new HashMap<>();

    @SubscribeEvent
    public void onLayersLoaded(EntityRenderersEvent.AddLayers event) {
        EntityModelSet entityModels = event.getEntityModels();
        ImmutableMap.Builder<ModelLayerLocation, VampirismArmorModel> models = ImmutableMap.builder();
        MODEL_CONSTRUCTORS.forEach((location, constructor) -> models.put(location, constructor.apply(entityModels.bakeLayer(location))));
        this.models = models.build();
    }

    public VampirismArmorModel getModel(ModelLayerLocation location) {
        if (!this.models.containsKey(location)) {
            throw new IllegalStateException("Model was not loaded '" + location + "'");
        }
        return this.models.get(location);
    }

    static  {
        ImmutableMap.Builder<ModelLayerLocation, Function<ModelPart, VampirismArmorModel>> builder = ImmutableMap.builder();
        builder.put(ModEntitiesRender.CLOAK, CloakModel::new);
        builder.put(ModEntitiesRender.CLOTHING_HAT, VampireHatModel::new);
        builder.put(ModEntitiesRender.CLOTHING_CROWN, ClothingCrownModel::new);
        builder.put(ModEntitiesRender.CLOTHING_BOOTS, ClothingBootsModel::new);
        builder.put(ModEntitiesRender.CLOTHING_PANTS, ClothingPantsModel::new);
        builder.put(ModEntitiesRender.HUNTER_HAT_TALL, HunterHatModel::new);
        builder.put(ModEntitiesRender.HUNTER_HAT_BROAD, HunterHatModel::new);
        builder.put(ModEntitiesRender.GENERIC_BIPED, DummyClothingModel::new);
        MODEL_CONSTRUCTORS = builder.build();
    }
}
