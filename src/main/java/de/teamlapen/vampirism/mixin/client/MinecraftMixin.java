package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.blockentity.MotherBlockEntity;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method="getSituationalMusic", at=@At(value="INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"), cancellable = true)
    private void getSituationalMusicVampirism(CallbackInfoReturnable<Music> cir){
        if(MotherBlockEntity.IS_A_MOTHER_LOADED_UNRELIABLE && Minecraft.getInstance().gui.getBossOverlay().shouldPlayMusic()){
            cir.setReturnValue(ModSounds.getMotherMusic());
        }
    }
}
