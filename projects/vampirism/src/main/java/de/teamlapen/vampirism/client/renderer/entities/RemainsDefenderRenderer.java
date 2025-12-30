package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.RemainsDefenderModel;
import de.teamlapen.vampirism.common.world.entity.RemainsDefenderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RemainsDefenderRenderer extends MobRenderer<RemainsDefenderEntity, RemainsDefenderRenderer.RemainsDefenderRenderState, RemainsDefenderModel> {
    private final Identifier TEX1 = VIdentifier.mod("textures/entity/remains_defender/remains_defender1.png");
    private final Identifier TEX2 = VIdentifier.mod("textures/entity/remains_defender/remains_defender2.png");
    private final Identifier TEX3 = VIdentifier.mod("textures/entity/remains_defender/remains_defender3.png");
    private final Identifier TEX4 = VIdentifier.mod("textures/entity/remains_defender/remains_defender4.png");

    public RemainsDefenderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new RemainsDefenderModel(pContext.bakeLayer(ModEntitiesRender.REMAINS_DEFENDER)), 0f);
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull RemainsDefenderRenderState pEntity) {
        int t = ((int) pEntity.ageInTicks) % 20;
        if (t > 15) {
            return TEX4;
        } else if (t > 10) {
            return TEX3;
        } else if (t > 5) {
            return TEX2;
        } else {
            return TEX1;
        }
    }

    @Override
    public void extractRenderState(RemainsDefenderEntity entity, RemainsDefenderRenderState state, float p_361157_) {
        super.extractRenderState(entity, state, p_361157_);
        state.attachedFace = entity.getAttachFace();
    }

    @Override
    protected void setupRotations(RemainsDefenderRenderState state, PoseStack pMatrixStack, float f1, float f2) {
        pMatrixStack.translate(0, 0.5d, 0);
        pMatrixStack.mulPose(state.attachedFace.getOpposite().getRotation());
        pMatrixStack.translate(0, -0.5, 0);
    }

    @Override
    protected int getBlockLightLevel(RemainsDefenderEntity pEntity, BlockPos pPos) {
        int i = (int) Mth.clampedLerp(0.0F, 15.0F, (float) pEntity.getLightTicksRemaining() / 10.0F);
        return i == 15 ? 15 : Math.max(i, super.getBlockLightLevel(pEntity, pPos));
    }

    @Override
    public @NotNull RemainsDefenderRenderState createRenderState() {
        return new RemainsDefenderRenderState();
    }

    public static class RemainsDefenderRenderState extends LivingEntityRenderState {
        public Direction attachedFace;
    }
}
