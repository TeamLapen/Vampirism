package de.teamlapen.vampirism.mixin.client;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mixin(BossHealthOverlay.class)
public interface BossOverlayGuiAccessor {

    @Accessor(value = "events")
    Map<UUID, LerpingBossEvent> getMapBossInfos();
}
