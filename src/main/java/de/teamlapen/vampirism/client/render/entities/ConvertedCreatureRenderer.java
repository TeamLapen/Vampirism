package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders a converted creature, by rendering it's old creature
 */
@OnlyIn(Dist.CLIENT)
public class ConvertedCreatureRenderer extends EntityRenderer<ConvertedCreatureEntity> {
    public static boolean renderOverlay = false;

    public ConvertedCreatureRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public ResourceLocation getTextureLocation(ConvertedCreatureEntity entity) {
        return null;
    }

    @Override
    public void render(ConvertedCreatureEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int packedLightIn) {
        PathfinderMob creature = entity.getOldCreature();
        if (creature != null) {
//            creature.removed = false;
            renderOverlay = true;
            this.entityRenderDispatcher.render(creature, 0, 0, 0, 0, 0, matrixStack, renderTypeBuffer, packedLightIn);
            renderOverlay = false;
//            creature.removed = true;
        }
    }
}
