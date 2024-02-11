package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.mixin.client.accessor.HumanoidArmorLayerAccessor;
import de.teamlapen.vampirism.util.PlayerModelType;
import de.teamlapen.vampirism.util.TextureComparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;


public abstract class DualBipedRenderer<T extends Mob, M extends HumanoidModel<T>> extends HumanoidMobRenderer<T, M> {
    private final @NotNull M modelA;
    private final M modelB;

    private ResourceLocation currentTexture;

    public DualBipedRenderer(EntityRendererProvider.@NotNull Context context, @NotNull M modelBipedInA, M modelBipedInB, float shadowSize) {
        super(context, modelBipedInA, shadowSize);
        this.modelA = modelBipedInA;
        this.modelB = modelBipedInB;
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull T entity) {
        return currentTexture != null ? currentTexture : DefaultPlayerSkin.getDefaultTexture(); //Steve texture is used as fallback
    }

    @Override
    public final void render(@NotNull T entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        Pair<ResourceLocation, PlayerModelType> b = determineTextureAndModel(entityIn);
        this.currentTexture = b.getLeft();
        this.model = switch (b.getRight()) {
            case SLIM -> modelB;
            case WIDE -> modelA;
        };
        this.renderSelected(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * @return Sets of texture resource location and model selecting boolean (true->b, false ->a)
     */
    protected abstract Pair<ResourceLocation, PlayerModelType> determineTextureAndModel(T entity);

    protected void renderSelected(@NotNull T entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * @return Array of texture and slim status
     */
    protected Pair<ResourceLocation, PlayerModelType> @NotNull [] separateSlimTextures(@NotNull Stream<ResourceLocation> set) {
        return set.map(r -> {
            PlayerModelType b = r.getPath().endsWith("slim.png") ? PlayerModelType.SLIM : PlayerModelType.WIDE;
            return Pair.of(r, b);
        }).sorted(alphaNumericComparator()).toArray((IntFunction<Pair<ResourceLocation, PlayerModelType>[]>) Pair[]::new);
    }

    /**
     * Gather all available textures (.png) in the given directory and in MODID namespace
     *
     * @param dirPath  relative assets' path (no namespace)
     * @param required whether to throw an illegal state exception if none found
     * @return Array of texture and slim status
     */
    protected Pair<ResourceLocation, PlayerModelType> @NotNull [] gatherTextures(@NotNull String dirPath, boolean required) {
        Collection<ResourceLocation> hunterTextures = new ArrayList<>(Minecraft.getInstance().getResourceManager().listResources(dirPath, s -> s.getPath().endsWith(".png")).keySet());
        Pair<ResourceLocation, PlayerModelType>[] textures = separateSlimTextures(hunterTextures.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())));
        if (textures.length == 0 && required) {
            throw new IllegalStateException("Must have at least one hunter texture: " + REFERENCE.MODID + ":" + dirPath + "/texture.png");
        }
        return textures;
    }

    protected Comparator<Pair<ResourceLocation, PlayerModelType>> alphaNumericComparator() {
        return (o1, o2) -> TextureComparator.alphaNumericComparator().compare(o1.getLeft(), o2.getLeft());
    }

    protected class ArmorLayer<A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {

        private final A pInnerModel;
        private final A pInnerModelSlim;
        private final A pOuterModel;
        private final A pOuterModelSlim;

        public ArmorLayer(RenderLayerParent<T, M> pRenderer, A pInnerModel, A pInnerModelSlim, A pOuterModel, A pOuterModelSlim, ModelManager pModelManager) {
            super(pRenderer, pInnerModel, pOuterModel, pModelManager);
            this.pInnerModel = pInnerModel;
            this.pInnerModelSlim = pInnerModelSlim;
            this.pOuterModel = pOuterModel;
            this.pOuterModelSlim = pOuterModelSlim;
        }

        @Override
        public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            Pair<ResourceLocation, PlayerModelType> b = determineTextureAndModel(pLivingEntity);

            A innerModel = switch (b.getRight()) {
                case SLIM -> pInnerModelSlim;
                case WIDE -> pInnerModel;
            };
            A outerModel = switch (b.getRight()) {
                case SLIM -> pOuterModelSlim;
                case WIDE -> pOuterModel;
            };

            ((HumanoidArmorLayerAccessor<T, M, A>) this).invoke_renderArmorPiece(pMatrixStack, pBuffer, pLivingEntity, EquipmentSlot.CHEST, pPackedLight, this.getArmorModel(EquipmentSlot.CHEST, innerModel, outerModel));
            ((HumanoidArmorLayerAccessor<T, M, A>) this).invoke_renderArmorPiece(pMatrixStack, pBuffer, pLivingEntity, EquipmentSlot.LEGS, pPackedLight, this.getArmorModel(EquipmentSlot.LEGS, innerModel, outerModel));
            ((HumanoidArmorLayerAccessor<T, M, A>) this).invoke_renderArmorPiece(pMatrixStack, pBuffer, pLivingEntity, EquipmentSlot.FEET, pPackedLight, this.getArmorModel(EquipmentSlot.FEET, innerModel, outerModel));
            ((HumanoidArmorLayerAccessor<T, M, A>) this).invoke_renderArmorPiece(pMatrixStack, pBuffer, pLivingEntity, EquipmentSlot.HEAD, pPackedLight, this.getArmorModel(EquipmentSlot.HEAD, innerModel, outerModel));
        }

        private A getArmorModel(EquipmentSlot slot, A innerModel, A outerModel) {
            //noinspection rawtypes
            return ((HumanoidArmorLayerAccessor) this).invoke_usesInnerModel(slot) ? innerModel : outerModel;
        }
    }
}
