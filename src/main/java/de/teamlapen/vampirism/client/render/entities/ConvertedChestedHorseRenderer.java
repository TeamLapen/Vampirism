package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class ConvertedChestedHorseRenderer<T extends AbstractChestedHorseEntity> extends ChestedHorseRenderer<T> {
    private static final Map<EntityType<?>, ResourceLocation> entityTypes = Maps.newHashMap(ImmutableMap.of(ModEntities.CONVERTED_DONKEY.get(), new ResourceLocation("textures/entity/horse/donkey.png"), ModEntities.CONVERTED_MULE.get(), new ResourceLocation("textures/entity/horse/mule.png")));

    public ConvertedChestedHorseRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, 0.87f);
        this.addLayer(new VampireEntityLayer<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png"), false));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entityTypes.get(entity.getType());
    }
}
