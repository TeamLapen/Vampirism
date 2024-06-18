package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.GhostModel;
import de.teamlapen.vampirism.entity.GhostEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GhostRenderer extends MobRenderer<GhostEntity, GhostModel> {

    public static final ResourceLocation TEXTURE = VResourceLocation.mod("textures/entity/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GhostModel(pContext.bakeLayer(ModEntitiesRender.GHOST)), 0.1f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GhostEntity pEntity) {
        return TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(@NotNull GhostEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return super.getRenderType(pLivingEntity, pBodyVisible, true, pGlowing);
    }
}
