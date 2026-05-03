package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.tags.ModItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(at = @At("HEAD"), method = "canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
    private static void canHunterWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.FEET).is(ModItemTags.POWDER_SNOW_WALKABLE_BOOTS)) {
            cir.setReturnValue(true);
        }
    }
}
