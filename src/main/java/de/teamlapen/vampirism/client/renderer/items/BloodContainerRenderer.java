package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.teamlapen.lib.client.renderer.VertexUtils;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class BloodContainerRenderer implements SpecialModelRenderer<SimpleFluidContent> {

    public static final ResourceLocation ID = VResourceLocation.mod("blood_container");

    @Override
    public void submit(@Nullable SimpleFluidContent argument, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        VertexUtils.renderFluidTank(
                argument != null ? argument.copy() : null,
                BloodContainerBlockEntity.CAPACITY,
                new Vec3(8 / 16f, 1 / 16f, 8 / 16f),
                new Vec3(10 / 16f, 13.8 / 16f, 10 / 16f),
                0.85f,
                poseStack,
                nodeCollector,
                packedLight,
                packedOverlay
        );
    }

    @Override
    public void getExtents(Set<Vector3f> output) {

    }

    @Override
    public @Nullable SimpleFluidContent extractArgument(ItemStack stack) {
        return stack.get(ModDataComponents.BLOOD_CONTAINER.get());
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @NotNull SpecialModelRenderer<?> bake(@NotNull BakingContext context) {
            return new BloodContainerRenderer();
        }

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    public static class BloodContainerRenderState extends BlockEntityRenderState {
        public FluidStack stack;
    }
}
