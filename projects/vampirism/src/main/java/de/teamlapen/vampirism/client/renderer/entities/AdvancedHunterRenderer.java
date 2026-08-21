package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

public class AdvancedHunterRenderer<T extends Mob> extends SupporterBasedRenderer<T, AdvancedHunterRenderer.AdvancedHunterRenderState> {

    public AdvancedHunterRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mod("hunter"), VIdentifier.mod("textures/entity/advanced/hunter.png")), null, null, PlayerModelType.WIDE, false));
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
