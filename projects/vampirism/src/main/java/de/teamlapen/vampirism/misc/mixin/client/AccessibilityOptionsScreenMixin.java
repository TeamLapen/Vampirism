package de.teamlapen.vampirism.misc.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {

    @ModifyReturnValue(method = "options(Lnet/minecraft/client/Options;)[Lnet/minecraft/client/OptionInstance;", at = @At("RETURN"))
    private static OptionInstance<?>[] vampirism$options(OptionInstance<?>[] original, Options options) {
        List<OptionInstance<?>> extended = new ArrayList<>(Arrays.asList(original));
        extended.add(options.vampirism$invertedSunBlindness());
        return extended.toArray(new OptionInstance<?>[0]);
    }
}
