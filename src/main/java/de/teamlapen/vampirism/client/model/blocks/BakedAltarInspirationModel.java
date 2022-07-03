package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.blockentity.AltarInspirationBlockEntity;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;


/**
 * Extends the basic (JSON) baked altar inspiration model, by the textured model that fits to the fluid level
 */
@SuppressWarnings("ClassCanBeRecord")
@OnlyIn(Dist.CLIENT)
public class BakedAltarInspirationModel implements IDynamicBakedModel {

    public static final int FLUID_LEVELS = 10;

    /**
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)})
     */
    public static final BakedModel[] FLUID_MODELS = new BakedModel[FLUID_LEVELS];


    private final BakedModel baseModel;

    public BakedAltarInspirationModel(BakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseModel.getParticleIcon();
    }

    @Nonnull
    @Override
    public ItemOverrides getOverrides() {
        return baseModel.getOverrides();
    }

    @Nonnull
    @Override
    public ItemTransforms getTransforms() {
        return baseModel.getTransforms();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new LinkedList<>(baseModel.getQuads(state, side, rand));
        Integer level = extraData.getData(AltarInspirationBlockEntity.FLUID_LEVEL_PROP);
        if (level != null && level > 0 && level <= FLUID_LEVELS) {
            quads.addAll(FLUID_MODELS[level - 1].getQuads(state, side, rand));
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

}
