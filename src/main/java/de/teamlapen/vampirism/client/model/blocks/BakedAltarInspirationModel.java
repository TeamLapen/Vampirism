package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.blockentity.AltarInspirationBlockEntity;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;


/**
 * Extends the basic (JSON) baked altar inspiration model, by the textured model that fits to the fluid level
 */
public class BakedAltarInspirationModel implements IDynamicBakedModel {

    public static final int FLUID_LEVELS = 10;

    /**
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult} )
     */
    public static final BakedModel[] FLUID_MODELS = new BakedModel[FLUID_LEVELS];


    private final BakedModel baseModel;

    public BakedAltarInspirationModel(BakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseModel.getParticleIcon();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return baseModel.getOverrides();
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return baseModel.getTransforms();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @org.jetbrains.annotations.Nullable RenderType renderType) {
        List<BakedQuad> quads = new LinkedList<>(baseModel.getQuads(state, side, rand, ModelData.EMPTY, renderType));
        Integer level = extraData.get(AltarInspirationBlockEntity.FLUID_LEVEL_PROP);
        if (level != null && level > 0 && level <= FLUID_LEVELS) {
            quads.addAll(FLUID_MODELS[level - 1].getQuads(state, side, rand, ModelData.EMPTY, renderType));
        }
        return quads;
    }

    @Override
    public boolean isCustomRenderer() {
        return baseModel.isCustomRenderer();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return baseModel.usesBlockLight();
    }

    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return baseModel.getRenderTypes(state, rand, data);
    }
}
