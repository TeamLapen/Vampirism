package de.teamlapen.vampirism.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.client.accessor.BossHealthOverlayAccessor;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Final
    @Shadow
    public Gui gui;
    @Shadow @Nullable public LocalPlayer player;

    @Inject(method="getSituationalMusic", at=@At(value="INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"), cancellable = true)
    private void getSituationalMusicVampirism(CallbackInfoReturnable<Music> cir){
        Map<UUID, LerpingBossEvent> events = ((BossHealthOverlayAccessor) this.gui.getBossOverlay()).getEvents();
        events.values().stream().map(s -> ((ClientProxy) VampirismMod.proxy).getBossEventSound(s.getId())).filter(Objects::nonNull).findFirst().ifPresent(s -> cir.setReturnValue(ModSounds.getMusic(s)));
    }

    @ModifyExpressionValue(method = "shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value ="INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z"))
    private boolean vampireGlowing(boolean original, Entity entity) {
//        if(player != null && Helper.isHunter(player) && HunterPlayer.get(player).getActionHandler().isActionActive(HunterActions.AWARENESS_HUNTER.get())) {
//            if (Helper.isVampire(entity) && entity.distanceToSqr(player) < 256) {
//                return false;
//            }
//        }
        return original;
    }
}
