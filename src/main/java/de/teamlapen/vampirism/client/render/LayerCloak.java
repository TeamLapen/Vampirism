package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.BipedCloakedModel;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;


public class LayerCloak<T extends MobEntity, Q extends BipedCloakedModel<T>> extends LayerRenderer<T, Q> {

    private final ResourceLocation textureCloak;
    private final Predicate<T> renderPredicate;

    public LayerCloak(IEntityRenderer<T, Q> entityRendererIn, ResourceLocation texture, Predicate<T> predicate) {
        super(entityRendererIn);
        this.textureCloak = texture;
        this.renderPredicate = predicate;
    }

    @Override
    public void render(T t, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        if (!t.isInvisible() && renderPredicate.test(t)) {
            this.bindTexture(textureCloak);
            this.getEntityModel().renderCloak(p_212842_8_);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
