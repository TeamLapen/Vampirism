package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.entity.CrossbowArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CrossbowArrowRenderer extends ArrowRenderer<CrossbowArrowEntity, ArrowRenderState> {

    private static final ResourceLocation RES_ARROW = VResourceLocation.mc("textures/entity/projectiles/arrow.png");

    public CrossbowArrowRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @Override
    protected @NotNull ResourceLocation getTextureLocation(@NotNull ArrowRenderState state) {
        return RES_ARROW;
    }

    @Override
    public @NotNull ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }
}
