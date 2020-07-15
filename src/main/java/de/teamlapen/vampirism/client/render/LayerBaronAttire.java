package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.client.model.BaronAttireModel;
import de.teamlapen.vampirism.client.model.BaronWrapperModel;
import de.teamlapen.vampirism.client.model.BaronessAttireModel;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;


/**
 * Render attire for baron. Includes Male and female version
 */
public class LayerBaronAttire extends LayerRenderer<VampireBaronEntity, BaronWrapperModel> {
    private final BaronessAttireModel baroness = new BaronessAttireModel();
    private final BaronAttireModel baron = new BaronAttireModel();
    private final ResourceLocation textureBaroness = new ResourceLocation(REFERENCE.MODID, "textures/entity/baroness_attire.png");
    private final ResourceLocation textureBaron = new ResourceLocation(REFERENCE.MODID, "textures/entity/baron_attire.png");
    private final Predicate<VampireBaronEntity> predicateFemale;

    /**
     * @param predicateFemale used to choose between baron and baroness attire
     */
    public LayerBaronAttire(IEntityRenderer<VampireBaronEntity, BaronWrapperModel> entityRendererIn, Predicate<VampireBaronEntity> predicateFemale) {
        super(entityRendererIn);
        this.predicateFemale = predicateFemale;
    }

    @Override
    public void render(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entityIn.isInvisible()) {
            boolean female = predicateFemale.test(entityIn);
            bindTexture(female ? textureBaroness : textureBaron);
            EntityModel<VampireBaronEntity> model = female ? baroness : baron;
            this.getEntityModel().setModelAttributes(model);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1, 1f, 1);
            model.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            model.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();

        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
