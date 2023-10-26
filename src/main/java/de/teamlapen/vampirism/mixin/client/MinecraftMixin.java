package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Final
    @Shadow
    public Gui gui;
    @Inject(method="getSituationalMusic", at=@At(value="INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"), cancellable = true)
    private void getSituationalMusicVampirism(CallbackInfoReturnable<Music> cir){
        Map<UUID, LerpingBossEvent> events = ((BossHealthOverlayAccessor) this.gui.getBossOverlay()).getEvents();
        events.values().stream().map(s -> ((ClientProxy) VampirismMod.proxy).getBossEventSound(s.getId())).filter(Objects::nonNull).findFirst().ifPresent(s -> cir.setReturnValue(ModSounds.getMusic(s)));
    }
}
