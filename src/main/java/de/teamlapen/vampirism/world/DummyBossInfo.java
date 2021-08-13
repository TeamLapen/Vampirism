package de.teamlapen.vampirism.world;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.util.UUID;

import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;

public class DummyBossInfo extends LerpingBossEvent {
    public DummyBossInfo(UUID uniqueIdIn, Component nameIn) {
        super(uniqueIdIn,nameIn, 0,BossBarColor.WHITE, BossBarOverlay.PROGRESS, false, false, false);
    }

    public static class DummyBossInfo2 extends BossEvent {
        public DummyBossInfo2(UUID uniqueIdIn, Component nameIn) {
            super(uniqueIdIn, nameIn, BossBarColor.WHITE, BossBarOverlay.PROGRESS);
        }
    }
}
