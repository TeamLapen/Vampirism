package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.entity.CrossbowArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class CrossbowArrowRenderer extends ArrowRenderer<CrossbowArrowEntity, ArrowRenderState> {

    private static final Identifier RES_ARROW = VIdentifier.mc("textures/entity/projectiles/arrow.png");

    public CrossbowArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Identifier getTextureLocation(ArrowRenderState state) {
        return RES_ARROW;
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }
}
