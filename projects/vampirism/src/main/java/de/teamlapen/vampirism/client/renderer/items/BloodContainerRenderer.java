package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.renderer.VertexUtils;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.blockentity.BloodContainerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class BloodContainerRenderer implements SpecialModelRenderer<SimpleFluidContent> {

    public static final Identifier ID = VIdentifier.mod("blood_container");

    @Override
    public void submit(@Nullable SimpleFluidContent argument, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        VertexUtils.renderFluidTank(
                Minecraft.getInstance().level, Minecraft.getInstance().player != null ? Minecraft.getInstance().player.blockPosition() : BlockPos.ZERO,
                argument != null ? argument.copy() : null,
                BloodContainerBlockEntity.CAPACITY,
                new Vec3(0, 0, 0),
                new Vec3(10 / 16f, 14 / 16f, 10 / 16f),
                poseStack,
                nodeCollector,
                packedLight,
                packedOverlay
        );
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {

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
