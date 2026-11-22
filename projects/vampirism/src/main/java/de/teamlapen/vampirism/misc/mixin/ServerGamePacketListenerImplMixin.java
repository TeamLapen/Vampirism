package de.teamlapen.vampirism.misc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    /**
     * Piercing weapons (e.g. spears) attack via the STAB action instead of {@link net.minecraft.world.entity.player.Player#attack(net.minecraft.world.entity.Entity)},
     * so {@link net.neoforged.neoforge.event.entity.player.AttackEntityEvent} is not fired. Prevent stab attacks (and the lunge enchantment) while in bat/mist form.
     */
    @ModifyExpressionValue(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;cannotAttackWithItem(Lnet/minecraft/world/item/ItemStack;I)Z"))
    private boolean preventStabInForm(boolean original) {
        return original || VampirePlayer.get(this.player).getSkillProperties().hasForm();
    }
}
