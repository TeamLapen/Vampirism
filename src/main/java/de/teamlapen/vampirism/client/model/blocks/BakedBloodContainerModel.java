package de.teamlapen.vampirism.client.model.blocks;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Extends the basic (JSON) baked blood container model, by the textured model that fits to the specified fluid and fluid level
 */
public class BakedBloodContainerModel implements IBakedModel {

    private final IBakedModel baseModel;
    private final String fluidName;
    private final int fluidLevel;

    public BakedBloodContainerModel(IBakedModel baseModel, String fluidName, int fluidLevel) {
        this.baseModel = baseModel;
        this.fluidName = fluidName;
        this.fluidLevel = fluidLevel;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing) {
        List<BakedQuad> faceQuads = new LinkedList<>();

        faceQuads.addAll(baseModel.getFaceQuads(facing));

        if (fluidLevel > 0 && fluidLevel <= BloodContainerModelFactory.FLUID_LEVELS) {
            HashMap<String, IBakedModel> fluidModels = BloodContainerModelFactory.FLUID_MODELS[fluidLevel - 1];

            if (fluidModels.containsKey(fluidName)) {
                faceQuads.addAll(fluidModels.get(fluidName).getFaceQuads(facing));
            }
        }
        return faceQuads;
    }


    @Override
    public List<BakedQuad> getGeneralQuads() {
        List<BakedQuad> generalQuads = new LinkedList<>();
        generalQuads.addAll(baseModel.getGeneralQuads());
        if (fluidLevel > 0 && fluidLevel <= BloodContainerModelFactory.FLUID_LEVELS) {
            HashMap<String, IBakedModel> fluidModels = BloodContainerModelFactory.FLUID_MODELS[fluidLevel - 1];

            if (fluidModels.containsKey(fluidName)) {
                // The fluid model needs a separate culling logic from the rest of the tank,
                // because the top of the fluid is supposed to be visible if the tank block
                // above is empty. (getGeneralQuads() handles quads that don't have a cullface
                // annotation in the .json)

                generalQuads.addAll(fluidModels.get(fluidName).getGeneralQuads());
            }
        }


        return generalQuads;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
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
