package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.common.world.entity.DarkBloodProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DarkBloodProjectileRenderer extends EntityRenderer<DarkBloodProjectileEntity, EntityRenderState> {

    public DarkBloodProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);

    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

}
