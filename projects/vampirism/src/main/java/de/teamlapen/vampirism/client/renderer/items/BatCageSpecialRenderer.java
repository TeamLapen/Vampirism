package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.renderer.blockentity.BatCageRenderer;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BatCageSpecialRenderer implements SpecialModelRenderer<CompoundTag> {

    public static final Identifier ID = VIdentifier.mod("bat_cage");

    private final BatCageRenderer renderer;

    public BatCageSpecialRenderer(BatCageRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void submit(@Nullable CompoundTag tag, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        if (tag != null) {
            renderer.renderBat(poseStack, submitNodeCollector, packedLight, Direction.NORTH);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
    }

    @Override
    public @Nullable CompoundTag extractArgument(ItemStack itemStack) {
        return itemStack.get(ModDataComponents.HELD_ENTITY);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<BatCageSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(BatCageSpecialRenderer.Unbaked::new);

        @Override
        public SpecialModelRenderer<?> bake(BakingContext bakingContext) {
            return new BatCageSpecialRenderer(new BatCageRenderer(bakingContext.entityModelSet()));
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
