package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.blocks.BlockBloodContainer;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;

/**
 * Factory for blood container models returns {@link BakedBloodContainerModel} with the corresponding fluid and fluid level
 * Used as item and block.
 * <p>
 * Rendering heavily inspired by @Zarathul's  <a href="https://github.com/Zarathul/simplefluidtanks">https://github.com/Zarathul/simplefluidtanks</a>
 * <p>
 * By the way: If a block is rendered in the {@link EnumWorldBlockLayer#TRANSLUCENT} the item is renderd somehow transparent as well
 */
public class BloodContainerModelFactory implements ISmartBlockModel, ISmartItemModel {

    public static final int FLUID_LEVELS = 14;
    /**
     * Stores a FluidName-> Baked Fluid model map for each possible fluid level
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(ModelBakeEvent)}
     */
    public static final HashMap<String, IBakedModel>[] FLUID_MODELS = new HashMap[FLUID_LEVELS];

    static {
        for (int x = 0; x < FLUID_LEVELS; x++) {
            FLUID_MODELS[x] = new HashMap<>();
        }
    }

    private final IBakedModel baseModel;

    public BloodContainerModelFactory(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }


    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    /**
     * Returns a new baked model based on  the given block states property values
     *
     * @param state
     * @return
     */
    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        return new BakedBloodContainerModel(baseModel, extendedState.getValue(BlockBloodContainer.FLUID_NAME), extendedState.getValue(BlockBloodContainer.FLUID_LEVEL));
    }

    /**
     * Return a new baked model based on the given stack's nbt
     *
     * @param stack
     * @return
     */
    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fluid")) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("fluid"));
            if (fluid != null) {
                return new BakedBloodContainerModel(baseModel, fluid.getFluid().getName(), fluid.amount / TileBloodContainer.LEVEL_AMOUNT);
            }
        }
        return new BakedBloodContainerModel(baseModel, "", 0);
    }

    @Override
    public boolean isAmbientOcclusion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBuiltInRenderer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isGui3d() {
        throw new UnsupportedOperationException();
    }
}
