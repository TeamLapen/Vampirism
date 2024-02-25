package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public interface BossHealthOverlayAccessor {

    @Accessor("events")
    Map<UUID, LerpingBossEvent> getEvents();

    @Accessor()
    static ResourceLocation[] getOVERLAY_BACKGROUND_SPRITES() {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
