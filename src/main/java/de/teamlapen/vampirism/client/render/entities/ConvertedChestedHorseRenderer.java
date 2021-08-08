package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class ConvertedChestedHorseRenderer<T extends AbstractChestedHorse> extends ChestedHorseRenderer<T> {
    private static final Map<EntityType<?>, ResourceLocation> entityTypes = Maps.newHashMap(ImmutableMap.of(ModEntities.converted_donkey, new ResourceLocation("textures/entity/horse/donkey.png"), ModEntities.converted_mule, new ResourceLocation("textures/entity/horse/mule.png")));

    public ConvertedChestedHorseRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, 0.87f);
        this.addLayer(new VampireEntityLayer<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png"), false));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entityTypes.get(entity.getType());
    }
}
