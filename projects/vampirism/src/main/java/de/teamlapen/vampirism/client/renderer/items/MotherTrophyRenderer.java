package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.GhostModel;
import de.teamlapen.vampirism.client.renderer.entities.GhostRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class MotherTrophyRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier ID = VIdentifier.mod("mother_trophy");
    private final GhostModel model;

    public MotherTrophyRenderer(GhostModel model) {
        this.model = model;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0.0F, -1.701F, 0.0F);
        float f1 = RotationSegment.convertToDegrees(0);
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        submitNodeCollector.submitModel(this.model, new GhostRenderer.GhostRenderState(), poseStack, RenderTypes.entityTranslucentCullItemTarget(GhostRenderer.TEXTURE), lightCoords, overlayCoords, 0, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack posestack = new PoseStack();
        posestack.scale(1.0F, -1.0F, -1.0F);
        this.model.root().getExtentsForGui(posestack, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Void> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MotherTrophyRenderer bake(BakingContext context) {
            return new MotherTrophyRenderer(new GhostModel(context.entityModelSet().bakeLayer(ModEntitiesRender.GHOST)));
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<Void>> type() {
            return MAP_CODEC;
        }
    }
}
