package de.teamlapen.faction.misc.mixin;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.util.ConfigComponent;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComponentSerialization.class)
public class ComponentSerializationMixin {

    @Inject(method = "bootstrap", at = @At(value = "RETURN"))
    private static void add(ExtraCodecs.LateBoundIdMapper<String, MapCodec<? extends ComponentContents>> contentTypes, CallbackInfo ci) {
        contentTypes.put(FIdentifier.modString("config"), ConfigComponent.MAP_CODEC);
    }
}
