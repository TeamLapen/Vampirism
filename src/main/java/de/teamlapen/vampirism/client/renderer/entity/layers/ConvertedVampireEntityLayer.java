package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.client.renderer.entity.ConvertedCreatureRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Render the vampire overlay for converted creatures
 */
public class ConvertedVampireEntityLayer<T extends LivingEntity, U extends EntityModel<T>> extends RenderLayer<T, U> {

    private final boolean checkIfRender;

    /**
     * @param checkIfRender If it should check if {@link ConvertedCreatureRenderer#renderOverlay} is true
     */
    public ConvertedVampireEntityLayer(@NotNull RenderLayerParent<T, U> entityRendererIn, boolean checkIfRender) {
        super(entityRendererIn);
        this.checkIfRender = checkIfRender;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, @NotNull T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        if(!entity.isInvisible()) {
            String sourceId = null;
            if (ConvertedCreatureRenderer.renderOverlay) {
                sourceId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
            } else if(!checkIfRender && entity instanceof IConvertedCreature<?> converted) {
                sourceId = converted.getSourceEntityId();
            }
            ResourceLocation texture = VampirismAPI.entityRegistry().getConvertibleOverlay(sourceId);
            if (texture != null) {
                renderColoredCutoutModel(this.getParentModel(), texture, matrixStack, iRenderTypeBuffer, i, entity, 1, 1, 1);
            }
        }

    }
}
