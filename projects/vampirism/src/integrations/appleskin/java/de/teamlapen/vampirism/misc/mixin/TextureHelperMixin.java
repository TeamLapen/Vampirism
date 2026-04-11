package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.client.gui.overlay.BloodBarOverlay;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.TextureHelper;

// TODO: This does not work fully yet as the tooltip background is hardcoded and cannot be changed
@Mixin(TextureHelper.class)
public class TextureHelperMixin {

    /**
    @Inject(method = "getFoodTexture", at = @At("RETURN"), cancellable = true)
    private static void getVampirismFoodTexture(boolean isRotten, TextureHelper.FoodType type, CallbackInfoReturnable<Identifier> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && Helper.isVampire(player)) {
            cir.setReturnValue(switch (type)
            {
                case EMPTY -> BloodBarOverlay.BACKGROUND;
                case HALF -> BloodBarOverlay.QUARTER;
                case FULL -> BloodBarOverlay.HALF;
            });
        }
    }
    **/
}
