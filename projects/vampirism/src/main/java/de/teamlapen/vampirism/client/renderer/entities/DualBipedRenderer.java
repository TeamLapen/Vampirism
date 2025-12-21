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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;


public abstract class DualBipedRenderer<T extends Mob, S extends AvatarLikeRenderState, M extends HumanoidModel<S>> extends HumanoidMobRenderer<T, S, M> {
    private final @NotNull M modelA;
    private final M modelB;

    private PlayerSkin playerSkin;

    public DualBipedRenderer(EntityRendererProvider.@NotNull Context context, @NotNull M modelBipedInA, M modelBipedInB, float shadowSize) {
        super(context, modelBipedInA, shadowSize);
        this.modelA = modelBipedInA;
        this.modelB = modelBipedInB;
    }

    @Override
    public @NotNull Identifier getTextureLocation(S renderState) {
        return renderState.skin.body().texturePath();
    }

    @Override
    public void submit(S renderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, @NotNull CameraRenderState cameraRenderState) {
        this.model = switch (renderState.skin.model()) {
            case SLIM -> modelB;
            case WIDE -> modelA;
        };
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
    }

    /**
     * @return Sets of texture resource location and model selecting boolean (true->b, false ->a)
     */
    protected abstract PlayerSkin determineTextureAndModel(S entity);

    /**
     * @return Array of texture and slim status
     */
    protected @NotNull PlayerSkin[] separateSlimTextures(@NotNull Stream<Identifier> set) {
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
    protected @NotNull PlayerSkin[] gatherTextures(@NotNull String dirPath, boolean required) {
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

    protected class ArmorLayer<A extends HumanoidModel<S>> extends HumanoidArmorLayer<S, M, A> {

        private final ArmorModelSet<A> slimModelSet;
        private final ArmorModelSet<A> wideModelSet;

        public ArmorLayer(RenderLayerParent<S, M> renderer, ArmorModelSet<A> slimModelSet, ArmorModelSet<A> wideModelSet, EquipmentLayerRenderer equipmentRenderer) {
            super(renderer, slimModelSet, equipmentRenderer);
            this.slimModelSet = slimModelSet;
            this.wideModelSet = wideModelSet;
        }

        @Override
        public @NotNull A getArmorModel(@NotNull S renderState, @NotNull EquipmentSlot slot) {
            PlayerSkin b = determineTextureAndModel(renderState);
            return (switch (b.model()) {
                case SLIM -> slimModelSet;
                case WIDE -> wideModelSet;
            }).get(slot);
        }
    }

}
