package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.common.world.blockentity.VelmorraAltarBlockEntity;
import de.teamlapen.vampirism.common.world.blocks.VelmorraAltarBlock;
import de.teamlapen.vampirism.common.world.items.RitualKnifeItem;
import de.teamlapen.vampirism.common.world.items.component.RitualKnifeType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Renders the inserted ritual knives with their 3D item model, sticking vertically out of the altar top.
 * The knives are only halfway inserted while the portal is closed and fully inserted while it is open.
 */
public class VelmorraAltarRenderer implements BlockEntityRenderer<VelmorraAltarBlockEntity, VelmorraAltarRenderer.VelmorraAltarRenderState> {

    private static final float SCALE = 0.6f;
    /**
     * Height of the knife center above the altar top
     */
    private static final float HALF_INSERTED_HEIGHT = 0.27f;
    private static final float FULLY_INSERTED_HEIGHT = -0.03f;
    private static final RitualKnifeType[] TYPES = RitualKnifeType.values();

    private final ItemModelResolver itemModelResolver;
    private final Map<RitualKnifeType, ItemStack> knives = new EnumMap<>(RitualKnifeType.class);

    public VelmorraAltarRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    private ItemStack knife(RitualKnifeType type) {
        return this.knives.computeIfAbsent(type, RitualKnifeItem::create);
    }

    @Override
    public void extractRenderState(VelmorraAltarBlockEntity blockEntity, VelmorraAltarRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        BlockState state = blockEntity.getBlockState();
        renderState.channeling = state.getValue(VelmorraAltarBlock.CHANNELING);
        renderState.open = state.getValue(VelmorraAltarBlock.OPEN);
        for (RitualKnifeType type : TYPES) {
            ItemStackRenderState itemState = renderState.knives[type.getId()];
            if (state.getValue(VelmorraAltarBlock.knifeProperty(type))) {
                this.itemModelResolver.updateForTopItem(itemState, knife(type), ItemDisplayContext.NONE, blockEntity.getLevel(), null, type.getId());
            } else {
                itemState.clear();
            }
        }
    }

    @Override
    public void submit(VelmorraAltarRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        float height = renderState.open && !renderState.channeling ? FULLY_INSERTED_HEIGHT : HALF_INSERTED_HEIGHT;
        int light = renderState.channeling || renderState.open ? LightCoordsUtil.FULL_BRIGHT : renderState.lightCoords;
        for (RitualKnifeType type : TYPES) {
            ItemStackRenderState itemState = renderState.knives[type.getId()];
            if (itemState.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate(0.5, 1 + height, 0.5);
            // seeking north, passage east, anchoring west
            poseStack.mulPose(Axis.YP.rotationDegrees(switch (type) {
                case SEEKING -> 0;
                case PASSAGE -> -90;
                case ANCHORING -> 90;
            }));
            poseStack.translate(0, 0, -0.25);
            // the knife texture is diagonal, turn it so the blade points straight down
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            poseStack.scale(SCALE, SCALE, SCALE);
            itemState.submit(poseStack, nodeCollector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    @Override
    public AABB getRenderBoundingBox(VelmorraAltarBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).expandTowards(0, 1, 0);
    }

    @Override
    public VelmorraAltarRenderState createRenderState() {
        return new VelmorraAltarRenderState();
    }

    public static class VelmorraAltarRenderState extends BlockEntityRenderState {
        private final ItemStackRenderState[] knives = new ItemStackRenderState[]{new ItemStackRenderState(), new ItemStackRenderState(), new ItemStackRenderState()};
        private boolean channeling;
        private boolean open;
    }
}
