package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entity.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.util.PlayerModelType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class VampireMinionRenderer extends DualBipedRenderer<VampireMinionEntity, PlayerBodyOverlayLayer.VisibilityPlayerModel<VampireMinionEntity>> {

    private final Pair<ResourceLocation, PlayerModelType> @NotNull [] textures;
    private final Pair<ResourceLocation, PlayerModelType> @NotNull [] minionSpecificTextures;


    public VampireMinionRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_SLIM), true), 0.5F);
        textures = gatherTextures("textures/entity/vampire", true);
        minionSpecificTextures = gatherTextures("textures/entity/minion/vampire", false);

        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_ARMOR_INNER)), new HumanoidModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_ARMOR_OUTER)), context.getModelManager()));
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    public int getVampireTextureCount() {
        return this.textures.length;
    }

    @Override
    protected Pair<ResourceLocation, PlayerModelType> determineTextureAndModel(@NotNull VampireMinionEntity entity) {
        Pair<ResourceLocation, PlayerModelType> p = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getVampireType() % minionSpecificTextures.length] : textures[entity.getVampireType() % textures.length];
        if (entity.shouldRenderLordSkin()) {
            return entity.getOverlayPlayerProperties().map(Pair::getRight).map(b -> Pair.of(p.getLeft(), b)).orElse(p);
        }
        return p;
    }

    @Override
    protected void scale(@NotNull VampireMinionEntity entityIn, @NotNull PoseStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

}