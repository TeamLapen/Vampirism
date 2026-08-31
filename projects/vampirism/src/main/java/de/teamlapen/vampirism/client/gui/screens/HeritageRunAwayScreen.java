package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSimpleInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class HeritageRunAwayScreen extends ConfirmScreen {
    public HeritageRunAwayScreen() {
        super(confirmed -> {
            if (confirmed) {
                VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.RUN_AWAY_FROM_HERITAGE));
            }
            Minecraft.getInstance().setScreen(null);
        }, Component.translatable("gui.vampirism.heritage_run_away.title"), Component.translatable("gui.vampirism.heritage_run_away.warning"));
    }
}
