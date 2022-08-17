package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BasicVampireRenderer extends HumanoidMobRenderer<BasicVampireEntity, HumanoidModel<BasicVampireEntity>> {

    private final ResourceLocation[] textures;

    public BasicVampireRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().listResources("textures/entity/vampire", s -> s.getPath().endsWith(".png")).keySet().stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(BasicVampireEntity entity) {
        return getVampireTexture(entity.getEntityTextureType());
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }
}
