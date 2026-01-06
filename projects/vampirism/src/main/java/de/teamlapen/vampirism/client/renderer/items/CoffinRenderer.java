package de.teamlapen.vampirism.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModModels;
import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

public class CoffinRenderer implements SpecialModelRenderer<CoffinBlock> {

    public static final Identifier ID = VIdentifier.mod("coffin");
    private final BlockStateModel[] models;

    public CoffinRenderer(BlockStateModel[] models) {
        this.models = models;
    }

    @Override
    public void submit(@Nullable CoffinBlock argument, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        if (argument == null) return;
        nodeCollector.submitBlockModel(poseStack, RenderTypes.solidMovingBlock(), this.models[argument.getColor().getId()], 1, 1, 1, packedLight, packedOverlay, outlineColor);
    }


    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {

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
        public @NotNull SpecialModelRenderer<?> bake(@NotNull BakingContext context) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            var standaloneModelKeyStream = ModModels.COFFIN_KEYS.row(ModModels.CoffinType.BOTTOM).entrySet().stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().getId()))
                    .map(Map.Entry::getValue)
                    .map(modelManager::getStandaloneModel)
                    .toArray(BlockStateModel[]::new);
            return new CoffinRenderer(standaloneModelKeyStream);
        }

        @Override
        public @NotNull MapCodec<? extends Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
