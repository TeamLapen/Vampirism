package de.teamlapen.vampirism.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.teamlapen.lib.lib.client.VertexUtils;
import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.core.ModDataComponents;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BloodContainerSpecialRenderer implements SpecialModelRenderer<SimpleFluidContent> {

    @Override
    public void render(@Nullable SimpleFluidContent fluid, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        if (fluid != null && !fluid.isEmpty()) {
            VertexUtils.renderFluidTank(
                    fluid.copy(),
                    BloodContainerBlockEntity.CAPACITY,
                    new Vec3(8 / 16f, 1 / 16f, 8 / 16f),
                    new Vec3(10 / 16f,13.8 / 16f,10 / 16f),
                    0.85f,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay
            );
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
