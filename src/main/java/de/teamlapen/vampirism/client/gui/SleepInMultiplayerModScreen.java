package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class SleepInMultiplayerModScreen extends InBedChatScreen {

    private final String leaveText;

    public SleepInMultiplayerModScreen(String text) {
        this.leaveText = text;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.get(0).setMessage(new TranslatableComponent(leaveText));
    }
}
