package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.client.core.ClientEventHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Extends the basic (JSON) baked altar inspiration model, by the textured model that fits to the fluid level
 */
@OnlyIn(Dist.CLIENT)
public class BakedAltarInspirationModel implements IBakedModel {

    public static final int FLUID_LEVELS = 10;

    /**
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)} )}
     */
    public static final IBakedModel[] FLUID_MODELS = new IBakedModel[FLUID_LEVELS];


    private final IBakedModel baseModel;
    private String fluidNameItem;
    private int fluidLevelItem;

    public BakedAltarInspirationModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }


    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, Random rand) {
        List<BakedQuad> quads = new LinkedList<>();

        try {
            int fluidLevel = state.getFluidState().getLevel();

            quads.addAll(baseModel.getQuads(state, side, rand));
            if (fluidLevel > 0 && fluidLevel <= FLUID_LEVELS) {
                quads.addAll(FLUID_MODELS[fluidLevel - 1].getQuads(state, side, rand));
            }
        } catch (NullPointerException e) {
            //Occurs when the block is destroyed since the it is not the correct extended block state
            //TODO remove when forge is fixed
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

}
