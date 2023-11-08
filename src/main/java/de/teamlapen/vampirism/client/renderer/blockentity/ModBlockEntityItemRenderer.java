package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.blockentity.MotherTrophyBlockEntity;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModBlockEntityItemRenderer extends BlockEntityWithoutLevelRenderer {

    private MotherTrophyBlockEntity mother_trophy;
    private final BlockEntityRenderDispatcher pBlockEntityRenderDispatcher;

    public ModBlockEntityItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
        this.pBlockEntityRenderDispatcher = pBlockEntityRenderDispatcher;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        this.mother_trophy = new MotherTrophyBlockEntity(BlockPos.ZERO, ModBlocks.MOTHER_TROPHY.get().defaultBlockState());
    }

    @Override
    public void renderByItem(@NotNull ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pStack.is(ModBlocks.MOTHER_TROPHY.get().asItem())) {
            this.pBlockEntityRenderDispatcher.renderItem(mother_trophy, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
    }
}
