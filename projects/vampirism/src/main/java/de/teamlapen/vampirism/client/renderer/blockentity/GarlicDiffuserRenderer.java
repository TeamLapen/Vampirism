package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.GarlicDiffuserBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class GarlicDiffuserRenderer implements BlockEntityRenderer<GarlicDiffuserBlockEntity, GarlicDiffuserRenderer.GarlicDiffuserRenderState> {

    private final ItemModelResolver itemModelResolver;
    private final ItemStack garlic;

    public GarlicDiffuserRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.garlic = ModBlocks.GARLIC.toStack();
    }

    @Override
    public void submit(GarlicDiffuserRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.scale(renderState.scale, renderState.scale, renderState.scale);

        poseStack.mulPose(Axis.YP.rotationDegrees((renderState.ticks) % 360));
            poseStack.translate(-0.5D, 0, -0.5);
            poseStack.pushPose();
        renderState.itemState.submit(poseStack, new CutoutStorage(nodeCollector), renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1);
            poseStack.popPose();
            poseStack.popPose();
    }

    public static class CutoutStorage extends SubmitNodeStorage {

        private final SubmitNodeCollector storage;

        public CutoutStorage(SubmitNodeCollector storage) {
            this.storage = storage;
        }

        @Override
        public SubmitNodeCollection order(int order) {
            return (SubmitNodeCollection) this.storage.order(order);
        }

        @Override
        public void submitItem(PoseStack p_434225_, ItemDisplayContext p_439693_, int p_433159_, int p_435776_, int p_440725_, int[] p_439144_, List<BakedQuad> p_439266_, RenderType p_439740_, ItemStackRenderState.FoilType p_440348_) {
            super.submitItem(p_434225_, p_439693_, p_433159_, p_435776_, p_440725_, p_439144_, p_439266_, ModRenderPipelines.cutoutNoDepth().get(), p_440348_);
        }
    }

    @Override
    public void extractRenderState(GarlicDiffuserBlockEntity blockEntity, GarlicDiffuserRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.ticks = blockEntity.getLevel().getGameTime() + partialTick;
        renderState.scale = (float) Mth.clamp(Math.sqrt(blockEntity.getBlockPos().distSqr(Minecraft.getInstance().getCameraEntity().blockPosition())) / 16, 1, 3);
        this.itemModelResolver.updateForTopItem(renderState.itemState, this.garlic, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
    }

    @Override
    public GarlicDiffuserRenderState createRenderState() {
        return new GarlicDiffuserRenderState();
    }

    @Override
    public boolean shouldRender(GarlicDiffuserBlockEntity blockEntity, Vec3 cameraPos) {
        return Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living && Stream.of(living.getMainHandItem(), living.getOffhandItem()).anyMatch(i -> i.is(ModItems.GARLIC_FINDER) && blockEntity.isInRange(living.blockPosition()));
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class GarlicDiffuserRenderState extends BlockEntityRenderState {
        public ItemStackRenderState itemState = new ItemStackRenderState();
        public float ticks;
        public float scale;
    }
}
