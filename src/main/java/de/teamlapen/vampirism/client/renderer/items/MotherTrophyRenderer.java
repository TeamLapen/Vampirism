package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.GhostModel;
import de.teamlapen.vampirism.client.renderer.entities.GhostRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jetbrains.annotations.NotNull;

public class MotherTrophyRenderer implements NoDataSpecialModelRenderer {

    public static final ResourceLocation ID = VResourceLocation.mod("mother_trophy");
    private final GhostModel model;

    public MotherTrophyRenderer(GhostModel model) {
        this.model = model;
    }

    @Override
    public void render(@NotNull ItemDisplayContext context, @NotNull PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean glint) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(GhostRenderer.TEXTURE));
        stack.pushPose();
        stack.translate(0.5, 0, 0.5);
        stack.mulPose(Axis.ZP.rotationDegrees(180));
        stack.translate(0.0F, -1.701F, 0.0F);
        float f1 = RotationSegment.convertToDegrees(0);
        stack.mulPose(Axis.YP.rotationDegrees(f1));
        this.model.renderToBuffer(stack, buffer, packedLight, packedOverlay);
        stack.popPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet set) {
            return new MotherTrophyRenderer(new GhostModel(set.bakeLayer(ModEntitiesRender.GHOST)));
        }
    }
}
