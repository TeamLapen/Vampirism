package de.teamlapen.vampirism.client.renderer.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.BaronAttireModel;
import de.teamlapen.vampirism.client.models.entities.BaronBaseModel;
import de.teamlapen.vampirism.client.models.entities.BaronessAttireModel;
import de.teamlapen.vampirism.client.renderer.entities.VampireBaronRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;


/**
 * Render attire for baron. Includes Male and female version
 */
public class BaronAttireLayer extends RenderLayer<VampireBaronRenderer.VampireBaronRenderState, BaronBaseModel> {
    private final @NotNull BaronessAttireModel baroness;
    private final @NotNull BaronAttireModel baron;
    private final Identifier textureBaroness = VResourceLocation.mod("textures/entity/baroness_attire.png");
    private final Identifier textureBaron = VResourceLocation.mod("textures/entity/baron_attire.png");
    private final Predicate<VampireBaronRenderer.VampireBaronRenderState> predicateFemale;

    /**
     * @param predicateFemale used to choose between baron and baroness attire
     */
    public BaronAttireLayer(@NotNull RenderLayerParent<VampireBaronRenderer.VampireBaronRenderState, BaronBaseModel> entityRendererIn, EntityRendererProvider.@NotNull Context context, Predicate<VampireBaronRenderer.VampireBaronRenderState> predicateFemale) {
        super(entityRendererIn);
        this.baroness = new BaronessAttireModel(context.bakeLayer(ModEntitiesRender.BARONESS_ATTIRE));
        this.baron = new BaronAttireModel(context.bakeLayer(ModEntitiesRender.BARON_ATTIRE));
        this.predicateFemale = predicateFemale;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, VampireBaronRenderer.VampireBaronRenderState renderState, float yRot, float xRot) {
        if (!renderState.isInvisible) {
            boolean female = predicateFemale.test(renderState);
            EntityModel<VampireBaronRenderer.VampireBaronRenderState> model = female ? baroness : baron;
            coloredCutoutModelCopyLayerRender(model, female ? textureBaroness : textureBaron, poseStack, nodeCollector, packedLight, renderState, -1, -1);
        }
    }
}