package de.teamlapen.vampirism.client.model.blocks;

import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.core.ModFluids;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Extends the basic (JSON) baked blood container model, by the textured model that fits to the specified fluid and fluid level
 */
@OnlyIn(Dist.CLIENT)
public class BakedBloodContainerModel implements IDynamicBakedModel {

    public static final int FLUID_LEVELS = 14;

    /**
     * Stores a fluid level -> fluid model array
     * Filled when the fluid json model is loaded (in {@link ClientEventHandler#onModelBakeEvent(net.minecraftforge.client.event.ModelEvent.BakingCompleted)})
     */
    public static final BakedModel[] BLOOD_FLUID_MODELS = new BakedModel[FLUID_LEVELS];
    public static final BakedModel[] IMPURE_BLOOD_FLUID_MODELS = new BakedModel[FLUID_LEVELS];

    private final static ItemOverrides overrideList = new CustomItemOverride();


    private final BakedModel baseModel;
    private boolean impure;
    private int fluidLevel = 0;
    private boolean item;

    public BakedBloodContainerModel(BakedModel baseModel) {
        this.baseModel = baseModel;
    }

    public BakedBloodContainerModel(BakedModel baseModel, FluidStack stack) {
        this.baseModel = baseModel;
        this.impure = stack.getFluid().equals(ModFluids.IMPURE_BLOOD.get());
        this.fluidLevel = Mth.clamp(stack.getAmount() / BloodContainerBlockEntity.LEVEL_AMOUNT, 1, FLUID_LEVELS) - 1;
        item = true;
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseModel.getParticleIcon();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return baseModel.getTransforms();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @org.jetbrains.annotations.Nullable RenderType renderType) {
        List<BakedQuad> quads = new LinkedList<>(baseModel.getQuads(state, side, rand));

        if (!item) {
            Integer level = extraData.get(BloodContainerBlockEntity.FLUID_LEVEL_PROP);
            Boolean impure = extraData.get(BloodContainerBlockEntity.FLUID_IMPURE);
            if (impure != null && level != null && level > 0 && level <= FLUID_LEVELS) {
                quads.addAll((impure ? IMPURE_BLOOD_FLUID_MODELS[level - 1] : BLOOD_FLUID_MODELS[level - 1]).getQuads(state, side, rand, ModelData.EMPTY, renderType));
            }
        } else {
            {
                quads.addAll((impure ? IMPURE_BLOOD_FLUID_MODELS[fluidLevel] : BLOOD_FLUID_MODELS[fluidLevel]).getQuads(state, side, rand, ModelData.EMPTY, renderType));
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

    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return baseModel.getRenderTypes(state, rand, data);
    }

    private static class CustomItemOverride extends ItemOverrides {

        CustomItemOverride() {
            super();
        }

        @Override
        public BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack, ClientLevel world, LivingEntity entity, int p) {
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
