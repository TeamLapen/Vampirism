package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import de.teamlapen.vampirism.client.model.BaronWrapperModel;
import de.teamlapen.vampirism.client.render.layers.BaronAttireLayer;
import de.teamlapen.vampirism.client.render.layers.WingsLayer;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireBaronRenderer extends MobRenderer<VampireBaronEntity, BaronWrapperModel> {

    private static final ResourceLocation textureLord = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baron.png");
    private static final ResourceLocation textureLady = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baroness.png");
    private static final ResourceLocation textureLordEnraged = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baron_enraged.png");
    private static final ResourceLocation textureLadyEnraged = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baroness_enraged.png");


    public VampireBaronRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BaronWrapperModel(), 0.5F);
        this.addLayer(new WingsLayer<>(this, Predicates.alwaysTrue(), (entity, model) -> model.getBodyPart(entity)));
        this.addLayer(new BaronAttireLayer(this, VampireBaronEntity::isLady));
    }

    @Override
    public ResourceLocation getEntityTexture(VampireBaronEntity entity) {
        return entity.isEnraged() ? (entity.isLady() ? textureLadyEnraged : textureLordEnraged) : (entity.isLady() ? textureLady : textureLord);
    }

}