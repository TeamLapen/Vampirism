package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

/**
 * ModelBiped with a cloak
 */
@OnlyIn(Dist.CLIENT)
public class BipedCloakedModel<T extends LivingEntity> extends PlayerModel<T> {
    private static final String CLOAK = "cloak";
    protected final ModelPart bipedCloak;

    public static MeshDefinition createMesh(boolean smallArms) {
        MeshDefinition var2 = PlayerModel.createMesh(CubeDeformation.NONE, smallArms);
        PartDefinition var3 = var2.getRoot();
        var3.addOrReplaceChild(CLOAK, CubeListBuilder.create().texOffs(0, 0).addBox(-7, 0, 0.4f, 14, 20, 1), PartPose.offset(0, 0, 2));
        return var2;
    }

    public BipedCloakedModel(ModelPart part, boolean smallArms) {
        super(part, smallArms);
        bipedCloak = part.getChild(CLOAK);
    }

    public void renderCustomCloak(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn) {
        this.bipedCloak.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        bipedCloak.visible = visible;
    }

    @Override
    public void setupAnim(@NotNull T entity, float f, float f1, float f2, float f3, float f4) {
        super.setupAnim(entity, f, f1, f2, f3, f4);
        if (entity.isCrouching()) {
            this.bipedCloak.y = 2.0F;
        } else {
            this.bipedCloak.y = 0.0F;
        }
    }
}
