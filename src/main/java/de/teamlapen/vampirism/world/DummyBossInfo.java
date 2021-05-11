package de.teamlapen.vampirism.world;

import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

import java.util.UUID;

public class DummyBossInfo extends ClientBossInfo {
    public DummyBossInfo(UUID uniqueIdIn, ITextComponent nameIn) {
        super(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, new DummyBossInfo2(uniqueIdIn, nameIn)));
    }

    public static class DummyBossInfo2 extends BossInfo {
        public DummyBossInfo2(UUID uniqueIdIn, ITextComponent nameIn) {
            super(uniqueIdIn, nameIn, Color.WHITE, Overlay.PROGRESS);
        }
    }
}
