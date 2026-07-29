package de.teamlapen.vampirism.misc.mixin.client;

import de.teamlapen.vampirism.misc.extension.client.IOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin implements IOptions {

    @Unique
    private final OptionInstance<Boolean> vampirism$invertedSunBlindness = OptionInstance.createBoolean("options.vampirism.invertedSunBlindness", OptionInstance.cachedConstantTooltip(Component.translatable("options.vampirism.invertedSunBlindness.tooltip")), false);

    @Inject(method = "processOptions(Lnet/minecraft/client/Options$FieldAccess;)V", at = @At("TAIL"))
    public void vampirism$processOptions(Options.FieldAccess access, CallbackInfo ci) {
        access.process("vampirism$invertedSunBlindness", this.vampirism$invertedSunBlindness);
    }

    @Override
    public OptionInstance<Boolean> vampirism$invertedSunBlindness() {
        return this.vampirism$invertedSunBlindness;
    }
}
