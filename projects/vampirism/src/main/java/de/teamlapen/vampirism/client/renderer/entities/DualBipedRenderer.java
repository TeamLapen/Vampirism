package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.common.util.TextureComparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;


public abstract class DualBipedRenderer<TEntity extends Mob, TRenderState extends AvatarLikeRenderState, TModel extends HumanoidModel<TRenderState>> extends HumanoidMobRenderer<TEntity, TRenderState, TModel> {
    protected final TModel wideModel;
    protected final TModel tallModel;

    public DualBipedRenderer(EntityRendererProvider.Context context, TModel modelWide, TModel modelSlim, float shadowSize) {
        super(context, modelWide, shadowSize);
        this.wideModel = modelWide;
        this.tallModel = modelSlim;
    }

    @Override
    public Identifier getTextureLocation(TRenderState renderState) {
        return determineSkin(renderState).body().texturePath();
    }

    @Override
    public void submit(TRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        this.model = this.adultModel = provideModel(renderState);
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
    }

    protected TModel provideModel(TRenderState renderState) {
        return switch (renderState.skin.model()) {
            case SLIM -> tallModel;
            case WIDE -> wideModel;
        };
    }

    /**
     * @return Sets of texture resource location and model selecting boolean (true->b, false ->a)
     */
    protected PlayerSkin determineSkin(TRenderState entity) {
        return entity.skin;
    }

    /**
     * @return Array of texture and slim status
     */
    protected PlayerSkin[] separateSlimTextures(Stream<Identifier> set) {
        return set.map(r -> {
            PlayerModelType b = r.getPath().endsWith("slim.png") ? PlayerModelType.SLIM : PlayerModelType.WIDE;
            return new PlayerSkin(new ClientAsset.ResourceTexture(r, r), null, null, b, false);
        }).sorted(alphaNumericComparator()).toArray(PlayerSkin[]::new);
    }

    /**
     * Gather all available textures (.png) in the given directory and in MODID namespace
     *
     * @param dirPath  relative assets' path (no namespace)
     * @param required whether to throw an illegal state exception if none found
     * @return Array of texture and slim status
     */
    protected PlayerSkin[] gatherTextures(String dirPath, boolean required) {
        Collection<Identifier> hunterTextures = new ArrayList<>(Minecraft.getInstance().getResourceManager().listResources(dirPath, s -> s.getPath().endsWith(".png")).keySet());
        PlayerSkin[] textures = separateSlimTextures(hunterTextures.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())));
        if (textures.length == 0 && required) {
            throw new IllegalStateException("Must have at least one hunter texture: " + REFERENCE.MODID + ":" + dirPath + "/texture.png");
        }
        return textures;
    }

    protected Comparator<PlayerSkin> alphaNumericComparator() {
        return (o1, o2) -> TextureComparator.alphaNumericComparator().compare(o1.body().texturePath(), o2.body().texturePath());
    }

    protected class ArmorLayer<TArmorModel extends HumanoidModel<TRenderState>> extends HumanoidArmorLayer<TRenderState, TModel, TArmorModel> {

        private final ArmorModelSet<TArmorModel> slimModelSet;
        private final ArmorModelSet<TArmorModel> wideModelSet;

        public ArmorLayer(RenderLayerParent<TRenderState, TModel> renderer, ArmorModelSet<TArmorModel> slimModelSet, ArmorModelSet<TArmorModel> wideModelSet, EquipmentLayerRenderer equipmentRenderer) {
            super(renderer, slimModelSet, equipmentRenderer);
            this.slimModelSet = slimModelSet;
            this.wideModelSet = wideModelSet;
        }

        @Override
        public TArmorModel getArmorModel(TRenderState renderState, EquipmentSlot slot) {
            PlayerSkin b = determineSkin(renderState);
            return (switch (b.model()) {
                case SLIM -> slimModelSet;
                case WIDE -> wideModelSet;
            }).get(slot);
        }
    }

}
