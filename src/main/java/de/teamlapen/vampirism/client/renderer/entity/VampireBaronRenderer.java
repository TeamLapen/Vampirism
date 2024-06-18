package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BaronModel;
import de.teamlapen.vampirism.client.model.BaronWrapperModel;
import de.teamlapen.vampirism.client.model.BaronessModel;
import de.teamlapen.vampirism.client.renderer.entity.layers.BaronAttireLayer;
import de.teamlapen.vampirism.client.renderer.entity.layers.WingsLayer;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VampireBaronRenderer extends MobRenderer<VampireBaronEntity, BaronWrapperModel> {

    private static final ResourceLocation textureLord = VResourceLocation.mod("textures/entity/baron.png");
    private static final ResourceLocation textureLady = VResourceLocation.mod("textures/entity/baroness.png");
    private static final ResourceLocation textureLordEnraged = VResourceLocation.mod("textures/entity/baron_enraged.png");
    private static final ResourceLocation textureLadyEnraged = VResourceLocation.mod("textures/entity/baroness_enraged.png");


    public VampireBaronRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BaronWrapperModel(new BaronModel(context.bakeLayer(ModEntitiesRender.BARON)), new BaronessModel(context.bakeLayer(ModEntitiesRender.BARONESS))), 0.5F);
        this.addLayer(new WingsLayer<>(this, context.getModelSet(), vampireBaronEntity -> true, (entity, model) -> model.getBodyPart(entity)));
        this.addLayer(new BaronAttireLayer(this, context, VampireBaronEntity::isLady));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull VampireBaronEntity entity) {
        return entity.isEnraged() ? (entity.isLady() ? textureLadyEnraged : textureLordEnraged) : (entity.isLady() ? textureLady : textureLord);
    }

}