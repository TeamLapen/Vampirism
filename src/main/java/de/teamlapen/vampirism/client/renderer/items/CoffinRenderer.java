package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.blocks.CoffinBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoffinRenderer implements SpecialModelRenderer<CoffinBlock> {

    public static final ResourceLocation ID = VResourceLocation.mod("coffin");

    @Override
    public void render(@Nullable CoffinBlock coffin, @NotNull ItemDisplayContext context, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean glint) {
        if (coffin != null) {
            stack.pushPose();
            BakedModel model = Minecraft.getInstance().getModelManager().getStandaloneModel(VResourceLocation.mod("block/coffin/coffin_bottom_" + coffin.getColor().getName()));
            BlockState blockState = coffin.defaultBlockState();
//            stack.translate(0,0.5f,0);
//            stack.mulPose(Axis.XP.rotationDegrees(90));
//            stack.translate(0.5,0.5,0.5);
//            stack.mulPose(Axis.ZP.rotationDegrees(180));
//            stack.translate(-0.5,-0.5,-0.5);
            for (RenderType renderType : model.getRenderTypes(blockState, RandomSource.create(42), ModelData.EMPTY)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(renderType)), blockState, model, 1,1,1,packedLight, packedOverlay, ModelData.EMPTY, renderType);
            }
            stack.popPose();
        }
    }

    @Override
    public @Nullable CoffinBlock extractArgument(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoffinBlock coffin) {
            return coffin;
        }
        return null;
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @NotNull SpecialModelRenderer<?> bake(@NotNull EntityModelSet modelSet) {
            return new CoffinRenderer();
        }

        @Override
        public @NotNull MapCodec<? extends Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
