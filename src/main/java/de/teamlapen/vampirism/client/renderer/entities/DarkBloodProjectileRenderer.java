package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.common.entity.DarkBloodProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.NotNull;

public class DarkBloodProjectileRenderer extends EntityRenderer<DarkBloodProjectileEntity, EntityRenderState> {

    public DarkBloodProjectileRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);

    }

    @Override
    public @NotNull EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

}
