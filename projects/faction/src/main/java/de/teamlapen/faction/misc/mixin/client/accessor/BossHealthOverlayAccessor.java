package de.teamlapen.faction.misc.mixin.client.accessor;

import de.teamlapen.faction.misc.extensions.client.IBossHealthOverlay;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public interface BossHealthOverlayAccessor extends IBossHealthOverlay {

    @Override
    @Accessor("events")
    Map<UUID, LerpingBossEvent> getEvents();

    @Accessor("OVERLAY_BACKGROUND_SPRITES")
    static Identifier[] getOVERLAY_BACKGROUND_SPRITES() {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
