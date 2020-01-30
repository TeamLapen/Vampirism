package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Extends the basic (JSON) baked blood container model, by the textured model that fits to the specified fluid and fluid level
 */
@OnlyIn(Dist.CLIENT)
public class BakedBloodContainerModel implements IDynamicBakedModel {

    public static final int FLUID_LEVELS = 14;
    /**
     * Stores a FluidName-> Baked Fluid model map for each possible fluid level
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)} )}
     */
    public static final HashMap<Fluid, IBakedModel>[] FLUID_MODELS = new HashMap[FLUID_LEVELS];
    private final static ItemOverrideList overrideList = new CustomItemOverride();

    static {
        for (int x = 0; x < FLUID_LEVELS; x++) {
            FLUID_MODELS[x] = new HashMap<>();
        }
    }

    private final IBakedModel baseModel;
    private Fluid fluid;
    private int fluidLevel = 0;
    private boolean item;

    public BakedBloodContainerModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    public BakedBloodContainerModel(IBakedModel baseModel, FluidStack stack) {
        this.baseModel = baseModel;
        this.fluid = stack.getFluid();
        this.fluidLevel = MathHelper.clamp(stack.getAmount() / BloodContainerTileEntity.LEVEL_AMOUNT, 1, FLUID_LEVELS) - 1;
        item = true;
    }

    @Nonnull
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new LinkedList<>(baseModel.getQuads(state, side, rand));

        if (!item) {
            Integer level = extraData.getData(BloodContainerTileEntity.FLUID_LEVEL_PROP);
            Fluid fluid = extraData.getData(BloodContainerTileEntity.FLUID_PROP);
            if (fluid != null && level != null && level > 0 && level <= FLUID_LEVELS) {
                HashMap<Fluid, IBakedModel> fluidModels = FLUID_MODELS[level - 1];
                if (FLUID_MODELS[level - 1].containsKey(fluid)) {
                    quads.addAll(fluidModels.get(fluid).getQuads(state, side, rand));
                }
            }
        } else {
            HashMap<Fluid, IBakedModel> fluidModels = FLUID_MODELS[fluidLevel];
            if (FLUID_MODELS[fluidLevel].containsKey(fluid)) {
                quads.addAll(fluidModels.get(fluid).getQuads(state, side, rand));
            }
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

    private static class CustomItemOverride extends ItemOverrideList {

        CustomItemOverride() {
            super();
        }

        @Override
        public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, World world, LivingEntity entity) {
            if (originalModel instanceof BakedBloodContainerModel) {
                if (stack.hasTag() && stack.getTag().contains("fluid")) {
                    FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTag().getCompound("fluid"));
                    if (!fluid.isEmpty()) {
                        return new BakedBloodContainerModel(originalModel, fluid);
                    }
                }
            }
            return originalModel;
        }
    }
}
