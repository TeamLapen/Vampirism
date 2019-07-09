package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Extends the basic (JSON) baked blood container model, by the textured model that fits to the specified fluid and fluid level
 * TODO 1.14 check
 */
@OnlyIn(Dist.CLIENT)
public class BakedBloodContainerModel implements IBakedModel {

    public static final int FLUID_LEVELS = 14;
    /**
     * Stores a FluidName-> Baked Fluid model map for each possible fluid level
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)} )}
     */
    public static final HashMap<String, IBakedModel>[] FLUID_MODELS = new HashMap[FLUID_LEVELS];
    private final static ItemOverrideList overrideList = new CustomItemOverride();

    static {
        for (int x = 0; x < FLUID_LEVELS; x++) {
            FLUID_MODELS[x] = new HashMap<>();
        }
    }


    private final IBakedModel baseModel;
    private String fluidNameItem;
    private int fluidLevelItem;

    public BakedBloodContainerModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    public BakedBloodContainerModel(IBakedModel baseModel, String fluidName, int fluidLevel) {
        this(baseModel);
        this.fluidNameItem = fluidName;
        this.fluidLevelItem = fluidLevel;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        List<BakedQuad> quads = new LinkedList<>();

        try {
            String fluidName = state.getFluidState().getFluid().toString();//TODO name of Fluid
            int fluidLevel = state.getFluidState().getLevel();

            quads.addAll(baseModel.getQuads(state, side, rand));
            if (fluidLevel > 0 && fluidLevel <= FLUID_LEVELS) {
                HashMap<String, IBakedModel> fluidModels = FLUID_MODELS[fluidLevel - 1];

                if (fluidModels.containsKey(fluidName)) {
                    quads.addAll(fluidModels.get(fluidName).getQuads(state, side, rand));
                }
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

    private static class CustomItemOverride extends ItemOverrideList {

        CustomItemOverride() {
            super();
        }

        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
            if (originalModel instanceof BakedBloodContainerModel) {
                if (stack.hasTag() && stack.getTag().contains("fluid")) {
                    FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTag().getCompound("fluid"));
                    if (fluid != null) {

                        float amount = fluid.amount / (float) TileBloodContainer.LEVEL_AMOUNT;

                        return new BakedBloodContainerModel(originalModel, fluid.getFluid().getName(), (amount > 0 && amount < 1) ? 1 : (int) amount);
                    }
                }
            }
            return originalModel;
        }
    }
}
