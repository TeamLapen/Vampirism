package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.models.entities.VillagerWithArmsModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.villager.Villager;
import org.jetbrains.annotations.NotNull;

public class HunterVillagerRenderer extends MobRenderer<Villager, VillagerRenderState, VillagerWithArmsModel> {

    private static final Identifier texture = VResourceLocation.mc("textures/entity/villager/villager.png");

    public HunterVillagerRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new VillagerWithArmsModel(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS)), 0.5f);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
        this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager",
                new VillagerWithArmsModel(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS)),
                new VillagerWithArmsModel(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS))));
        this.addLayer(new CrossedArmsItemLayer<>(this));

    }

    @NotNull
    @Override
    public Identifier getTextureLocation(@NotNull VillagerRenderState villagerEntity) {
        return texture;
    }

    @Override
    protected void scale(VillagerRenderState p_362272_, @NotNull PoseStack poseStack) {
        float s = 0.9375F;
        if (p_362272_.isBaby) {
            s = (float) ((double) s * 0.5D);
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        poseStack.scale(s, s, s);
    }

    @Override
    public @NotNull VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }

    @Override
    public void extractRenderState(@NotNull Villager entity, @NotNull VillagerRenderState state, float p_361157_) {
        super.extractRenderState(entity, state, p_361157_);
        HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, this.itemModelResolver);
        state.isUnhappy = entity.getUnhappyCounter() > 0;
        state.villagerData = entity.getVillagerData();
        state.setRenderData(ModEntityRenderStates.ATTACK_TIME, entity.getAttackAnim(p_361157_));
        state.setRenderData(ModEntityRenderStates.ATTACK_ARM, entity.swingingArm == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite());
    }
}
