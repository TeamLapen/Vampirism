package de.teamlapen.vampirism.client.models.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Keep in sync with {@link net.minecraft.client.model.player.PlayerModel}
 */
public class ClothedModel<T extends AvatarLikeRenderState> extends HumanoidModel<T> {
    private static final String LEFT_SLEEVE = "left_sleeve";
    private static final String RIGHT_SLEEVE = "right_sleeve";
    private static final String LEFT_PANTS = "left_pants";
    private static final String RIGHT_PANTS = "right_pants";
    private static final String JACKET = "jacket";
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final boolean slim;

    public ClothedModel(ModelPart root, boolean slim) {
        super(root, RenderTypes::entityTranslucent);
        this.slim = slim;
        this.leftSleeve = this.leftArm.getChild(LEFT_SLEEVE);
        this.rightSleeve = this.rightArm.getChild(RIGHT_SLEEVE);
        this.leftPants = this.leftLeg.getChild(LEFT_PANTS);
        this.rightPants = this.rightLeg.getChild(RIGHT_PANTS);
        this.jacket = this.body.getChild(JACKET);
    }

    @Override
    public void translateToHand(T renderState, HumanoidArm arm, PoseStack poseStack) {
        this.root().translateAndRotate(poseStack);
        ModelPart modelpart = this.getArm(arm);
        if (this.slim) {
            float f = 0.5F * (float)(arm == HumanoidArm.RIGHT ? 1 : -1);
            modelpart.x += f;
            modelpart.translateAndRotate(poseStack);
            modelpart.x -= f;
        } else {
            modelpart.translateAndRotate(poseStack);
        }

    }

}
