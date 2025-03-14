package de.teamlapen.vampirism.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.teamlapen.vampirism.api.items.ICapeItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractClientPlayer.class, priority = 1200)
public class AbstractClientPlayerMixin {

    @ModifyReturnValue(method = "getSkin", at = @At("RETURN"))
    public PlayerSkin addCloak(PlayerSkin original) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        Item item = player.getItemBySlot(EquipmentSlot.CHEST).getItem();

        if (item instanceof ICapeItem capeItem) {
            return new PlayerSkin(original.texture(), original.textureUrl(), capeItem.getCapeTexture(), original.elytraTexture(), original.model(), original.secure());
        }
        
        return original;
    }
}
