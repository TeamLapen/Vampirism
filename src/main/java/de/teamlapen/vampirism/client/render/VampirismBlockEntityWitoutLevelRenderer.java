package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class VampirismBlockEntityWitoutLevelRenderer extends BlockEntityWithoutLevelRenderer {


    private CoffinBlockEntity coffin;
    private final BlockEntityRenderDispatcher dispatcher;

    public VampirismBlockEntityWitoutLevelRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
        dispatcher = p_172550_;
    }


    @Override
    public void renderByItem(ItemStack itemStack, @Nonnull ItemTransforms.TransformType transformType, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CoffinBlock) {
            if (coffin == null) {
                coffin = new CoffinBlockEntity(true, BlockPos.ZERO, ModBlocks.COFFIN.get().defaultBlockState());
            }
            dispatcher.renderItem(this.coffin, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        } else {
            super.renderByItem(itemStack, transformType, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        }
    }
}
