package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireFangLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AdvancedVampireRenderer<TEntity extends Mob> extends SupporterBasedRenderer<TEntity, AdvancedVampireRenderer.AdvancedVampireRenderState> {

    public AdvancedVampireRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mod("vampire"), VIdentifier.mod("textures/entity/advanced/vampire.png")), null, null, PlayerModelType.WIDE, false));
        addLayer(new AdvancedVampireEyeLayer(this));
        addLayer(new AdvancedVampireFangLayer(this));
    }

    @Override
    public AdvancedVampireRenderState createRenderState() {
        return new AdvancedVampireRenderState();
    }

    @Override
    protected void extractSupporter(TEntity tEntity, AdvancedVampireRenderState state, Supporter supporter) {
        super.extractSupporter(tEntity, state, supporter);
        try {
            Map<String, String> appearance = supporter.appearance();
            if (appearance.containsKey("eyes")) {
                state.eyeTexture = VIdentifier.mod("eyes" + appearance.get("eyes"));
            }
            if (appearance.containsKey("fang")) {
                state.eyeTexture = VIdentifier.mod("fangs" + appearance.get("fangs"));
            }
        } catch (Exception ignored) {}
    }

    public static class AdvancedVampireRenderState extends AvatarLikeRenderState {

        @Nullable
        public Identifier eyeTexture;
        @Nullable
        public Identifier fangTexture;
    }
}
