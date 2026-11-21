package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundRunAwayPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Asks before running away from one's heritage with the own heritage book on the lectern at the given position.
 */
public class HeritageRunAwayScreen extends ConfirmScreen {
    public HeritageRunAwayScreen(BlockPos lecternPos) {
        super(confirmed -> {
            if (confirmed) {
                VampirismMod.proxy.sendToServer(new ServerboundRunAwayPacket(lecternPos));
            }
            Minecraft.getInstance().setScreen(null);
        }, Component.translatable("gui.vampirism.heritage_run_away.title"), Component.translatable("gui.vampirism.heritage_run_away.warning"));
    }
}
