package de.teamlapen.vampirism.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.common.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.common.tags.ModGameEventTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VibrationSystem.User.class)
public interface VibrationSystemUserMixin {

    @WrapOperation(method = "isValidVibration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;dampensVibrations()Z"))
    private boolean modifyDampensVibrations(Entity instance, Operation<Boolean> original, Holder<GameEvent> gameEvent) {
        return (instance instanceof IVampirismPlayer player && player.vampirism$getVampAtts().getVampSpecial().darkStalker && gameEvent.is(ModGameEventTags.DARK_STALKER_IGNORE)) || original.call(instance);
    }
}
