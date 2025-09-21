package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class TotemRenderer implements BlockEntityRenderer<TotemBlockEntity> {

    private static final ResourceLocation TOTEM_BEAM_LOCATION = VResourceLocation.mod("textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    public TotemRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TotemBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        
        float textureScale = blockEntity.shouldRenderBeam();
        if (textureScale > 0.0f) {
            long totalWorldTime = level.getGameTime();
            int captureProgress = blockEntity.getCaptureProgress();
            int baseColors = blockEntity.getBaseColor();
            int offset = 0;
            if (captureProgress > 0) {
                int color = blockEntity.getCapturingColors();
                offset = (captureProgress * HEIGHT) / 100;
                BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, TOTEM_BEAM_LOCATION, partialTick, textureScale, totalWorldTime, 0, offset, color, 0.2f, 0.25f);
            }
            BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, TOTEM_BEAM_LOCATION, partialTick, textureScale, totalWorldTime, offset, HEIGHT - offset, baseColors, 0.2f, 0.25f);
        } else {
            Holder<? extends IFaction<?>> faction = blockEntity.getControllingFaction();
            if (!IFaction.isNeutral(faction)) {
                renderFactionName(faction.value(), poseStack, bufferSource, packedLight);
            }
        }
    }

    private void renderFactionName(IFaction<?> faction, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!ModConfig.CLIENT.renderTotemFactionName.getAsBoolean()) return;
        Component displayNameIn = faction.getNamePlural().plainCopy().withStyle(style -> style.withColor((faction.getChatColor())));
        poseStack.pushPose();
        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float nameOffset = (float) (-font.width(displayNameIn) / 2);
        font.drawInBatch(displayNameIn, nameOffset, 0, -2130706433, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(displayNameIn, nameOffset, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TotemBlockEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TotemBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
