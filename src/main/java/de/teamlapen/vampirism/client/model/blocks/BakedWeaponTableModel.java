package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
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

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Extends the basic weapon table model, by a variable lava fluid level
 */
@OnlyIn(Dist.CLIENT)
public class BakedWeaponTableModel implements IBakedModel {

    public static final int FLUID_LEVELS = 5;

    /**
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)} )}
     */
    public static final IBakedModel[][] FLUID_MODELS = new IBakedModel[FLUID_LEVELS][4];

    private final IBakedModel baseModel;

    public BakedWeaponTableModel(IBakedModel baseModel) {
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

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        List<BakedQuad> quads = new LinkedList<>();

        int fluidLevel = state == null ? 0 : state.get(WeaponTableBlock.LAVA);

        quads.addAll(baseModel.getQuads(state, side, rand));
        if (fluidLevel > 0 && fluidLevel <= FLUID_LEVELS) {
            quads.addAll(FLUID_MODELS[fluidLevel - 1][state == null ? 0 : state.get(WeaponTableBlock.FACING).getHorizontalIndex()].getQuads(state, side, rand));
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
