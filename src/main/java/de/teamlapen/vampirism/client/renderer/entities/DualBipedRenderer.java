package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.mixin.client.accessor.HumanoidArmorLayerAccessor;
import de.teamlapen.vampirism.common.util.TextureComparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;


public abstract class DualBipedRenderer<T extends Mob, S extends PlayerRenderState, M extends HumanoidModel<S>> extends HumanoidMobRenderer<T, S, M> {
    private final @NotNull M modelA;
    private final M modelB;

    private PlayerSkin playerSkin;

    public DualBipedRenderer(EntityRendererProvider.@NotNull Context context, @NotNull M modelBipedInA, M modelBipedInB, float shadowSize) {
        super(context, modelBipedInA, shadowSize);
        this.modelA = modelBipedInA;
        this.modelB = modelBipedInB;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(S renderState) {
        return renderState.skin.texture();
    }

    @Override
    public final void render(@NotNull S entityIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        this.model = switch (entityIn.skin.model()) {
            case SLIM -> modelB;
            case WIDE -> modelA;
        };
        super.render(entityIn, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * @return Sets of texture resource location and model selecting boolean (true->b, false ->a)
     */
    protected abstract PlayerSkin determineTextureAndModel(S entity);

    /**
     * @return Array of texture and slim status
     */
    protected @NotNull PlayerSkin[] separateSlimTextures(@NotNull Stream<ResourceLocation> set) {
        return set.map(r -> {
            PlayerSkin.Model b = r.getPath().endsWith("slim.png") ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE;
            return new PlayerSkin(r, null, null, null, b, false);
        }).sorted(alphaNumericComparator()).toArray(PlayerSkin[]::new);
    }

    /**
     * Gather all available textures (.png) in the given directory and in MODID namespace
     *
     * @param dirPath  relative assets' path (no namespace)
     * @param required whether to throw an illegal state exception if none found
     * @return Array of texture and slim status
     */
    protected @NotNull PlayerSkin[] gatherTextures(@NotNull String dirPath, boolean required) {
        Collection<ResourceLocation> hunterTextures = new ArrayList<>(Minecraft.getInstance().getResourceManager().listResources(dirPath, s -> s.getPath().endsWith(".png")).keySet());
        PlayerSkin[] textures = separateSlimTextures(hunterTextures.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())));
        if (textures.length == 0 && required) {
            throw new IllegalStateException("Must have at least one hunter texture: " + REFERENCE.MODID + ":" + dirPath + "/texture.png");
        }
        return textures;
    }

    protected Comparator<PlayerSkin> alphaNumericComparator() {
        return (o1, o2) -> TextureComparator.alphaNumericComparator().compare(o1.texture(), o2.texture());
    }

    protected class ArmorLayer<A extends HumanoidModel<S>> extends HumanoidArmorLayer<S, M, A> {

        private final A pInnerModel;
        private final A pInnerModelSlim;
        private final A pOuterModel;
        private final A pOuterModelSlim;

        public ArmorLayer(RenderLayerParent<S, M> pRenderer, A pInnerModel, A pInnerModelSlim, A pOuterModel, A pOuterModelSlim, EquipmentLayerRenderer pModelManager) {
            super(pRenderer, pInnerModel, pOuterModel, pModelManager);
            this.pInnerModel = pInnerModel;
            this.pInnerModelSlim = pInnerModelSlim;
            this.pOuterModel = pOuterModel;
            this.pOuterModelSlim = pOuterModelSlim;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull S pLivingEntity, float f1, float f2) {
            PlayerSkin b = determineTextureAndModel(pLivingEntity);

            A innerModel = switch (b.model()) {
                case SLIM -> pInnerModelSlim;
                case WIDE -> pInnerModel;
            };
            A outerModel = switch (b.model()) {
                case SLIM -> pOuterModelSlim;
                case WIDE -> pOuterModel;
            };

            ((HumanoidArmorLayerAccessor<S, A>) this).invokeRenderArmorPiece(pMatrixStack, pBuffer, pLivingEntity.chestEquipment, EquipmentSlot.CHEST, pPackedLight, this.getArmorModel(EquipmentSlot.CHEST, innerModel, outerModel));
            ((HumanoidArmorLayerAccessor<S, A>) this).invokeRenderArmorPiece(pMatrixStack, pBuffer, pLivingEntity.legsEquipment, EquipmentSlot.LEGS, pPackedLight, this.getArmorModel(EquipmentSlot.LEGS, innerModel, outerModel));
            ((HumanoidArmorLayerAccessor<S, A>) this).invokeRenderArmorPiece(pMatrixStack, pBuffer, pLivingEntity.feetEquipment, EquipmentSlot.FEET, pPackedLight, this.getArmorModel(EquipmentSlot.FEET, innerModel, outerModel));
            ((HumanoidArmorLayerAccessor<S, A>) this).invokeRenderArmorPiece(pMatrixStack, pBuffer, pLivingEntity.headEquipment, EquipmentSlot.HEAD, pPackedLight, this.getArmorModel(EquipmentSlot.HEAD, innerModel, outerModel));
        }

        private A getArmorModel(EquipmentSlot slot, A innerModel, A outerModel) {
            //noinspection rawtypes
            return ((HumanoidArmorLayerAccessor) this).invokeUsesInnerModel(slot) ? innerModel : outerModel;
        }
    }

}
