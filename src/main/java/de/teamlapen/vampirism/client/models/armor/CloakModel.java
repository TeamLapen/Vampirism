package de.teamlapen.vampirism.client.models.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class CloakModel extends VampirismArmorModel {

    private static final String CLOAK = "cloak";
    private static final String HOLDER = "holder";

    private final ModelPart cloak;
    private final ModelPart holder;

    public CloakModel(ModelPart root) {
        super(root);
        this.cloak = root.getChild(CLOAK);
        this.holder = root.getChild(HOLDER);
    }

    public static LayerDefinition createCloakLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild(
                CLOAK,
                CubeListBuilder.create().texOffs(0, 3)
                        .addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                HOLDER,
                CubeListBuilder.create().texOffs(1, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 10.0F, 3.0F, 0.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    protected @NotNull Iterable<ModelPart> getBodyModels() {
        return ImmutableList.of(cloak, holder);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        super.setupAnim(state);

        float capeLean = state instanceof PlayerRenderState playerState ? playerState.capeLean : 0.0F;
        float capeFlap = state instanceof PlayerRenderState playerState ? playerState.capeLean2 : 0.0F;

        this.cloak.rotateBy(new Quaternionf()
                .rotateX(Math.max(4.0F + capeLean / 3.0F + capeFlap, 1.0F) * (float) Math.PI / 180.0F)
                .rotateY((float) Math.PI)
        );

        cloak.y -= 0.1F;
        cloak.z += 1.95F;

        if (state.isCrouching) {
            cloak.y -= 1.1F;
            cloak.z -= 0.15F;
        }

        holder.xRot -= (float) Math.PI / 2;
        holder.yRot -= (float) Math.PI;
        if (state.isCrouching) holder.xRot -= 50.0F * (float) Math.PI / 180.0F;

        holder.x += 5.0F;
        holder.y -= 0.02F;
        holder.z -= 1.01F;
    }
}
