package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SupporterBasedRenderer<TEntity extends Mob, TRenderState extends AvatarLikeRenderState> extends DualBipedRenderer<TEntity, TRenderState, ClothedModel<TRenderState>> {

    private final Map<String, PlayerSkin> textures;
    private final PlayerSkin fallback;

    public SupporterBasedRenderer(EntityRendererProvider.Context context, PlayerSkin fallback) {
        super(context,new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false),new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
        this.addLayer(new ArmorLayer<HumanoidModel<TRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, false)),
                context.getEquipmentRenderer()));
        this.textures = VampirismModClient.services().playerSkinHelper().getSkins();
        this.fallback = fallback;
    }

    @Override
    protected void submitNameDisplay(TRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.distanceToCameraSq <= 256) {
            super.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    public void extractRenderState(TEntity entity, TRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        extractSupporter(entity, state, entity.getData(ModAttachments.SUPPORTER));
    }

    @MustBeInvokedByOverriders
    protected void extractSupporter(TEntity entity, TRenderState state, Supporter supporter) {
        state.skin = this.textures.getOrDefault(supporter.player(), this.fallback);
    }

}
