package de.teamlapen.vampirism.misc.mixin.client;

import de.teamlapen.vampirism.misc.extension.client.IOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin implements IOptions {

    @Unique
    private final OptionInstance<Double> vampirism$sunBlindnessIntensity = new OptionInstance<>("options.vampirism.sunBlindnessIntensity", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, _ -> {});

    @Inject(method = "processOptions(Lnet/minecraft/client/Options$FieldAccess;)V", at = @At("TAIL"))
    public void vampirism$processOptions(Options.FieldAccess access, CallbackInfo ci) {
        access.process("vampirism$sunBlindnessIntensity", this.vampirism$sunBlindnessIntensity);
    }

    @Override
    public OptionInstance<Double> vampirism$sunBlindnessIntensity() {
        return this.vampirism$sunBlindnessIntensity;
    }
}
