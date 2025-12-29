package de.teamlapen.vampirism.misc.mixin.client;

import de.teamlapen.vampirism.misc.extension.client.IHunterPlayerState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class HunterAvatarRenderStateMixin implements IHunterPlayerState {

    @Unique
    private boolean vampirism$hunter$fullHunterCoat;
    @Unique
    private boolean vampirism$hunter$disguised;

    @Override
    public boolean vampirism$hunter$fullHunterCoat() {
        return this.vampirism$hunter$fullHunterCoat;
    }

    @Override
    public boolean vampirism$hunter$isDisguised() {
        return this.vampirism$hunter$disguised;
    }

    @Override
    public void vampirism$hunter$setFullHunterCoat(boolean fullHunterCoat) {
        this.vampirism$hunter$fullHunterCoat = fullHunterCoat;
    }

    @Override
    public void vampirism$hunter$setDisguised(boolean disguised) {
        this.vampirism$hunter$disguised = disguised;
    }
}
