package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.tileentity.AltarInspirationTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Extends the basic (JSON) baked altar inspiration model, by the textured model that fits to the fluid level
 */
@OnlyIn(Dist.CLIENT)
public class BakedAltarInspirationModel implements IDynamicBakedModel {

    public static final int FLUID_LEVELS = 10;

    /**
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)} )}
     */
    public static final IBakedModel[] FLUID_MODELS = new IBakedModel[FLUID_LEVELS];


    private final IBakedModel baseModel;

    public BakedAltarInspirationModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return baseModel.getOverrides();
    }


    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new LinkedList<>(baseModel.getQuads(state, side, rand));
        Integer level = extraData.getData(AltarInspirationTileEntity.FLUID_LEVEL_PROP);
        if (level != null && level > 0 && level <= FLUID_LEVELS) {
            quads.addAll(FLUID_MODELS[level - 1].getQuads(state, side, rand));
        }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return baseModel.isBuiltInRenderer();
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return baseModel.func_230044_c_();
    }

}
