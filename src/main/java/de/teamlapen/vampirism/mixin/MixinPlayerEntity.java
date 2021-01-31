package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(method = "func_234570_el_", at = @At("RETURN"))
    private static void registerCustomAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
        cir.getReturnValue().createMutableAttribute(ModAttributes.sundamage).createMutableAttribute(ModAttributes.blood_exhaustion).createMutableAttribute(ModAttributes.bite_damage);
    }
}
