package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)} )}
     */
    public static final IBakedModel[] BLOOD_FLUID_MODELS = new IBakedModel[FLUID_LEVELS];
    public static final IBakedModel[] IMPURE_BLOOD_FLUID_MODELS = new IBakedModel[FLUID_LEVELS];

    private final static ItemOverrideList overrideList = new CustomItemOverride();


    private final IBakedModel baseModel;
    private boolean impure;
    private int fluidLevel = 0;
    private boolean item;

    public BakedBloodContainerModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    public BakedBloodContainerModel(IBakedModel baseModel, FluidStack stack) {
        this.baseModel = baseModel;
        this.impure = stack.getFluid().equals(ModFluids.IMPURE_BLOOD.get());
        this.fluidLevel = MathHelper.clamp(stack.getAmount() / BloodContainerTileEntity.LEVEL_AMOUNT, 1, FLUID_LEVELS) - 1;
        item = true;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseModel.getParticleIcon();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }

    @Nonnull
    @Override
    public ItemCameraTransforms getTransforms() {
        return baseModel.getTransforms();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new LinkedList<>(baseModel.getQuads(state, side, rand));

        if (!item) {
            Integer level = extraData.getData(BloodContainerTileEntity.FLUID_LEVEL_PROP);
            Boolean impure = extraData.getData(BloodContainerTileEntity.FLUID_IMPURE);
            if (impure != null && level != null && level > 0 && level <= FLUID_LEVELS) {
                quads.addAll((impure ? IMPURE_BLOOD_FLUID_MODELS[level - 1] : BLOOD_FLUID_MODELS[level - 1]).getQuads(state, side, rand));
            }
        } else {
            {
                quads.addAll((impure ? IMPURE_BLOOD_FLUID_MODELS[fluidLevel] : BLOOD_FLUID_MODELS[fluidLevel]).getQuads(state, side, rand));
            }
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

    private static class CustomItemOverride extends ItemOverrideList {

        CustomItemOverride() {
            super();
        }

        @Override
        public IBakedModel resolve/*getModelWithOverrides*/(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, ClientWorld world, LivingEntity entity) {
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
