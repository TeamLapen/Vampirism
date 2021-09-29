package de.teamlapen.vampirism.world;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * used as dummy boss event that can be passed to {@link RenderGameOverlayEvent.BossInfo}
 */
public class DummyBossInfo extends LerpingBossEvent {

    public DummyBossInfo(MultiBossEvent multiBossEvent) {
        super(multiBossEvent.getUniqueId(), multiBossEvent.getName(), 0, BossBarColor.WHITE, BossBarOverlay.PROGRESS, false, false, false);
    }
}
