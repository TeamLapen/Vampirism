package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.client.ResourceLoadStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLoadStateTracker.class)
public class ResourceLoadStateTrackerMixin {

    @Inject(method = "finishReload", at = @At("RETURN"))
    private void updateOverlays(CallbackInfo ci) {
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).syncOverlays();
    }
}
