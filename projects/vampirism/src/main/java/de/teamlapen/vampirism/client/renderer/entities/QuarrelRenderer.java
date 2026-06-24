package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.HeavyQuarrelModel;
import de.teamlapen.vampirism.client.models.entities.QuarrelModel;
import de.teamlapen.vampirism.common.world.entity.QuarrelEntity;
import de.teamlapen.vampirism.common.world.items.crossbow.behavior.HeavyBehavior;
import de.teamlapen.vampirism.common.world.items.crossbow.behavior.SpitfireBehavior;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class QuarrelRenderer extends EntityRenderer<QuarrelEntity, QuarrelRenderer.QuarrelRenderState> {

    public static final Identifier QUARREL_LOCATION = VIdentifier.mod("textures/entity/quarrel/quarrel.png");
    public static final Identifier HEAVY_QUARREL_LOCATION = VIdentifier.mod("textures/entity/quarrel/heavy_quarrel.png");

    private static final SpriteId ALCHEMICAL_FIRE_0 = Sheets.BLOCKS_MAPPER.apply(VIdentifier.mod("alchemical_fire_0"));
    private static final SpriteId ALCHEMICAL_FIRE_1 = Sheets.BLOCKS_MAPPER.apply(VIdentifier.mod("alchemical_fire_1"));

    private final QuarrelModel model;
    private final HeavyQuarrelModel heavyModel;
    private final SpriteGetter sprites;

    public QuarrelRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new QuarrelModel(context.bakeLayer(ModEntitiesRender.QUARREL));
        this.heavyModel = new HeavyQuarrelModel(context.bakeLayer(ModEntitiesRender.HEAVY_QUARREL));
        this.sprites = context.getSprites();
    }

    @Override
    public void submit(QuarrelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        EntityModel<ArrowRenderState> model = state.heavy ? this.heavyModel : this.model;
        submitNodeCollector.submitModel(model, state, poseStack, this.getTextureLocation(state), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
        if (state.alchemicalFire) {
            submitAlchemicalFlame(state, poseStack, submitNodeCollector, camera);
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public QuarrelRenderState createRenderState() {
        return new QuarrelRenderState();
    }

    protected Identifier getTextureLocation(QuarrelRenderState state) {
        return state.heavy ? HEAVY_QUARREL_LOCATION : QUARREL_LOCATION;
    }

    @Override
    public void extractRenderState(QuarrelEntity entity, QuarrelRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.xRot = entity.getXRot(partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        state.shake = (float) entity.shakeTime - partialTicks;
        state.heavy = entity.getArrowType() instanceof HeavyBehavior;
        // Render alchemical fire instead of the normal one for the spitfire quarrel
        state.alchemicalFire = state.displayFireAnimation && entity.getArrowType() instanceof SpitfireBehavior;
        if (state.alchemicalFire) {
            state.displayFireAnimation = false;
        }
    }

    /**
     * Copied and edited from {@link FlameFeatureRenderer}.
     */
    private void submitAlchemicalFlame(QuarrelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        TextureAtlasSprite fire0 = this.sprites.get(ALCHEMICAL_FIRE_0);
        TextureAtlasSprite fire1 = this.sprites.get(ALCHEMICAL_FIRE_1);
        Quaternionf rotation = Mth.rotationAroundAxis(Mth.Y_AXIS, camera.orientation, new Quaternionf());
        submitNodeCollector.submitCustomGeometry(poseStack, Sheets.cutoutBlockSheet(), (pose, buffer) -> renderFlame(pose, buffer, fire0, fire1, state, rotation));
    }

    private void renderFlame(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite fire1, TextureAtlasSprite fire2, QuarrelRenderState state, Quaternionf rotation) {
        float s = state.boundingBoxWidth * 1.4F;
        pose.scale(s, s, s);
        float r = 0.5F;
        float h = state.boundingBoxHeight / s;
        float yo = 0.0F;
        pose.rotate(rotation);
        pose.translate(0.0F, 0.0F, 0.3F - (float)((int)h) * 0.02F);
        float zo = 0.0F;
        int ss = 0;

        for (int lightCoords = LightCoordsUtil.withBlock(state.lightCoords, 15); h > 0.0F; ++ss) {
            TextureAtlasSprite tex = ss % 2 == 0 ? fire1 : fire2;
            float u0 = tex.getU0();
            float v0 = tex.getV0();
            float u1 = tex.getU1();
            float v1 = tex.getV1();
            if (ss / 2 % 2 == 0) {
                float tmp = u1;
                u1 = u0;
                u0 = tmp;
            }

            FlameFeatureRenderer.fireVertex(pose, buffer, -r - 0.0F, 0.0F - yo, zo, u1, v1, lightCoords);
            FlameFeatureRenderer.fireVertex(pose, buffer, r - 0.0F, 0.0F - yo, zo, u0, v1, lightCoords);
            FlameFeatureRenderer.fireVertex(pose, buffer, r - 0.0F, 1.4F - yo, zo, u0, v0, lightCoords);
            FlameFeatureRenderer.fireVertex(pose, buffer, -r - 0.0F, 1.4F - yo, zo, u1, v0, lightCoords);
            h -= 0.45F;
            yo -= 0.45F;
            r *= 0.9F;
            zo -= 0.03F;
        }
    }

    public static class QuarrelRenderState extends ArrowRenderState {

        public boolean alchemicalFire;
        public boolean heavy;
    }
}