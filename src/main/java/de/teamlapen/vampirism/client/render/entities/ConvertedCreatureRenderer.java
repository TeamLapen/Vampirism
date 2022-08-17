package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

/**
 * Renders a converted creature, by rendering its old creature
 */
@SuppressWarnings("rawtypes")
@OnlyIn(Dist.CLIENT)
public class ConvertedCreatureRenderer extends EntityRenderer<ConvertedCreatureEntity> { // RawType because of ConvertedCreatureEntity#IMob
    public static boolean renderOverlay = false;

    public ConvertedCreatureRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull ConvertedCreatureEntity entity) {
        return null;
    }

    @Override
    public void render(ConvertedCreatureEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource renderTypeBuffer, int packedLightIn) {
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
