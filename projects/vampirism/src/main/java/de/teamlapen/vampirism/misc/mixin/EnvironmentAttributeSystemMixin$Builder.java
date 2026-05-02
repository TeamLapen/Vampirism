package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.world.attributes.EnvironmentLevelAttributeModificator;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnvironmentAttributeSystem.Builder.class)
public class EnvironmentAttributeSystemMixin$Builder {


    @Inject(method = "addDefaultLayers", at = @At("RETURN"))
    private void addVampirism(Level level, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        EnvironmentLevelAttributeModificator.addLayers((EnvironmentAttributeSystem.Builder) (Object) this, level);
    }
}
