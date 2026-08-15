package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;

public class AdvancedHunterRenderer<T extends Mob> extends SupporterBasedRenderer<T, AdvancedHunterRenderer.AdvancedHunterRenderState> {

    public AdvancedHunterRenderer(EntityRendererProvider.Context context) {
        super(context, "hunter");
    }

    @Override
    public AdvancedHunterRenderState createRenderState() {
        return new AdvancedHunterRenderState();
    }

    @Override
    public void extractRenderState(T entity, AdvancedHunterRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    public static class AdvancedHunterRenderState extends AvatarLikeRenderState {

    }
}
