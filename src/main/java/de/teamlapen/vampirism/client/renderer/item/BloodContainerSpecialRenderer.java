package de.teamlapen.vampirism.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import de.teamlapen.lib.lib.client.VertexUtils;
import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.client.core.ModMaterials;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModFluids;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BloodContainerSpecialRenderer implements SpecialModelRenderer<SimpleFluidContent> {

    @Override
    public void render(@Nullable SimpleFluidContent fluid, @NotNull ItemDisplayContext context, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, boolean hasFoil) {
        if (fluid != null && !fluid.isEmpty()) {
            Material material = null;
            if (fluid.is(ModFluids.BLOOD)) {
                material = ModMaterials.BLOOD_MATERIAL;
            } else if (fluid.is(ModFluids.IMPURE_BLOOD)) {
                material = ModMaterials.IMPURE_BLOOD_MATERIAL;
            }
            if (material != null) {
                var filled = Math.clamp(fluid.getAmount() / (float) BloodContainerBlockEntity.CAPACITY, 0f, 1f);
                stack.pushPose();
                stack.translate(0.5, 1/16f, 0.5);
                stack.scale(10f/16f,14f/16f,10f/16f);
                VertexConsumer consumer = material.buffer(bufferSource, RenderType::entityCutout);
                VertexUtils.addCube(consumer, stack, 1, filled, combinedLight, combinedOverlay, -1);
                stack.popPose();
            }
        }
    }

    @Override
    public @Nullable SimpleFluidContent extractArgument(ItemStack stack) {
        return stack.get(ModDataComponents.BLOOD_CONTAINER.get());
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @NotNull SpecialModelRenderer<?> bake(@NotNull EntityModelSet modelSet) {
            return new BloodContainerSpecialRenderer();
        }

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
