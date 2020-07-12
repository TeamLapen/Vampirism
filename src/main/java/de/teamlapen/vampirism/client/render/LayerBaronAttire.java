package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.BaronAttireModel;
import de.teamlapen.vampirism.client.model.BaronessAttireModel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;


/**
 * Render attire for baron. Includes Male and female version
 */
public class LayerBaronAttire<T extends LivingEntity, Q extends EntityModel<T>> extends LayerRenderer<T, Q> {
    private final BaronessAttireModel<T> baroness = new BaronessAttireModel<>();
    private final BaronAttireModel<T> baron = new BaronAttireModel<>();
    private final ResourceLocation textureBaroness = new ResourceLocation(REFERENCE.MODID, "textures/entity/baroness_attire.png");
    private final ResourceLocation textureBaron = new ResourceLocation(REFERENCE.MODID, "textures/entity/baron_attire.png");
    private final Predicate<T> predicateFemale;

    /**
     * @param predicateFemale used to choose between baron and baroness attire
     */
    public LayerBaronAttire(IEntityRenderer<T, Q> entityRendererIn, Predicate<T> predicateFemale) {
        super(entityRendererIn);
        this.predicateFemale = predicateFemale;
    }

    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entityIn.isInvisible()) {
            boolean female = predicateFemale.test(entityIn);
            bindTexture(female ? textureBaroness : textureBaron);
            EntityModel<T> model = female ? baroness : baron;
            this.getEntityModel().setModelAttributes(model);
            model.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            model.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
