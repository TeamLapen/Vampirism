package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

import javax.annotation.Nonnull;
import java.util.Map;

public class ConvertedChestedHorseRenderer<T extends AbstractChestedHorse> extends ChestedHorseRenderer<T> {
    private static final Map<EntityType<?>, ResourceLocation> entityTypes = Maps.newHashMap(ImmutableMap.of(ModEntities.CONVERTED_DONKEY.get(), new ResourceLocation("textures/entity/horse/donkey.png"), ModEntities.CONVERTED_MULE.get(), new ResourceLocation("textures/entity/horse/mule.png")));

    public ConvertedChestedHorseRenderer(EntityRendererProvider.Context context, ModelLayerLocation type) {
        super(context, 0.87f, type);
        this.addLayer(new VampireEntityLayer<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png"), false));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entityTypes.get(entity.getType());
    }
}
