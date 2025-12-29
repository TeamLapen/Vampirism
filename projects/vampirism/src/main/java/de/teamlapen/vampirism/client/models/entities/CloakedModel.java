package de.teamlapen.vampirism.client.models.entities;

import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * ModelBiped with a cloak
 */
public class CloakedModel<T extends AvatarLikeRenderState> extends ClothedModel<T> {
    private static final String CLOAK = "cloak";
    public final @NotNull ModelPart bipedCloak;

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition var2 = PlayerModel.createMesh(CubeDeformation.NONE, false);
        PartDefinition var3 = var2.getRoot();
        var3.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-7, 0, 2f, 14, 20, 1), PartPose.offset(0, 0, 2));
        return LayerDefinition.create(var2, 64,32);
    }

    public CloakedModel(@NotNull ModelPart part, boolean smallArms) {
        super(part, smallArms);
        bipedCloak = part.getChild(CLOAK);
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        bipedCloak.visible = visible;
    }


    @Override
    public void setupAnim(@NotNull T state) {
        super.setupAnim(state);
        float f = 1.0F;
        if (state.fallFlyingTimeInTicks > 4) {
            f = state.speedValue;
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }
        if (state.isCrouching) {
            this.bipedCloak.y = 2.0F;
        } else {
            this.bipedCloak.y = 0.0F;
        }
        this.bipedCloak.xRot = Math.max(Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed / f, Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed / f);
    }

}
