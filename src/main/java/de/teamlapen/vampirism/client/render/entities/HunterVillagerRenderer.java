package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.VillagerWithArmsModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class HunterVillagerRenderer extends MobRenderer<Villager, VillagerWithArmsModel<Villager>> {

    private static final ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");

    public HunterVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerWithArmsModel<>(context.bakeLayer(ModEntitiesRender.VILLAGER_WITH_ARMS)), 0.5f);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager"));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull Villager villagerEntity) {
        return texture;
    }

    /**
     * Copied from VillagerRenderer
     */
    @Override
    protected void scale(Villager entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
        float s = 0.9375F;
        if (entity.isBaby()) {
            s = (float) ((double) s * 0.5D);
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        matrixStack.scale(s, s, s);
    }
}
