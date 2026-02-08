package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.VillagerWithArmsModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.ItemInVillagerHandLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class HunterVillagerRenderer extends MobRenderer<Villager, VillagerRenderState, VillagerWithArmsModel>{

    private static final Identifier texture = VIdentifier.mc("textures/entity/villager/villager.png");

    public HunterVillagerRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new VillagerWithArmsModel(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS)), 0.5f);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
        this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager",
                new VillagerWithArmsModel(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS)),
                new VillagerWithArmsModel(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS))));
        this.addLayer(new ItemInVillagerHandLayer<>(this));
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
        itemModelResolver.updateForLiving(state.heldItem, entity.getMainHandItem(), entity.getMainArm() == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
        state.isUnhappy = entity.getUnhappyCounter() > 0;
        state.villagerData = entity.getVillagerData();

        //Attention RenderState extensions are reset almost directly afterward in RenderStateExtensions.onUpdateEntityRenderState, so no point in setRenderData
        //Thus the above is moved to {@link ModEntityRenderStates}
        //The alternative would be to create a subclass of {@link net.minecraft.client.renderer.entity.state.VillagerRenderState}, but this causes issues as {@link VillagerModel} is hardcoded to {@link VillagerRenderState} and not subclasses
        //        state.setRenderData(ModEntityRenderStates.ATTACK_TIME, entity.getAttackAnim(p_361157_));
        //        state.setRenderData(ModEntityRenderStates.ATTACK_ARM, entity.swingingArm == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite());
    }
}
