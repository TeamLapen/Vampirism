package de.teamlapen.vampirism.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.client.renderer.entity.layers.ConvertedVampireEntityLayer;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConvertedChestedHorseRenderer<T extends AbstractChestedHorse> extends ChestedHorseRenderer<T> {
    private static final Map<EntityType<?>, ResourceLocation> entityTypes = Maps.newHashMap(ImmutableMap.of(ModEntities.CONVERTED_DONKEY.get(), new ResourceLocation("textures/entity/horse/donkey.png"), ModEntities.CONVERTED_MULE.get(), new ResourceLocation("textures/entity/horse/mule.png")));

    public ConvertedChestedHorseRenderer(EntityRendererProvider.@NotNull Context context, float pScale, @NotNull ModelLayerLocation type) {
        super(context, pScale, type);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull T entity) {
        return entityTypes.get(entity.getType());
    }
}
