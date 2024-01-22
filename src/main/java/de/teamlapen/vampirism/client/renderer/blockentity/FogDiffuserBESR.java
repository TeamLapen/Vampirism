package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FogDiffuserBESR extends VampirismBESR<de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack motherCore;

    public FogDiffuserBESR(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        motherCore = ModItems.MOTHER_CORE.get().getDefaultInstance();
    }

    @Override
    public void render(@NotNull de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.3, 0.5);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getLevel().getGameTime() + pPartialTick));
        this.itemRenderer.renderStatic(motherCore, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, pBlockEntity.getLevel(), 0);
        pPoseStack.popPose();
    }
}
