package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.BaronAttireModel;
import de.teamlapen.vampirism.client.model.BaronWrapperModel;
import de.teamlapen.vampirism.client.model.BaronessAttireModel;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;


/**
 * Render attire for baron. Includes Male and female version
 */
public class BaronAttireLayer extends LayerRenderer<VampireBaronEntity, BaronWrapperModel> {
    private final BaronessAttireModel baroness = new BaronessAttireModel();
    private final BaronAttireModel baron = new BaronAttireModel();
    private final ResourceLocation textureBaroness = new ResourceLocation(REFERENCE.MODID, "textures/entity/baroness_attire.png");
    private final ResourceLocation textureBaron = new ResourceLocation(REFERENCE.MODID, "textures/entity/baron_attire.png");
    private final Predicate<VampireBaronEntity> predicateFemale;

    /**
     * @param predicateFemale used to choose between baron and baroness attire
     */
    public BaronAttireLayer(IEntityRenderer<VampireBaronEntity, BaronWrapperModel> entityRendererIn, Predicate<VampireBaronEntity> predicateFemale) {
        super(entityRendererIn);
        this.predicateFemale = predicateFemale;
    }


    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            boolean female = predicateFemale.test(entityIn);
            EntityModel<VampireBaronEntity> model = female ? baroness : baron;
            renderCopyCutoutModel(this.getEntityModel(), model, female ? textureBaroness : textureBaron, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
        }
    }


}